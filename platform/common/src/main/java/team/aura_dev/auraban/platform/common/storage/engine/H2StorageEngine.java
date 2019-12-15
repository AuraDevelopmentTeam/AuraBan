package team.aura_dev.auraban.platform.common.storage.engine;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.storage.sql.NamedPreparedStatement;
import team.aura_dev.auraban.platform.common.storage.sql.SQLStorageEngine;

@RequiredArgsConstructor
public class H2StorageEngine extends SQLStorageEngine {
  private static final String URLFormat = "jdbc:h2:%s;AUTO_SERVER=%s;DATABASE_TO_UPPER=FALSE";
  private static final int SCHEME_VERSION = 1;

  private final Path databasePath;
  private final boolean shareDatabase;

  private Connection connection = null;

  @Override
  @SneakyThrows(ClassNotFoundException.class)
  protected void connect() throws SQLException {
    if (connection != null) return;

    final String connectionURL =
        String.format(URLFormat, databasePath.toFile(), shareDatabase ? "TRUE" : "FALSE");

    AuraBanBase.logger.debug("Connecting to \"" + connectionURL + '"');

    // Make sure driver is loaded
    Class.forName("org.h2.Driver"); // This should never throw
    connection = DriverManager.getConnection(connectionURL);
  }

  @Override
  protected void createTables() throws SQLException {
    logTableCreation("table_versions");
    // table_versions
    executeUpdateQuery(
        // Table name
        "CREATE TABLE IF NOT EXISTS table_versions ( "
            // Columns
            + "name VARCHAR(128) NOT NULL, version INT NOT NULL, "
            // Keys
            + "PRIMARY KEY (name))");

    super.createTables();
  }

  @SuppressFBWarnings(
    value = "SF_SWITCH_FALLTHROUGH",
    justification = "Fallthrough behavior intended"
  )
  @Override
  protected void createTablePlayer() throws SQLException {
    switch (getTableVersion("players")) {
        // case x: // Version below
        // logTableUpgrade(tablePlayers, x);
        // upgrade queries from x to y go here
        // case y: // Version below
        // logTableUpgrade(tablePlayers, y);
        // upgrade queries from y to SCHEME_VERSION go here
        // setTableVersion("players");
      case SCHEME_VERSION: // Current version
      default: // Versions above the current version
        break;
      case -1: // Version could not be determined
        // Also logs a warning
        renameConflictingTable("players");
      case 0: // Table doesn't exist
        logTableCreation("players");
        // players
        executeUpdateQuery(
            // Table name
            "CREATE TABLE players ("
                // Columns
                + "id INT NOT NULL AUTO_INCREMENT, uuid BINARY(16) NOT NULL, name VARCHAR(16) NOT NULL, "
                // Keys
                + "PRIMARY KEY (id), UNIQUE (uuid))");
        setTableVersion("players");
    }
  }

  @SuppressFBWarnings(
    value = "SF_SWITCH_FALLTHROUGH",
    justification = "Fallthrough behavior intended"
  )
  @Override
  protected void createTableLadders() throws SQLException {
    // TODO
  }

  @SuppressFBWarnings(
    value = "SF_SWITCH_FALLTHROUGH",
    justification = "Fallthrough behavior intended"
  )
  @Override
  protected void createTableLadderSteps() throws SQLException {
    // TODO
  }

  @SuppressFBWarnings(
    value = "SF_SWITCH_FALLTHROUGH",
    justification = "Fallthrough behavior intended"
  )
  @Override
  protected void createTablePunishments() throws SQLException {
    // TODO: rename to punishments
    switch (getTableVersion("bans")) {
      case SCHEME_VERSION: // Current version
      default: // Versions above the current version
        break;
      case -1: // Version could not be determined
        // Also logs a warning
        renameConflictingTable("players");
      case 0: // Table doesn't exist
        logTableCreation("bans");
        // bans
        executeUpdateQuery(
            // Table name
            "CREATE TABLE bans ("
                // Columns
                + "id INT NOT NULL AUTO_INCREMENT, player_id INT NOT NULL, operator_id INT NOT NULL, end DATETIME NULL, reason VARCHAR(1024) NOT NULL, "
                // Keys
                + "PRIMARY KEY (id), "
                // Foreign keys
                + "FOREIGN KEY (player_id) REFERENCES players(id), FOREIGN KEY (operator_id) REFERENCES players(id));"
                // Indexes
                + "CREATE INDEX ON bans(end)");
        setTableVersion("bans");
        // bans_resolved
        executeUpdateQuery(getResolvedBanViewQuery("bans"));
        // current_bans
        executeUpdateQuery(
            // View Name
            "CREATE OR REPLACE VIEW current_bans AS "
                // Columns
                + "SELECT id, player_id, operator_id, end, reason "
                // Table
                + "FROM bans "
                // Condition
                + "WHERE (end IS NULL) OR (end > CURRENT_TIMESTAMP)");
        // current_bans_resolved
        executeUpdateQuery(getResolvedBanViewQuery("current_bans"));
    }
  }

  @SuppressFBWarnings(
    value = "SF_SWITCH_FALLTHROUGH",
    justification = "Fallthrough behavior intended"
  )
  @Override
  protected void createTablePunishmentPoints() throws SQLException {
    // TODO
  }

  @Override
  protected int getTableVersion(String tableName) throws SQLException {
    // First see if the table exists
    try (final NamedPreparedStatement exitanceStatement =
        prepareStatement(
            "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = :table LIMIT 1")) {
      exitanceStatement.setString("table", tableName);

      try (final ResultSet result = exitanceStatement.executeQuery()) {
        if (!result.next()) {
          return -1;
        }

        if (result.getInt(1) == 0) {
          return 0;
        }
      }
    }

    // Then check if we have a version for it
    try (final NamedPreparedStatement versionStatement =
        prepareStatement("SELECT version FROM table_versions WHERE name = :table LIMIT 1")) {
      versionStatement.setString("table", tableName);

      try (final ResultSet result = versionStatement.executeQuery()) {
        if (!result.next()) {
          return -1;
        }

        return result.getInt(1);
      }
    }
  }

  @Override
  protected void renameConflictingTable(String tableName) throws SQLException {
    warnAboutInvalidTable(tableName);
    executeUpdateQuery("ALTER TABLE " + tableName + " RENAME TO conflict_" + tableName + "");
  }

  private void setTableVersion(String tableName) throws SQLException {
    try (final NamedPreparedStatement statement =
        prepareStatement("MERGE INTO table_versions (name, version) VALUES (:table, :version)")) {
      statement.setString("table", tableName);
      statement.setInt("version", SCHEME_VERSION);

      statement.executeUpdate();
    }
  }

  private String getResolvedBanViewQuery(String baseTableName) {
    return // View Name
    "CREATE OR REPLACE VIEW "
        + baseTableName
        + "_resolved AS "
        // Columns
        + "SELECT "
        + baseTableName
        + ".id, player.uuid player_uuid, player.name player_name, operator.uuid operator_uuid, operator.name operator_name, end, reason "
        // Table
        + "FROM `"
        + baseTableName
        + "` "
        // Joins
        + "LEFT OUTER JOIN players player ON player.id = player_id LEFT OUTER JOIN players operator ON operator.id = operator_id";
  }

  @Override
  protected Connection getConnection() {
    return connection;
  }
}

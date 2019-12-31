package team.aura_dev.auraban.platform.common.storage.engine;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import team.aura_dev.auraban.api.player.PlayerData;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.player.PlayerDataCommon;
import team.aura_dev.auraban.platform.common.storage.sql.NamedPreparedStatement;
import team.aura_dev.auraban.platform.common.util.UuidUtils;

@RequiredArgsConstructor
public class H2StorageEngine extends SQLStorageEngine {
  protected static final String URLFormat = "jdbc:h2:%s;AUTO_SERVER=%s;DATABASE_TO_UPPER=FALSE";
  protected static final int SCHEME_VERSION = 1;

  @NonNull protected final Path databasePath;
  protected final boolean shareDatabase;

  protected Connection connection = null;

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
    createTableTableVersions();

    super.createTables();
  }

  protected void createTableTableVersions() throws SQLException {
    logTableCreation("table_versions");
    // table_versions
    executeUpdateQuery(
        // Table name
        "CREATE TABLE IF NOT EXISTS table_versions ( "
            // Columns
            + "name VARCHAR(128) NOT NULL, version INT NOT NULL, "
            // Keys
            + "PRIMARY KEY (name))");
  }

  @SuppressFBWarnings(
      value = "SF_SWITCH_FALLTHROUGH",
      justification = "Fallthrough behavior intended")
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
      justification = "Fallthrough behavior intended")
  @Override
  protected void createTableLadders() throws SQLException {
    switch (getTableVersion("ladders")) {
      case SCHEME_VERSION: // Current version
      default: // Versions above the current version
        break;
      case -1: // Version could not be determined
        // Also logs a warning
        renameConflictingTable("ladders");
      case 0: // Table doesn't exist
        logTableCreation("ladders");
        // ladders
        executeUpdateQuery(
            // Table name
            "CREATE TABLE ladders ("
                // Columns
                + "id INT NOT NULL AUTO_INCREMENT, name VARCHAR(128) NOT NULL, "
                // Keys
                + "PRIMARY KEY (id));"
                // Indexes
                + "CREATE INDEX ON ladders(name)");
        setTableVersion("ladders");
    }
  }

  @SuppressFBWarnings(
      value = "SF_SWITCH_FALLTHROUGH",
      justification = "Fallthrough behavior intended")
  @Override
  protected void createTableLadderSteps() throws SQLException {
    switch (getTableVersion("ladder_steps")) {
      case SCHEME_VERSION: // Current version
      default: // Versions above the current version
        break;
      case -1: // Version could not be determined
        // Also logs a warning
        renameConflictingTable("ladder_steps");
      case 0: // Table doesn't exist
        logTableCreation("ladder_steps");
        // ladder_steps
        executeUpdateQuery(
            // Table Name
            "CREATE TABLE ladder_steps ("
                // Columns
                + "ladder_id INT NOT NULL, ladder_points SMALLINT NOT NULL, type ENUM('warning', 'mute', 'kick', 'ban') NOT NULL, duration INT NULL, "
                // Keys
                + "PRIMARY KEY (ladder_id, ladder_points), "
                // Foreign Keys
                + "FOREIGN KEY (ladder_id) REFERENCES ladders (id))");
        setTableVersion("ladder_steps");
        // ladder_steps_resolved
        executeUpdateQuery(
            // View Name
            "CREATE OR REPLACE VIEW ladder_steps_resolved AS "
                // Columns
                + "SELECT ladders.name AS ladder_name, ladder_points, type, duration "
                // Table
                + "FROM ladder_steps "
                // Joins
                + "LEFT OUTER JOIN ladders ON ladders.id = ladder_id");
    }
  }

  @SuppressFBWarnings(
      value = "SF_SWITCH_FALLTHROUGH",
      justification = "Fallthrough behavior intended")
  @Override
  protected void createTablePunishments() throws SQLException {
    switch (getTableVersion("punishments")) {
      case SCHEME_VERSION: // Current version
      default: // Versions above the current version
        break;
      case -1: // Version could not be determined
        // Also logs a warning
        renameConflictingTable("punishments");
      case 0: // Table doesn't exist
        logTableCreation("punishments");
        // punishments
        executeUpdateQuery(
            // Table Name
            "CREATE TABLE punishments ("
                // Columns
                + "id INT UNSIGNED NOT NULL AUTO_INCREMENT, player_id INT UNSIGNED NOT NULL, operator_id INT UNSIGNED NOT NULL, type ENUM('warning', 'mute', 'kick', 'ban') NOT NULL, active BOOLEAN NOT NULL, ladder_id INT UNSIGNED NULL, ladder_points SMALLINT NULL, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, end DATETIME NULL, reason VARCHAR(1024) NOT NULL, "
                // Keys
                + "PRIMARY KEY (id), "
                // Foreign Keys
                + "FOREIGN KEY (player_id) REFERENCES players (id), FOREIGN KEY (operator_id) REFERENCES players (id), FOREIGN KEY (ladder_id) REFERENCES ladders (id));"
                // Indexes
                + "CREATE INDEX ON punishments(type); CREATE INDEX ON punishments(end)");
        setTableVersion("punishments");
        // punishments_resolved
        executeUpdateQuery(getResolvedPunishmentViewQuery("punishments", "punishments_resolved"));
        // active_punishments
        executeUpdateQuery(getActivePunishmentViewQuery("punishments", "active_punishments"));
        // active_punishments_resolved
        executeUpdateQuery(
            getResolvedPunishmentViewQuery("active_punishments", "active_punishments_resolved"));
        // warnings
        executeUpdateQuery(getPunishmentTypeViewQuery("punishments", "warnings", "warning"));
        // warnings_resolved
        executeUpdateQuery(getResolvedPunishmentViewQuery("warnings", "warnings_resolved"));
        // active_warnings
        executeUpdateQuery(getActivePunishmentViewQuery("warnings", "active_warnings"));
        // active_warnings_resolved
        executeUpdateQuery(
            getResolvedPunishmentViewQuery("active_warnings", "active_warnings_resolved"));
        // kicks
        executeUpdateQuery(getPunishmentTypeViewQuery("punishments", "kicks", "kick"));
        // kicks_resolved
        executeUpdateQuery(getResolvedPunishmentViewQuery("kicks", "kicks_resolved"));
        // mutes
        executeUpdateQuery(getPunishmentTypeViewQuery("punishments", "mutes", "mute"));
        // mutes_resolved
        executeUpdateQuery(getResolvedPunishmentViewQuery("mutes", "mutes_resolved"));
        // active_mutes
        executeUpdateQuery(getActivePunishmentViewQuery("mutes", "active_mutes"));
        // active_mutes_resolved
        executeUpdateQuery(getResolvedPunishmentViewQuery("active_mutes", "active_mutes_resolved"));
        // bans
        executeUpdateQuery(getPunishmentTypeViewQuery("punishments", "bans", "ban"));
        // bans_resolved
        executeUpdateQuery(getResolvedPunishmentViewQuery("bans", "bans_resolved"));
        // active_bans
        executeUpdateQuery(getActivePunishmentViewQuery("bans", "active_bans"));
        // active_bans_resolved
        executeUpdateQuery(getResolvedPunishmentViewQuery("active_bans", "active_bans_resolved"));
    }
  }

  @SuppressFBWarnings(
      value = "SF_SWITCH_FALLTHROUGH",
      justification = "Fallthrough behavior intended")
  @Override
  protected void createTablePunishmentPoints() throws SQLException {
    switch (getTableVersion("punishment_points")) {
      case SCHEME_VERSION: // Current version
      default: // Versions above the current version
        break;
      case -1: // Version could not be determined
        // Also logs a warning
        renameConflictingTable("punishment_points");
      case 0: // Table doesn't exist
        logTableCreation("punishment_points");
        // punishment_points
        executeUpdateQuery(
            // Table Name
            "CREATE TABLE punishment_points ("
                // Columns
                + "player_id INT UNSIGNED NOT NULL, ladder_id INT UNSIGNED NOT NULL, ladder_points SMALLINT NOT NULL, "
                // Keys
                + "PRIMARY KEY (player_id, ladder_id), "
                // Foreign Keys
                + "FOREIGN KEY (player_id) REFERENCES players (id), FOREIGN KEY (ladder_id) REFERENCES ladders (id))");
        setTableVersion("punishment_points");
        // punishment_points_resolved
        executeUpdateQuery(
            // View Name
            "CREATE OR REPLACE VIEW punishment_points_resolved AS "
                // Columns
                + "SELECT players.uuid AS player_uuid, players.name AS player_name, ladders.name AS ladder_name, ladder_points "
                // Table
                + "FROM punishment_points "
                // Joins
                + "LEFT JOIN players ON players.id = player_id LEFT JOIN ladders ON ladders.id = ladder_id");
    }
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

        final int versionNum = result.getInt(1);

        return (versionNum < 1) ? -1 : versionNum;
      }
    }
  }

  @Override
  protected void renameConflictingTable(String tableName) throws SQLException {
    warnAboutInvalidTable(tableName);
    executeUpdateQuery("ALTER TABLE " + tableName + " RENAME TO conflict_" + tableName + "");
  }

  protected void setTableVersion(String tableName) throws SQLException {
    try (final NamedPreparedStatement statement =
        prepareStatement("MERGE INTO table_versions (name, version) VALUES (:table, :version)")) {
      statement.setString("table", tableName);
      statement.setInt("version", SCHEME_VERSION);

      statement.executeUpdate();
    }
  }

  protected String getPunishmentTypeViewQuery(String baseTableName, String viewName, String type) {
    return // View Name
    "CREATE OR REPLACE VIEW "
        + viewName
        + " AS "
        // Columns
        + "SELECT id, player_id, operator_id, type, active, ladder_id, ladder_points, timestamp, end, reason "
        // Table
        + "FROM "
        + baseTableName
        + " "
        // Condition
        + "WHERE type = '"
        + type
        + "'";
  }

  protected String getActivePunishmentViewQuery(String baseTableName, String viewName) {
    return // View Name
    "CREATE OR REPLACE VIEW "
        + viewName
        + " AS "
        // Columns
        + "SELECT id, player_id, operator_id, type, active, ladder_id, ladder_points, timestamp, end, reason "
        // Table
        + "FROM "
        + baseTableName
        + " "
        // Condition
        + "WHERE ((end IS NULL) OR (end > CURRENT_TIMESTAMP)) AND active";
  }

  protected String getResolvedPunishmentViewQuery(String baseTableName, String viewName) {
    return // View Name
    "CREATE OR REPLACE VIEW "
        + viewName
        + " AS "
        // Columns
        + "SELECT "
        + baseTableName
        + ".id, player.uuid AS player_uuid, player.name AS player_name, operator.uuid AS operator_uuid, operator.name AS operator_name, type, active, ladders.name AS ladder_name, ladder_points, timestamp, end, reason "
        // Table
        + "FROM "
        + baseTableName
        + " "
        // Joins
        + "LEFT JOIN players player ON player.id = player_id LEFT JOIN players operator ON operator.id = operator_id LEFT JOIN ladders ON ladders.id = ladder_id";
  }

  @Override
  protected Connection getConnection() {
    return connection;
  }

  @Override
  protected Optional<PlayerData> loadPlayerDataSync(UUID uuid) throws SQLException {
    try (NamedPreparedStatement statement =
        prepareStatement("SELECT name FROM players WHERE uuid = :uuid LIMIT 1")) {
      statement.setBytes("uuid", UuidUtils.asBytes(uuid));

      try (ResultSet result = statement.executeQuery()) {
        if (result.next()) {
          return Optional.of(new PlayerDataCommon(uuid, result.getString("name")));
        }
      }
    }

    return Optional.empty();
  }

  @Override
  protected void updateDataSync(UUID uuid, String playerName) throws SQLException {
    try (NamedPreparedStatement statement =
        prepareStatement("MERGE INTO players (uuid, name) KEY (uuid) VALUES (:uuid, :name)")) {
      statement.setBytes("uuid", UuidUtils.asBytes(uuid));
      statement.setString("name", playerName);

      statement.executeUpdate();
    }
  }
}

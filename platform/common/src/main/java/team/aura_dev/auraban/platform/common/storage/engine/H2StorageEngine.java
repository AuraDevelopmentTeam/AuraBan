package team.aura_dev.auraban.platform.common.storage.engine;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.storage.sql.SQLStorageEngine;

@RequiredArgsConstructor
public class H2StorageEngine extends SQLStorageEngine {
  private static final String URLFormat = "jdbc:h2:%s;AUTO_SERVER=TRUE";

  private final Path databasePath;

  private Connection connection = null;

  @Override
  @SneakyThrows(ClassNotFoundException.class)
  protected void connect() throws SQLException {
    final String connectionURL = String.format(URLFormat, databasePath.toFile());

    AuraBanBase.logger.debug("Connecting to \"" + connectionURL + '"');

    // Make sure driver is loaded
    Class.forName("org.h2.Driver"); // This should never throw
    connection = DriverManager.getConnection(connectionURL);
  }

  @SuppressFBWarnings(
    value = "SF_SWITCH_FALLTHROUGH",
    justification = "Fallthrough behavior intended"
  )
  @Override
  protected void createTables() throws SQLException {
    switch (getTableVersion("players")) {
      case 1: // Current version
      default: // Versions above the current version
        break;
      case -1: // Version could not be determined
        // Also logs a warning
        renameConflictingTable("players");
      case 0: // Table doesn't exist
        executeUpdateQuery(
            "CREATE TABLE players ( ID INT NOT NULL AUTO_INCREMENT, UUID BINARY(16) NOT NULL, NAME VARCHAR(16) NOT NULL, PRIMARY KEY (ID), UNIQUE (UUID)) COMMENT 'v1' DEFAULT CHARSET UTF8");
    }
  }

  @Override
  protected int getTableVersion(String tableName) throws SQLException {
    // TODO
    return 0;
  }

  @Override
  protected void renameConflictingTable(String tableName) throws SQLException {
    warnAboutInvalidTable(tableName);
    executeUpdateQuery("ALTER TABLE " + tableName + " RENAME TO conflict_" + tableName + "");
  }

  @Override
  protected Connection getConnection() {
    return connection;
  }
}

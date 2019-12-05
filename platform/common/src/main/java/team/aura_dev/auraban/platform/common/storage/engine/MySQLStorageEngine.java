package team.aura_dev.auraban.platform.common.storage.engine;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import lombok.NonNull;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.storage.sql.NamedPreparedStatement;
import team.aura_dev.auraban.platform.common.storage.sql.SQLStorageEngine;

public class MySQLStorageEngine extends SQLStorageEngine {
  private static final String URLFormat = "jdbc:mysql://%s:%d/%s";
  private static final int SCHEME_VERSION = 1;

  // Credentials
  @NonNull private final String host;
  private final int port;
  @NonNull private final String database;
  @NonNull private final String user;
  @NonNull private final String password;
  @NonNull private final String tablePrefix;

  // Pool Settings
  private final long connectionTimeout;
  private final long maximumLifetime;
  private final int maximumPoolSize;
  private final int minimumIdle;
  @NonNull private final Map<String, String> properties;
  private final String encoding;

  // Table Names
  private final String tablePlayers;
  private final String tableBans;
  private final String tableVBansResolved;
  private final String tableVCurrentBans;
  private final String tableVCurrentBansResolved;

  // Data Source
  private HikariDataSource dataSource;

  public MySQLStorageEngine(
      @NonNull final String host,
      final int port,
      @NonNull final String database,
      @NonNull final String user,
      @NonNull final String password,
      @NonNull final String tablePrefix,
      final long connectionTimeout,
      final long maximumLifetime,
      final int maximumPoolSize,
      final int minimumIdle,
      @NonNull final Map<String, String> properties) {
    this.host = host;
    this.port = port;
    this.database = database;
    this.user = user;
    this.password = password;
    this.tablePrefix = tablePrefix;

    this.connectionTimeout = connectionTimeout;
    this.maximumLifetime = maximumLifetime;
    this.maximumPoolSize = maximumPoolSize;
    this.minimumIdle = minimumIdle;
    this.properties = properties;
    this.encoding = properties.get("characterEncoding");

    this.tablePlayers = tablePrefix + "players";
    this.tableBans = tablePrefix + "bans";
    this.tableVBansResolved = tablePrefix + "bans_resolved";
    this.tableVCurrentBans = tablePrefix + "current_bans";
    this.tableVCurrentBansResolved = tablePrefix + "current_bans_resolved";
  }

  @Override
  protected Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  @Override
  protected boolean useSafePreparedStatements() {
    return true;
  }

  @Override
  protected void connect() {
    final String connectionURL = String.format(URLFormat, host, port, database);

    AuraBanBase.logger.debug("Connecting to \"" + connectionURL + '"');

    HikariConfig config = new HikariConfig();
    config.setDriverClassName("org.mariadb.jdbc.Driver");
    config.setJdbcUrl(connectionURL);
    config.setUsername(user);
    config.setPassword(password);
    config.setConnectionTimeout(connectionTimeout);
    config.setInitializationFailTimeout(connectionTimeout);
    config.setMaxLifetime(maximumLifetime);
    config.setMaximumPoolSize(maximumPoolSize);
    config.setMinimumIdle(minimumIdle);

    config.addDataSourceProperty("cachePrepStmts", true);
    config.addDataSourceProperty("useServerPrepStmts", true);
    config.addDataSourceProperty("prepStmtCacheSize", 250);
    config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
    config.addDataSourceProperty("cacheCallableStmts", true);
    config.addDataSourceProperty("alwaysSendSetIsolation", false);
    config.addDataSourceProperty("cacheServerConfiguration", true);
    config.addDataSourceProperty("elideSetAutoCommits", true);
    config.addDataSourceProperty("useLocalSessionState", true);

    for (Map.Entry<String, String> property : properties.entrySet()) {
      config.addDataSourceProperty(property.getKey(), property.getValue());
    }

    config.setPoolName("AuraBan-MySQL-Pool");

    dataSource = new HikariDataSource(config);
  }

  @SuppressFBWarnings(
    value = "SF_SWITCH_FALLTHROUGH",
    justification = "Fallthrough behavior intended"
  )
  @Override
  protected void createTables() throws SQLException {
    switch (getTableVersion(tablePlayers)) {
        // case x: // Version below
        // logTableUpgrade(tablePlayers, x);
        // upgrade queries from x to y go here
        // case y: // Version below
        // logTableUpgrade(tablePlayers, y);
        // upgrade queries from y to SCHEME_VERSION go here
        // set table version
      case SCHEME_VERSION: // Current version
      default: // Versions above the current version
        break;
      case -1: // Version could not be determined
        // Also logs a warning
        renameConflictingTable(tablePlayers);
      case 0: // Table doesn't exist
        // players
        executeUpdateQuery(
            // Table name
            "CREATE TABLE `"
                + tablePlayers
                // Columns
                + "` ( `id` INT UNSIGNED NOT NULL AUTO_INCREMENT , `uuid` BINARY(16) NOT NULL , `name` VARCHAR(16) NOT NULL , "
                // Keys
                + "PRIMARY KEY (`id`), UNIQUE (`uuid`)"
                // Comment and Encoding
                + ") COMMENT = 'v"
                + SCHEME_VERSION
                + "' DEFAULT CHARSET = "
                + encoding);
    }

    switch (getTableVersion(tableBans)) {
      case SCHEME_VERSION: // Current version
      default: // Versions above the current version
        break;
      case -1: // Version could not be determined
        // Also logs a warning
        renameConflictingTable(tableBans);
      case 0: // Table doesn't exist
        // bans
        executeUpdateQuery(
            // Table name
            "CREATE TABLE `"
                + tableBans
                // Columns
                + "` ( `id` INT UNSIGNED NOT NULL AUTO_INCREMENT , `player_id` INT UNSIGNED NOT NULL , `operator_id` INT UNSIGNED NOT NULL , `end` DATETIME NULL , `reason` TEXT NOT NULL , "
                // Keys
                + "PRIMARY KEY (`id`), INDEX (`player_id`), INDEX (`end`) , "
                // Foreign Keys
                + "FOREIGN KEY (`player_id`) REFERENCES `"
                + tablePlayers
                + "` (`id`) , FOREIGN KEY (`operator_id`) REFERENCES `"
                + tablePlayers
                + "` (`id`)"
                // Comment and Encoding
                + ") COMMENT = 'v"
                + SCHEME_VERSION
                + "' DEFAULT CHARSET = "
                + encoding);
        // bans_resolved
        executeUpdateQuery(getResolvedBanViewQuery(tableBans, tableVBansResolved));
        // current_bans
        executeUpdateQuery(
            // View name
            "CREATE OR REPLACE VIEW `"
                + tableVCurrentBans
                + "` AS "
                // Columns
                + "SELECT `id` , `player_id` , `operator_id` , `end` , `reason` "
                // Table
                + "FROM `auraban_bans`"
                // Condition
                + " WHERE (`end` IS NULL) OR (`end` > NOW())");
        // current_bans_resolved
        executeUpdateQuery(getResolvedBanViewQuery(tableVCurrentBans, tableVCurrentBansResolved));
    }
  }

  @Override
  protected int getTableVersion(String tableName) throws SQLException {
    try (final NamedPreparedStatement statement =
        prepareStatement(
            "SELECT `table_comment` FROM `information_schema`.`tables` WHERE `table_schema` = :database AND `table_name` = :table LIMIT 1")) {
      statement.setString("database", database);
      statement.setString("table", tableName);

      try (final ResultSet result = statement.executeQuery()) {
        if (!result.next()) {
          return 0;
        }

        final String version = result.getString(1);

        if (!version.isEmpty() && (version.charAt(0) == 'v')) {
          return Integer.parseInt(version.substring(1));
        }
      }
    } catch (NumberFormatException e) {
      // Ignore and return error value
    }

    return -1;
  }

  @Override
  protected void renameConflictingTable(String tableName) throws SQLException {
    warnAboutInvalidTable(tableName);
    executeUpdateQuery("RENAME TABLE `" + tableName + "` TO `conflict_" + tableName + "`");
  }

  private String getResolvedBanViewQuery(String baseTableName, String viewName) {
    return // View name
    "CREATE OR REPLACE VIEW `"
        + viewName
        + "` AS "
        // Columns
        + "SELECT `"
        + baseTableName
        + "`.`id` , `player`.`uuid` AS `player_uuid` , `player`.`name` AS `player_name` , `operator`.`uuid` AS `operator_uuid` , `operator`.`name` AS `operator_name` , `end` , `reason` "
        // Table
        + "FROM `"
        + baseTableName
        + "`"
        // Joins
        + "LEFT JOIN `"
        + tablePlayers
        + "` AS `player` ON `player`.`id` = `player_id` LEFT JOIN `"
        + tablePlayers
        + "` AS `operator` ON `operator`.`id` = `operator_id`";
  }

  @Override
  public void close() {
    if (dataSource != null) dataSource.close();
  }
}

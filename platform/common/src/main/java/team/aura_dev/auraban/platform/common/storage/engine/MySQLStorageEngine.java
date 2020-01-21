package team.aura_dev.auraban.platform.common.storage.engine;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import team.aura_dev.auraban.api.player.PlayerData;
import team.aura_dev.auraban.api.punishment.Punishment;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.player.PlayerDataCommon;
import team.aura_dev.auraban.platform.common.storage.sql.NamedPreparedStatement;
import team.aura_dev.auraban.platform.common.util.UuidUtils;

public class MySQLStorageEngine extends SQLStorageEngine {
  protected static final String URLFormat = "jdbc:mysql://%s:%d/%s";
  protected static final int SCHEME_VERSION = 1;

  // Credentials
  @NonNull protected final String host;
  protected final int port;
  @NonNull protected final String database;
  @NonNull protected final String user;
  @NonNull protected final String password;
  @NonNull protected final String tablePrefix;

  // Pool Settings
  protected final long connectionTimeout;
  protected final long maximumLifetime;
  protected final int maximumPoolSize;
  protected final int minimumIdle;
  @NonNull protected final Map<String, String> properties;
  protected final String encoding;

  // Table Names
  protected final String tablePlayers;
  protected final String tableLadders;
  protected final String tableLadderSteps;
  protected final String tableVLadderStepsResolved;
  protected final String tablePunishments;
  protected final String tableVPunishmentsResolved;
  protected final String tableVActivePunishments;
  protected final String tableVActivePunishmentsResolved;
  protected final String tableVWarnings;
  protected final String tableVWarningsResolved;
  protected final String tableVActiveWarnings;
  protected final String tableVActiveWarningsResolved;
  protected final String tableVKicks;
  protected final String tableVKicksResolved;
  protected final String tableVMutes;
  protected final String tableVMutesResolved;
  protected final String tableVActiveMutes;
  protected final String tableVActiveMutesResolved;
  protected final String tableVBans;
  protected final String tableVBansResolved;
  protected final String tableVActiveBans;
  protected final String tableVActiveBansResolved;
  protected final String tablePunishmentPoints;
  protected final String tableVPunishmentPointsResolved;

  // Data Source
  protected HikariDataSource dataSource;

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
    this.tableLadders = tablePrefix + "ladders";
    this.tableLadderSteps = tablePrefix + "ladder_steps";
    this.tableVLadderStepsResolved = tablePrefix + "ladder_steps_resolved";
    this.tablePunishments = tablePrefix + "punishments";
    this.tableVPunishmentsResolved = tablePrefix + "punishments_resolved";
    this.tableVActivePunishments = tablePrefix + "active_punishments";
    this.tableVActivePunishmentsResolved = tablePrefix + "active_punishments_resolved";
    this.tableVWarnings = tablePrefix + "warnings";
    this.tableVWarningsResolved = tablePrefix + "warnings_resolved";
    this.tableVActiveWarnings = tablePrefix + "active_warnings";
    this.tableVActiveWarningsResolved = tablePrefix + "active_warnings_resolved";
    this.tableVKicks = tablePrefix + "kicks";
    this.tableVKicksResolved = tablePrefix + "kicks_resolved";
    this.tableVMutes = tablePrefix + "mutes";
    this.tableVMutesResolved = tablePrefix + "mutes_resolved";
    this.tableVActiveMutes = tablePrefix + "active_mutes";
    this.tableVActiveMutesResolved = tablePrefix + "active_mutes_resolved";
    this.tableVBans = tablePrefix + "bans";
    this.tableVBansResolved = tablePrefix + "bans_resolved";
    this.tableVActiveBans = tablePrefix + "active_bans";
    this.tableVActiveBansResolved = tablePrefix + "active_bans_resolved";
    this.tablePunishmentPoints = tablePrefix + "punishment_points";
    this.tableVPunishmentPointsResolved = tablePrefix + "punishment_points_resolved";
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
    if (dataSource != null) return;

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
    // Convert the value from ms to s and times it by 1.5
    config.addDataSourceProperty(
        "sessionVariables", "wait_timeout=" + (maximumLifetime * 3 / 2000));

    for (Map.Entry<String, String> property : properties.entrySet()) {
      config.addDataSourceProperty(property.getKey(), property.getValue());
    }

    config.setPoolName("AuraBan-MySQL-Pool");

    dataSource = new HikariDataSource(config);
  }

  @SuppressFBWarnings(
      value = "SF_SWITCH_FALLTHROUGH",
      justification = "Fallthrough behavior intended")
  @Override
  protected void createTablePlayer() throws SQLException {
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
        logTableCreation(tablePlayers);
        // players
        executeUpdateQuery(
            // Table Name
            "CREATE TABLE `"
                + tablePlayers
                + "` ("
                // Columns
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT, `uuid` BINARY(16) NOT NULL, `name` VARCHAR(16) NOT NULL, "
                // Keys
                + "PRIMARY KEY (`id`), UNIQUE (`uuid`)"
                // Comment and Encoding
                + ") COMMENT = 'v"
                + SCHEME_VERSION
                + "' DEFAULT CHARSET = "
                + encoding);
    }
  }

  @SuppressFBWarnings(
      value = "SF_SWITCH_FALLTHROUGH",
      justification = "Fallthrough behavior intended")
  @Override
  protected void createTableLadders() throws SQLException {
    switch (getTableVersion(tableLadders)) {
      case SCHEME_VERSION: // Current version
      default: // Versions above the current version
        break;
      case -1: // Version could not be determined
        // Also logs a warning
        renameConflictingTable(tableLadders);
      case 0: // Table doesn't exist
        logTableCreation(tableLadders);
        // ladders
        executeUpdateQuery(
            // Table Name
            "CREATE TABLE `"
                + tableLadders
                + "` ("
                // Columns
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT, `name` VARCHAR(128) NOT NULL, "
                // Keys
                + "PRIMARY KEY (`id`), INDEX (`name`)"
                // Comment and Encoding
                + ") COMMENT = 'v"
                + SCHEME_VERSION
                + "' DEFAULT CHARSET = "
                + encoding);
    }
  }

  @SuppressFBWarnings(
      value = "SF_SWITCH_FALLTHROUGH",
      justification = "Fallthrough behavior intended")
  @Override
  protected void createTableLadderSteps() throws SQLException {
    switch (getTableVersion(tableLadderSteps)) {
      case SCHEME_VERSION: // Current version
      default: // Versions above the current version
        break;
      case -1: // Version could not be determined
        // Also logs a warning
        renameConflictingTable(tableLadderSteps);
      case 0: // Table doesn't exist
        logTableCreation(tableLadderSteps);
        // ladder_steps
        executeUpdateQuery(
            // Table Name
            "CREATE TABLE `"
                + tableLadderSteps
                + "` ("
                // Columns
                + "`ladder_id` INT UNSIGNED NOT NULL, `ladder_points` SMALLINT NOT NULL, `type` ENUM('warning', 'mute', 'kick', 'ban') NOT NULL, `duration` INT UNSIGNED NULL, "
                // Keys
                + "PRIMARY KEY (`ladder_id`, `ladder_points`), "
                // Foreign Keys
                + "FOREIGN KEY (`ladder_id`) REFERENCES `"
                + tableLadders
                + "` (`id`)"
                // Comment and Encoding
                + ") COMMENT = 'v"
                + SCHEME_VERSION
                + "' DEFAULT CHARSET = "
                + encoding);
        // ladder_steps_resolved
        executeUpdateQuery(
            // View Name
            "CREATE OR REPLACE VIEW `"
                + tableVLadderStepsResolved
                + "` AS "
                // Columns
                + "SELECT `ladders`.`name` AS `ladder_name`, `ladder_points`, `type`, `duration` "
                // Table
                + "FROM `"
                + tableLadderSteps
                + "` "
                // Joins
                + "LEFT JOIN `"
                + tableLadders
                + "` AS `ladders` ON `ladders`.`id` = `ladder_id`");
    }
  }

  @SuppressFBWarnings(
      value = "SF_SWITCH_FALLTHROUGH",
      justification = "Fallthrough behavior intended")
  @Override
  protected void createTablePunishments() throws SQLException {
    switch (getTableVersion(tablePunishments)) {
      case SCHEME_VERSION: // Current version
      default: // Versions above the current version
        break;
      case -1: // Version could not be determined
        // Also logs a warning
        renameConflictingTable(tablePunishments);
      case 0: // Table doesn't exist
        logTableCreation(tablePunishments);
        // punishments
        executeUpdateQuery(
            // Table Name
            "CREATE TABLE `"
                + tablePunishments
                + "` ("
                // Columns
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT, `player_id` INT UNSIGNED NOT NULL, `operator_id` INT UNSIGNED NOT NULL, `type` ENUM('warning', 'mute', 'kick', 'ban') NOT NULL, `active` BOOLEAN NOT NULL, `ladder_id` INT UNSIGNED NULL, `ladder_points` SMALLINT NULL, `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP, `end` DATETIME NULL, `reason` TEXT NOT NULL, "
                // Keys
                + "PRIMARY KEY (`id`), INDEX(`type`), INDEX (`end`), "
                // Foreign Keys
                + "FOREIGN KEY (`player_id`) REFERENCES `"
                + tablePlayers
                + "` (`id`), FOREIGN KEY (`operator_id`) REFERENCES `"
                + tablePlayers
                + "` (`id`), FOREIGN KEY (`ladder_id`) REFERENCES `"
                + tableLadders
                + "` (`id`)"
                // Comment and Encoding
                + ") COMMENT = 'v"
                + SCHEME_VERSION
                + "' DEFAULT CHARSET = "
                + encoding);
        // punishments_resolved
        executeUpdateQuery(
            getResolvedPunishmentViewQuery(tablePunishments, tableVPunishmentsResolved));
        // active_punishments
        executeUpdateQuery(getActivePunishmentViewQuery(tablePunishments, tableVActivePunishments));
        // active_punishments_resolved
        executeUpdateQuery(
            getResolvedPunishmentViewQuery(
                tableVActivePunishments, tableVActivePunishmentsResolved));
        // warnings
        executeUpdateQuery(getPunishmentTypeViewQuery(tablePunishments, tableVWarnings, "warning"));
        // warnings_resolved
        executeUpdateQuery(getResolvedPunishmentViewQuery(tableVWarnings, tableVWarningsResolved));
        // active_warnings
        executeUpdateQuery(getActivePunishmentViewQuery(tableVWarnings, tableVActiveWarnings));
        // active_warnings_resolved
        executeUpdateQuery(
            getResolvedPunishmentViewQuery(tableVActiveWarnings, tableVActiveWarningsResolved));
        // kicks
        executeUpdateQuery(getPunishmentTypeViewQuery(tablePunishments, tableVKicks, "kick"));
        // kicks_resolved
        executeUpdateQuery(getResolvedPunishmentViewQuery(tableVKicks, tableVKicksResolved));
        // mutes
        executeUpdateQuery(getPunishmentTypeViewQuery(tablePunishments, tableVMutes, "mute"));
        // mutes_resolved
        executeUpdateQuery(getResolvedPunishmentViewQuery(tableVMutes, tableVMutesResolved));
        // active_mutes
        executeUpdateQuery(getActivePunishmentViewQuery(tableVMutes, tableVActiveMutes));
        // active_mutes_resolved
        executeUpdateQuery(
            getResolvedPunishmentViewQuery(tableVActiveMutes, tableVActiveMutesResolved));
        // bans
        executeUpdateQuery(getPunishmentTypeViewQuery(tablePunishments, tableVBans, "ban"));
        // bans_resolved
        executeUpdateQuery(getResolvedPunishmentViewQuery(tableVBans, tableVBansResolved));
        // active_bans
        executeUpdateQuery(getActivePunishmentViewQuery(tableVBans, tableVActiveBans));
        // active_bans_resolved
        executeUpdateQuery(
            getResolvedPunishmentViewQuery(tableVActiveBans, tableVActiveBansResolved));
    }
  }

  @SuppressFBWarnings(
      value = "SF_SWITCH_FALLTHROUGH",
      justification = "Fallthrough behavior intended")
  @Override
  protected void createTablePunishmentPoints() throws SQLException {
    switch (getTableVersion(tablePunishmentPoints)) {
      case SCHEME_VERSION: // Current version
      default: // Versions above the current version
        break;
      case -1: // Version could not be determined
        // Also logs a warning
        renameConflictingTable(tablePunishmentPoints);
      case 0: // Table doesn't exist
        logTableCreation(tablePunishmentPoints);
        // punishment_points
        executeUpdateQuery(
            // Table Name
            "CREATE TABLE `"
                + tablePunishmentPoints
                + "` ("
                // Columns
                + "`player_id` INT UNSIGNED NOT NULL, `ladder_id` INT UNSIGNED NOT NULL, `ladder_points` SMALLINT NOT NULL, "
                // Keys
                + "PRIMARY KEY (`player_id`, `ladder_id`), "
                // Foreign Keys
                + "FOREIGN KEY (`player_id`) REFERENCES `"
                + tablePlayers
                + "` (`id`), FOREIGN KEY (`ladder_id`) REFERENCES `"
                + tableLadders
                + "` (`id`)"
                // Comment and Encoding
                + ") COMMENT = 'v"
                + SCHEME_VERSION
                + "' DEFAULT CHARSET = "
                + encoding);
        // punishment_points_resolved
        executeUpdateQuery(
            // View Name
            "CREATE OR REPLACE VIEW `"
                + tableVPunishmentPointsResolved
                + "` AS "
                // Columns
                + "SELECT `players`.`uuid` AS `player_uuid`, `players`.`name` AS `player_name`, `ladders`.`name` AS `ladder_name`, `ladder_points` "
                // Table
                + "FROM `"
                + tablePunishmentPoints
                + "` "
                // Joins
                + "LEFT JOIN `"
                + tablePlayers
                + "` AS `players` ON `players`.`id` = `player_id` LEFT JOIN `"
                + tableLadders
                + "` AS `ladders` ON `ladders`.`id` = `ladder_id`");
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
          final int versionNum = Integer.parseInt(version.substring(1));

          return (versionNum < 1) ? -1 : versionNum;
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

  protected String getPunishmentTypeViewQuery(String baseTableName, String viewName, String type) {
    return // View Name
    "CREATE OR REPLACE VIEW `"
        + viewName
        + "` AS "
        // Columns
        + "SELECT `id`, `player_id`, `operator_id`, `type`, `active`, `ladder_id`, `ladder_points`, `timestamp`, `end`, `reason` "
        // Table
        + "FROM `"
        + baseTableName
        + "` "
        // Condition
        + "WHERE `type` = '"
        + type
        + "'";
  }

  protected String getActivePunishmentViewQuery(String baseTableName, String viewName) {
    return // View Name
    "CREATE OR REPLACE VIEW `"
        + viewName
        + "` AS "
        // Columns
        + "SELECT `id`, `player_id`, `operator_id`, `type`, `active`, `ladder_id`, `ladder_points`, `timestamp`, `end`, `reason` "
        // Table
        + "FROM `"
        + baseTableName
        + "` "
        // Condition
        + "WHERE ((`end` IS NULL) OR (`end` > NOW())) AND `active`";
  }

  protected String getResolvedPunishmentViewQuery(String baseTableName, String viewName) {
    return // View Name
    "CREATE OR REPLACE VIEW `"
        + viewName
        + "` AS "
        // Columns
        + "SELECT `"
        + baseTableName
        + "`.`id`, `player`.`uuid` AS `player_uuid`, `player`.`name` AS `player_name`, `operator`.`uuid` AS `operator_uuid`, `operator`.`name` AS `operator_name`, `type`, `active`, `ladders`.`name` AS `ladder_name`, `ladder_points`, `timestamp`, `end`, `reason` "
        // Table
        + "FROM `"
        + baseTableName
        + "` "
        // Joins
        + "LEFT JOIN `"
        + tablePlayers
        + "` AS `player` ON `player`.`id` = `player_id` LEFT JOIN `"
        + tablePlayers
        + "` AS `operator` ON `operator`.`id` = `operator_id` LEFT JOIN `"
        + tableLadders
        + "` AS `ladders` ON `ladders`.`id` = `ladder_id`";
  }

  @Override
  public void close() {
    if (dataSource != null) dataSource.close();
  }

  @Override
  protected Optional<PlayerData> loadPlayerDataSync(UUID uuid) throws SQLException {
    try (NamedPreparedStatement statement =
        prepareStatement(
            "SELECT `name` FROM `" + tablePlayers + "` WHERE `uuid` = :uuid LIMIT 1")) {
      statement.setBytes("uuid", UuidUtils.asBytes(uuid));

      try (ResultSet result = statement.executeQuery()) {
        if (result.next()) {
          return Optional.of(
              new PlayerDataCommon(uuid, result.getString("name"), loadPunishmentsSync(uuid)));
        }
      }
    }

    return Optional.empty();
  }

  @Override
  protected void updateDataSync(UUID uuid, String playerName) throws SQLException {
    try (NamedPreparedStatement statement =
        prepareStatement(
            "REPALCE INTO `"
                + tablePlayers
                + "` (`uuid`, `name`) VALUES (:uuid, :name)")) {
      statement.setBytes("uuid", UuidUtils.asBytes(uuid));
      statement.setString("name", playerName);

      statement.executeUpdate();
    }
  }

  @Override
  protected Map<Integer, Punishment> loadPunishmentsSync(UUID uuid) throws SQLException {
    try (NamedPreparedStatement statement =
        prepareStatement(
            "SELECT `id`, `player_uuid`, `operator_uuid`, `type`, `active`, `timestamp`, `end`, `reason` FROM `"
                + tableVPunishmentsResolved
                + "` WHERE `player_uuid` = :uuid")) {
      statement.setBytes("uuid", UuidUtils.asBytes(uuid));

      return punishmentsFromQuery(statement);
    }
  }
}

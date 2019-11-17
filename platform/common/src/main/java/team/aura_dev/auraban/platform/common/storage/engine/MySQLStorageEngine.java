package team.aura_dev.auraban.platform.common.storage.engine;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.storage.sql.NamedPreparedStatement;
import team.aura_dev.auraban.platform.common.storage.sql.SQLStorageEngine;

@RequiredArgsConstructor
public class MySQLStorageEngine extends SQLStorageEngine {
  private static final String URLFormat = "jdbc:mysql://%s:%d/%s";

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

  // Data Source
  private HikariDataSource dataSource;

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

  @Override
  protected void createTables() {
    ;
  }

  @Override
  protected int getTableVersion(String tableName) throws SQLException {
    try (final NamedPreparedStatement statement =
        prepareStatement(
            "SELECT `table_comment` FROM `information_schema`.`tables` WHERE `table_schema` = :database AND `table_name` = :table")) {
      statement.setString("database", database);
      statement.setString("table", tableName);

      try (final ResultSet result = statement.executeQuery()) {
        final String version = result.getString(1);

        if (!version.isEmpty() && (version.charAt(0) == 'v')) {
          return Integer.parseInt(version.substring(1));
        }
      }
    } catch (NumberFormatException e) {
      // Ignore and return default value
    }

    return 0;
  }

  @Override
  public void close() {
    if (dataSource != null) dataSource.close();
  }
}

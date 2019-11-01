package team.aura_dev.auraban.platform.common.storage.engine;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;

@RequiredArgsConstructor
public class MySQLStorageEngine implements StorageEngine {
  private static final String URLFormat = "jdbc:mysql://%s:%d/%s";

  // Credentials
  private final String host;
  private final int port;
  private final String database;
  private final String user;
  private final String password;

  // Pool Settings
  private final long connectionTimeout;
  private final long maximumLifetime;
  private final int maximumPoolSize;
  private final int minimumIdle;
  private final Map<String, String> properties;

  // Data Source
  private HikariDataSource dataSource;

  @Override
  public void initialize() {
    connect();
    createTables();
  }

  private void connect() {
    final String connectionURL = String.format(URLFormat, host, port, database);

    AuraBanBase.logger.debug("Connecting to \"" + connectionURL + '"');

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(connectionURL);
    config.setUsername(user);
    config.setPassword(password);
    config.setConnectionTimeout(connectionTimeout);
    config.setMaxLifetime(maximumLifetime);
    config.setMaximumPoolSize(maximumPoolSize);
    config.setMinimumIdle(minimumIdle);
    config.addDataSourceProperty("cachePrepStmts", true);
    config.addDataSourceProperty("prepStmtCacheSize", 250);
    config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

    for (Map.Entry<String, String> property : properties.entrySet()) {
      config.addDataSourceProperty(property.getKey(), property.getValue());
    }

    config.setPoolName("AuraBan-MySQL-Pool");

    dataSource = new HikariDataSource(config);
  }

  private void createTables() {
    // TODO
  }

  @Override
  public void close() throws Exception {
    dataSource.close();
  }
}

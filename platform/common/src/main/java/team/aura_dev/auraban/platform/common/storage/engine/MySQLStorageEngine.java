package team.aura_dev.auraban.platform.common.storage.engine;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;

@RequiredArgsConstructor
public class MySQLStorageEngine implements StorageEngine {
  private static final String URLFormat = "jdbc:mysql://%s:%d/%s";

  private final String host;
  private final int port;
  private final String database;
  private final String user;
  private final String password;

  HikariDataSource dataSource;

  @Override
  public void initialize() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(String.format(URLFormat, host, port, database));
    config.setUsername(user);
    config.setPassword(password);
    config.addDataSourceProperty("cachePrepStmts", true);
    config.addDataSourceProperty("prepStmtCacheSize", 250);
    config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

    dataSource = new HikariDataSource(config);
  }

  @Override
  public void close() throws Exception {
    dataSource.close();
  }
}

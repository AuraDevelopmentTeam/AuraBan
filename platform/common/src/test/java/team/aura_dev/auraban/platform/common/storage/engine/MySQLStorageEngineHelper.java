package team.aura_dev.auraban.platform.common.storage.engine;

import java.sql.SQLException;
import java.util.Map;
import team.aura_dev.auraban.platform.common.storage.sql.NamedPreparedStatement;

/** Used to make the query methods public */
public class MySQLStorageEngineHelper extends MySQLStorageEngine {
  public MySQLStorageEngineHelper(
      String host,
      int port,
      String database,
      String user,
      String password,
      String tablePrefix,
      long connectionTimeout,
      long maximumLifetime,
      int maximumPoolSize,
      int minimumIdle,
      Map<String, String> properties) {
    super(
        host,
        port,
        database,
        user,
        password,
        tablePrefix,
        connectionTimeout,
        maximumLifetime,
        maximumPoolSize,
        minimumIdle,
        properties);
  }

  @Override
  public void connect() {
    super.connect();
  }

  @Override
  public NamedPreparedStatement prepareStatement(String query) throws SQLException {
    return super.prepareStatement(query);
  }

  @Override
  public int executeUpdateQuery(String query) throws SQLException {
    return super.executeUpdateQuery(query);
  }
}

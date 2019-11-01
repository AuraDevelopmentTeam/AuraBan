package team.aura_dev.auraban.platform.common.storage;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This helper class has been created to provide common methods to all subclasses.<br>
 * Like running a query for example.
 */
public abstract class SQLStorageEngine implements StorageEngine {
  @Override
  public final void initialize() throws SQLException {
    connect();
    createTables();
  }

  protected abstract void connect() throws SQLException;

  protected abstract void createTables() throws SQLException;

  protected abstract Connection getConnection() throws SQLException;

  @Override
  public void close() throws SQLException {
    closeConnections();
  }

  protected void closeConnections() throws SQLException {
    final Connection connection = getConnection();

    if (connection != null) {
      connection.close();
    }
  }
}

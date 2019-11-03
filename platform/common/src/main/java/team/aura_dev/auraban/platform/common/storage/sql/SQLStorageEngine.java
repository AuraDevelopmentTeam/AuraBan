package team.aura_dev.auraban.platform.common.storage.sql;

import java.sql.Connection;
import java.sql.SQLException;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;

/**
 * This helper class has been created to provide common methods to all subclasses.<br>
 * Like running a query for example.
 */
public abstract class SQLStorageEngine implements StorageEngine {
  ////////////////////////////////////////////////////////
  // Query Methods
  ////////////////////////////////////////////////////////

  protected abstract Connection getConnection() throws SQLException;

  protected boolean useSafePreparedStatements() {
    return false;
  }

  protected NamedPreparedStatement getPreparedStatement(Connection connection, String query)
      throws SQLException {
    return useSafePreparedStatements()
        ? new SafeNamedPreparedStatement(connection, query)
        : new NamedPreparedStatement(connection, query);
  }

  protected NamedPreparedStatement prepareStatement(String query) throws SQLException {
    final Connection connection = getConnection();

    return getPreparedStatement(connection, query);
  }

  protected void executeUpdateQuery(String query) throws SQLException {
    try (NamedPreparedStatement statement = prepareStatement(query)) {
      // Run the query and close the result set
      statement.executeQuery().close();
    }
  }

  ////////////////////////////////////////////////////////
  // Initialization Methods
  ////////////////////////////////////////////////////////

  @Override
  public final void initialize() throws SQLException {
    connect();
    createTables();
  }

  protected abstract void connect() throws SQLException;

  protected abstract void createTables() throws SQLException;

  ////////////////////////////////////////////////////////
  // Deinitialization Methods
  ////////////////////////////////////////////////////////

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

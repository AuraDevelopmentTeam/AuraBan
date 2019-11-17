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

  /**
   * Helper method to construct the actual {@link NamedPreparedStatement} from the connection and
   * the query.
   *
   * @param connection the connection to create the <code>NamedPreparedStatement</code> from
   * @param query query an SQL statement that may contain one or more named parameters
   * @return A <code>NamedPreparedStatement</code> ready to have the parameters set and be executed
   * @throws SQLException if a database access error occurs
   * @see #prepareStatement(String)
   * @see NamedPreparedStatement
   */
  protected NamedPreparedStatement getPreparedStatement(Connection connection, String query)
      throws SQLException {
    return useSafePreparedStatements()
        ? new SafeNamedPreparedStatement(connection, query)
        : new NamedPreparedStatement(connection, query);
  }

  /**
   * Prepares a {@link NamedPreparedStatement} from the passed query, which allows you to set named
   * parameters.
   *
   * @param query an SQL statement that may contain one or more named parameters
   * @return A <code>NamedPreparedStatement</code> ready to have the parameters set and be executed
   * @throws SQLException if a database access error occurs
   * @see NamedPreparedStatement
   */
  protected NamedPreparedStatement prepareStatement(String query) throws SQLException {
    final Connection connection = getConnection();

    return getPreparedStatement(connection, query);
  }

  /**
   * Executes the query which is not expected to select any data and instead just any query that
   * updates data.
   *
   * @param query an SQL statement
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statementsor (2) 0
   *     for SQL statements that return nothing
   * @throws SQLException if a database access error occurs or the SQL statement returns a {@link
   *     ResultSet} object
   * @see NamedPreparedStatement
   */
  protected int executeUpdateQuery(String query) throws SQLException {
    try (NamedPreparedStatement statement = prepareStatement(query)) {
      return statement.executeUpdate();
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

  protected abstract int getTableVersion(String tableName) throws SQLException;

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

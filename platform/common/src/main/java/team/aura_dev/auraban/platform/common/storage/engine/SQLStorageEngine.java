package team.aura_dev.auraban.platform.common.storage.engine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;
import team.aura_dev.auraban.platform.common.storage.sql.NamedPreparedStatement;
import team.aura_dev.auraban.platform.common.storage.sql.SafeNamedPreparedStatement;

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
  private NamedPreparedStatement getPreparedStatement(Connection connection, String query)
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
    logger.trace("SQL: {}", query);

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

    loadAndUpdatePlayerData(new UUID(0, 0), "Console");
  }

  protected abstract void connect() throws SQLException;

  protected void createTables() throws SQLException {
    createTablePlayer();
    createTableLadders();
    createTableLadderSteps();
    createTablePunishments();
    createTablePunishmentPoints();
  }

  protected abstract void createTablePlayer() throws SQLException;

  protected abstract void createTableLadders() throws SQLException;

  protected abstract void createTableLadderSteps() throws SQLException;

  protected abstract void createTablePunishments() throws SQLException;

  protected abstract void createTablePunishmentPoints() throws SQLException;

  /**
   * This method determines the version of a certain table scheme.<br>
   * It is recommended that all tables always are at the same version.
   *
   * @param tableName the name of the table
   * @return the version of the table (starting at <code>1</code>), <code>0</code> if the table
   *     doesn't exist or <code>-1</code> if the version could not be determined.
   * @throws SQLException if a database access error occurs
   */
  protected abstract int getTableVersion(String tableName) throws SQLException;

  protected abstract void renameConflictingTable(String tableName) throws SQLException;

  ////////////////////////////////////////////////////////
  // Logging Helper Methods
  ////////////////////////////////////////////////////////

  protected void warnAboutInvalidTable(String tableName) {
    logger.warn(
        "Found an already existing table of the name \""
            + tableName
            + "\" that has an unknown table scheme.");
    logger.warn("We will be renaming it to \"conflict_" + tableName + '"');
    logger.warn("Make sure nothing else tries to work with a table named \"" + tableName + '"');
  }

  protected void logTableCreation(String tableName) {
    logger.debug("Table \"" + tableName + "\" doesn't exist. Creating it now.");
  }

  protected void logTableUpgrade(String tableName, int oldScheme) {
    logTableUpgrade(tableName, oldScheme, oldScheme + 1);
  }

  protected void logTableUpgrade(String tableName, int oldScheme, int newScheme) {
    logger.debug(
        "Upgrading table \"" + tableName + "\" from v" + oldScheme + " to v" + newScheme + '.');
  }

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

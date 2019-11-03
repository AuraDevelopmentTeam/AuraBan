package team.aura_dev.auraban.platform.common.storage.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This is an implementation of {@link NamedPreparedStatement} that also automatically closes the
 * connection when being closed. Useful for HikariCP.
 */
public class SafeNamedPreparedStatement extends NamedPreparedStatement {
  protected final Connection connection;

  /**
   * Creates a SafeNamedPreparedStatement. Wraps a call to {@link
   * Connection#prepareStatement(String)}.
   *
   * @param connection the database connection
   * @param query the parameterized query
   * @throws SQLException if the statement could not be created
   */
  public SafeNamedPreparedStatement(Connection connection, String query) throws SQLException {
    super(connection, query);

    this.connection = connection;
  }

  /**
   * Creates a SafeNamedPreparedStatement. Wraps a call to {@link
   * Connection#prepareStatement(String, int)}.
   *
   * @param connection the database connection
   * @param query the parameterized query
   * @param autoGeneratedKeys a flag indicating whether auto-generated keys should be returned; one
   *     of {@link Statement#RETURN_GENERATED_KEYS} or {@link Statement#NO_GENERATED_KEYS}
   * @throws SQLException if the statement could not be created
   */
  public SafeNamedPreparedStatement(Connection connection, String query, int autoGeneratedKeys)
      throws SQLException {
    super(connection, query, autoGeneratedKeys);

    this.connection = connection;
  }

  @Override
  public void close() throws SQLException {
    if (connection != null) connection.close();

    super.close();
  }
}

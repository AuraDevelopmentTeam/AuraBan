package team.aura_dev.auraban.platform.common.storage.sql;

import com.google.common.collect.ImmutableMap;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import lombok.Getter;

/**
 * This class wraps around a {@link PreparedStatement} and allows the programmer to set parameters
 * by name instead of by index. This eliminates any confusion as to which parameter index represents
 * what. This also means that rearranging the SQL statement or adding a parameter doesn't involve
 * renumbering your indices. Code such as this:
 *
 * <pre>
 * Connection con = getConnection();
 * String query = "SELECT * FROM `my_table` WHERE `name` = ? OR `address` = ?";
 * PreparedStatement p = con.prepareStatement(query);
 * p.setString(1, "bob");
 * p.setString(2, "123 terrace ct");
 * ResultSet rs = p.executeQuery();
 * </pre>
 *
 * <p>can be replaced with:
 *
 * <pre>
 * Connection con = getConnection();
 * String query = "SELECT * FROM `my_table` WHERE `name` = :name OR `address` = :address";
 * NamedPreparedStatement p = new NamedPreparedStatement(con, query);
 * p.setString("name", "bob");
 * p.setString("address", "123 terrace ct");
 * ResultSet rs = p.executeQuery();
 * </pre>
 */
public class NamedPreparedStatement implements PreparedStatement {
  /**
   * The statement this object is wrapping.
   *
   * @return the statement
   */
  @Getter protected final PreparedStatement statement;

  /** Maps parameter names to arrays of ints which are the parameter indices. */
  protected final ImmutableMap<String, List<Integer>> indexMap;

  /**
   * Parses a query with named parameters. The parameter-index mappings are put into the map, and
   * the parsed query is returned.
   *
   * @param query query to parse
   * @param mapBuilder map builder to hold parameter-index mappings
   * @return the parsed query
   */
  private static final String parse(
      String query, ImmutableMap.Builder<String, List<Integer>> mapBuilder) {
    final Map<String, List<Integer>> helperMap = new HashMap<>();
    final int length = query.length();
    final String queryToParse = query + '\0';

    StringBuffer parsedQuery = new StringBuffer(length);
    boolean inSingleQuote = false;
    boolean inDoubleQuote = false;
    int index = 1;

    for (int i = 0; i < length; i++) {
      char c = queryToParse.charAt(i);
      char d = queryToParse.charAt(i + 1);

      if (inSingleQuote) {
        if ((c == '\'') && (d != '\'')) {
          inSingleQuote = false;
        }
      } else if (inDoubleQuote) {
        if ((c == '"') && (d != '"')) {
          inDoubleQuote = false;
        }
      } else {
        if (c == '\'') {
          inSingleQuote = true;
        } else if (c == '"') {
          inDoubleQuote = true;
        } else if ((c == ':') && Character.isJavaIdentifierStart(d)) {
          int j = i + 2;

          while ((j < length) && Character.isJavaIdentifierPart(queryToParse.charAt(j))) {
            j++;
          }

          String name = queryToParse.substring(i + 1, j);
          c = '?'; // replace the parameter with a question mark
          i += name.length(); // skip past the end if the parameter

          List<Integer> indexList = helperMap.get(name);

          if (indexList == null) {
            indexList = new LinkedList<>();
            helperMap.put(name, indexList);
          }

          indexList.add(index);
          index++;
        }
      }

      parsedQuery.append(c);
    }

    mapBuilder.putAll(helperMap);
    return parsedQuery.toString();
  }

  /**
   * Creates a NamedPreparedStatement. Wraps a call to {@link Connection#prepareStatement(String)}.
   *
   * @param connection the database connection
   * @param query the parameterized query
   * @throws SQLException if the statement could not be created
   */
  public NamedPreparedStatement(Connection connection, String query) throws SQLException {
    ImmutableMap.Builder<String, List<Integer>> mapBuilder = ImmutableMap.builder();
    String parsedQuery = parse(query, mapBuilder);

    statement = connection.prepareStatement(parsedQuery);
    indexMap = mapBuilder.build();
  }

  /**
   * Creates a NamedPreparedStatement. Wraps a call to {@link Connection#prepareStatement(String,
   * int)}.
   *
   * @param connection the database connection
   * @param query the parameterized query
   * @param autoGeneratedKeys a flag indicating whether auto-generated keys should be returned; one
   *     of {@link Statement#RETURN_GENERATED_KEYS} or {@link Statement#NO_GENERATED_KEYS}
   * @throws SQLException if the statement could not be created
   */
  public NamedPreparedStatement(Connection connection, String query, int autoGeneratedKeys)
      throws SQLException {
    ImmutableMap.Builder<String, List<Integer>> mapBuilder = ImmutableMap.builder();
    String parsedQuery = parse(query, mapBuilder);

    statement = connection.prepareStatement(parsedQuery, autoGeneratedKeys);
    indexMap = mapBuilder.build();
  }

  /**
   * Returns the indexes for a parameter.
   *
   * @param name parameter name
   * @return parameter indexes or an empty list if the parameter could not be found
   */
  protected List<Integer> getIndexes(String name) {
    List<Integer> indexes = indexMap.get(name);

    if (indexes == null) {
      return Collections.emptyList();
    }

    return indexes;
  }

  /**
   * Sets a parameter.
   *
   * @param name parameter name
   * @param value parameter value
   * @throws SQLException if an error occurred
   * @see PreparedStatement#setObject(int, java.lang.Object)
   */
  public void setObject(String name, Object value) throws SQLException {
    for (int index : getIndexes(name)) {
      setObject(index, value);
    }
  }

  /**
   * Sets a parameter.
   *
   * @param name parameter name
   * @param value parameter value
   * @throws SQLException if an error occurred
   * @see PreparedStatement#setString(int, java.lang.String)
   */
  public void setString(String name, String value) throws SQLException {
    for (int index : getIndexes(name)) {
      setString(index, value);
    }
  }

  /**
   * Sets a parameter.
   *
   * @param name parameter name
   * @param value parameter value
   * @throws SQLException if an error occurred
   * @see PreparedStatement#setTimestamp(int, java.sql.Timestamp)
   */
  public void setBytes(String name, byte[] value) throws SQLException {
    for (int index : getIndexes(name)) {
      setBytes(index, value);
    }
  }

  /**
   * Sets a parameter.
   *
   * @param name parameter name
   * @param value parameter value
   * @throws SQLException if an error occurred
   * @see PreparedStatement#setBoolean(int, boolean)
   */
  public void setBoolean(String name, boolean value) throws SQLException {
    for (int index : getIndexes(name)) {
      setBoolean(index, value);
    }
  }

  /**
   * Sets a parameter.
   *
   * @param name parameter name
   * @param value parameter value
   * @throws SQLException if an error occurred
   * @see PreparedStatement#setByte(int, byte)
   */
  public void setByte(String name, byte value) throws SQLException {
    for (int index : getIndexes(name)) {
      setByte(index, value);
    }
  }

  /**
   * Sets a parameter.
   *
   * @param name parameter name
   * @param value parameter value
   * @throws SQLException if an error occurred
   * @see PreparedStatement#setShort(int, short)
   */
  public void setShort(String name, short value) throws SQLException {
    for (int index : getIndexes(name)) {
      setShort(index, value);
    }
  }

  /**
   * Sets a parameter.
   *
   * @param name parameter name
   * @param value parameter value
   * @throws SQLException if an error occurred
   * @see PreparedStatement#setInt(int, int)
   */
  public void setInt(String name, int value) throws SQLException {
    for (int index : getIndexes(name)) {
      setInt(index, value);
    }
  }

  /**
   * Sets a parameter.
   *
   * @param name parameter name
   * @param value parameter value
   * @throws SQLException if an error occurred
   * @see PreparedStatement#setInt(int, int)
   */
  public void setLong(String name, long value) throws SQLException {
    for (int index : getIndexes(name)) {
      setLong(index, value);
    }
  }

  /**
   * Sets a parameter.
   *
   * @param name parameter name
   * @param value parameter value
   * @throws SQLException if an error occurred
   * @see PreparedStatement#setFloat(int, float)
   */
  public void setFloat(String name, float value) throws SQLException {
    for (int index : getIndexes(name)) {
      setFloat(index, value);
    }
  }

  /**
   * Sets a parameter.
   *
   * @param name parameter name
   * @param value parameter value
   * @throws SQLException if an error occurred
   * @see PreparedStatement#setDouble(int, double)
   */
  public void setDouble(String name, double value) throws SQLException {
    for (int index : getIndexes(name)) {
      setDouble(index, value);
    }
  }

  /**
   * Sets a parameter.
   *
   * @param name parameter name
   * @param value parameter value
   * @throws SQLException if an error occurred
   * @see PreparedStatement#setBigDecimal(int, BigDecimal)
   */
  public void setBigDecimal(String name, BigDecimal value) throws SQLException {
    for (int index : getIndexes(name)) {
      setBigDecimal(index, value);
    }
  }

  /**
   * Sets a parameter.
   *
   * @param name parameter name
   * @param value parameter value
   * @throws SQLException if an error occurred
   * @see PreparedStatement#setTimestamp(int, Timestamp)
   */
  public void setTimestamp(String name, Timestamp value) throws SQLException {
    for (int index : getIndexes(name)) {
      setTimestamp(index, value);
    }
  }

  /**
   * Sets a parameter.
   *
   * @param name parameter name
   * @param value parameter value
   * @throws SQLException if an error occurred
   * @see PreparedStatement#setTime(int, Time)
   */
  public void setTime(String name, Time value) throws SQLException {
    for (int index : getIndexes(name)) {
      setTime(index, value);
    }
  }

  /**
   * Sets a parameter.
   *
   * @param name parameter name
   * @param value parameter value
   * @throws SQLException if an error occurred
   * @see PreparedStatement#setDate(int, Date)
   */
  public void setDate(String name, Date value) throws SQLException {
    for (int index : getIndexes(name)) {
      setDate(index, value);
    }
  }

  @Override
  public void close() throws SQLException {
    statement.close();
  }

  @Generated
  protected boolean isDelegateWrapper(Class<?> iface) {
    return iface.isInstance(statement);
  }

  @Override
  @Generated
  public <T> T unwrap(Class<T> iface) throws SQLException {
    try {
      if (isDelegateWrapper(iface)) {
        return iface.cast(statement);
      } else if (statement.isWrapperFor(iface)) {
        return statement.unwrap(iface);
      } else {
        throw new SQLException("The receiver is not a wrapper for " + iface.getName());
      }
    } catch (SQLException e) {
      throw e;
    } catch (Exception e) {
      throw new SQLException(
          "The receiver is not a wrapper and does not implement the interface", e);
    }
  }

  @Override
  @Generated
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return isDelegateWrapper(iface) || statement.isWrapperFor(iface);
  }

  @Override
  @Generated
  public ResultSet executeQuery() throws SQLException {
    return statement.executeQuery();
  }

  @Override
  @Generated
  public int executeUpdate() throws SQLException {
    return statement.executeUpdate();
  }

  @Override
  @Generated
  public void setNull(int paramInt1, int paramInt2) throws SQLException {
    statement.setNull(paramInt1, paramInt2);
  }

  @Override
  @Generated
  public ResultSet executeQuery(String paramString) throws SQLException {
    return statement.executeQuery(paramString);
  }

  @Override
  @Generated
  public void setBoolean(int paramInt, boolean paramBoolean) throws SQLException {
    statement.setBoolean(paramInt, paramBoolean);
  }

  @Override
  @Generated
  public int executeUpdate(String paramString) throws SQLException {
    return statement.executeUpdate(paramString);
  }

  @Override
  @Generated
  public void setByte(int paramInt, byte paramByte) throws SQLException {
    statement.setByte(paramInt, paramByte);
  }

  @Override
  @Generated
  public void setShort(int paramInt, short paramShort) throws SQLException {
    statement.setShort(paramInt, paramShort);
  }

  @Override
  @Generated
  public int getMaxFieldSize() throws SQLException {
    return statement.getMaxFieldSize();
  }

  @Override
  @Generated
  public void setInt(int paramInt1, int paramInt2) throws SQLException {
    statement.setInt(paramInt1, paramInt2);
  }

  @Override
  @Generated
  public void setMaxFieldSize(int paramInt) throws SQLException {
    statement.setMaxFieldSize(paramInt);
  }

  @Override
  @Generated
  public void setLong(int paramInt, long paramLong) throws SQLException {
    statement.setLong(paramInt, paramLong);
  }

  @Override
  @Generated
  public int getMaxRows() throws SQLException {
    return statement.getMaxRows();
  }

  @Override
  @Generated
  public void setMaxRows(int paramInt) throws SQLException {
    statement.setMaxRows(paramInt);
  }

  @Override
  @Generated
  public void setFloat(int paramInt, float paramFloat) throws SQLException {
    statement.setFloat(paramInt, paramFloat);
  }

  @Override
  @Generated
  public void setEscapeProcessing(boolean paramBoolean) throws SQLException {
    statement.setEscapeProcessing(paramBoolean);
  }

  @Override
  @Generated
  public void setDouble(int paramInt, double paramDouble) throws SQLException {
    statement.setDouble(paramInt, paramDouble);
  }

  @Override
  @Generated
  public int getQueryTimeout() throws SQLException {
    return statement.getQueryTimeout();
  }

  @Override
  @Generated
  public void setBigDecimal(int paramInt, BigDecimal paramBigDecimal) throws SQLException {
    statement.setBigDecimal(paramInt, paramBigDecimal);
  }

  @Override
  @Generated
  public void setQueryTimeout(int paramInt) throws SQLException {
    statement.setQueryTimeout(paramInt);
  }

  @Override
  @Generated
  public void setString(int paramInt, String paramString) throws SQLException {
    statement.setString(paramInt, paramString);
  }

  @Override
  @Generated
  public void cancel() throws SQLException {
    statement.cancel();
  }

  @Override
  @Generated
  public SQLWarning getWarnings() throws SQLException {
    return statement.getWarnings();
  }

  @Override
  @Generated
  public void setBytes(int paramInt, byte[] paramArrayOfByte) throws SQLException {
    statement.setBytes(paramInt, paramArrayOfByte);
  }

  @Override
  @Generated
  public void clearWarnings() throws SQLException {
    statement.clearWarnings();
  }

  @Override
  @Generated
  public void setDate(int paramInt, Date paramDate) throws SQLException {
    statement.setDate(paramInt, paramDate);
  }

  @Override
  @Generated
  public void setCursorName(String paramString) throws SQLException {
    statement.setCursorName(paramString);
  }

  @Override
  @Generated
  public void setTime(int paramInt, Time paramTime) throws SQLException {
    statement.setTime(paramInt, paramTime);
  }

  @Override
  @Generated
  public boolean execute(String paramString) throws SQLException {
    return statement.execute(paramString);
  }

  @Override
  @Generated
  public void setTimestamp(int paramInt, Timestamp paramTimestamp) throws SQLException {
    statement.setTimestamp(paramInt, paramTimestamp);
  }

  @Override
  @Generated
  public ResultSet getResultSet() throws SQLException {
    return statement.getResultSet();
  }

  @Override
  @Generated
  public int getUpdateCount() throws SQLException {
    return statement.getUpdateCount();
  }

  @Override
  @Generated
  public void setAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
      throws SQLException {
    statement.setAsciiStream(paramInt1, paramInputStream, paramInt2);
  }

  @Override
  @Generated
  public boolean getMoreResults() throws SQLException {
    return statement.getMoreResults();
  }

  @Override
  @Generated
  public void setFetchDirection(int paramInt) throws SQLException {
    statement.setFetchDirection(paramInt);
  }

  @Override
  @Deprecated
  @Generated
  public void setUnicodeStream(int paramInt1, InputStream paramInputStream, int paramInt2)
      throws SQLException {
    statement.setUnicodeStream(paramInt1, paramInputStream, paramInt2);
  }

  @Override
  @Generated
  public int getFetchDirection() throws SQLException {
    return statement.getFetchDirection();
  }

  @Override
  @Generated
  public void setFetchSize(int paramInt) throws SQLException {
    statement.setFetchSize(paramInt);
  }

  @Override
  @Generated
  public void setBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
      throws SQLException {
    statement.setBinaryStream(paramInt1, paramInputStream, paramInt2);
  }

  @Override
  @Generated
  public int getFetchSize() throws SQLException {
    return statement.getFetchSize();
  }

  @Override
  @Generated
  public void clearParameters() throws SQLException {
    statement.clearParameters();
  }

  @Override
  @Generated
  public int getResultSetConcurrency() throws SQLException {
    return statement.getResultSetConcurrency();
  }

  @Override
  @Generated
  public void setObject(int paramInt1, Object paramObject, int paramInt2) throws SQLException {
    statement.setObject(paramInt1, paramObject, paramInt2);
  }

  @Override
  @Generated
  public int getResultSetType() throws SQLException {
    return statement.getResultSetType();
  }

  @Override
  @Generated
  public void addBatch(String paramString) throws SQLException {
    statement.addBatch(paramString);
  }

  @Override
  @Generated
  public void setObject(int paramInt, Object paramObject) throws SQLException {
    statement.setObject(paramInt, paramObject);
  }

  @Override
  @Generated
  public void clearBatch() throws SQLException {
    statement.clearBatch();
  }

  @Override
  @Generated
  public boolean execute() throws SQLException {
    return statement.execute();
  }

  @Override
  @Generated
  public int[] executeBatch() throws SQLException {
    return statement.executeBatch();
  }

  @Override
  @Generated
  public void addBatch() throws SQLException {
    statement.addBatch();
  }

  @Override
  @Generated
  public Connection getConnection() throws SQLException {
    return statement.getConnection();
  }

  @Override
  @Generated
  public void setCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
      throws SQLException {
    statement.setCharacterStream(paramInt1, paramReader, paramInt2);
  }

  @Override
  @Generated
  public boolean getMoreResults(int paramInt) throws SQLException {
    return statement.getMoreResults(paramInt);
  }

  @Override
  @Generated
  public void setRef(int paramInt, Ref paramRef) throws SQLException {
    statement.setRef(paramInt, paramRef);
  }

  @Override
  @Generated
  public ResultSet getGeneratedKeys() throws SQLException {
    return statement.getGeneratedKeys();
  }

  @Override
  @Generated
  public int executeUpdate(String paramString, int paramInt) throws SQLException {
    return statement.executeUpdate(paramString, paramInt);
  }

  @Override
  @Generated
  public void setBlob(int paramInt, Blob paramBlob) throws SQLException {
    statement.setBlob(paramInt, paramBlob);
  }

  @Override
  @Generated
  public void setClob(int paramInt, Clob paramClob) throws SQLException {
    statement.setClob(paramInt, paramClob);
  }

  @Override
  @Generated
  public int executeUpdate(String paramString, int[] paramArrayOfInt) throws SQLException {
    return statement.executeUpdate(paramString, paramArrayOfInt);
  }

  @Override
  @Generated
  public void setArray(int paramInt, Array paramArray) throws SQLException {
    statement.setArray(paramInt, paramArray);
  }

  @Override
  @Generated
  public int executeUpdate(String paramString, String[] paramArrayOfString) throws SQLException {
    return statement.executeUpdate(paramString, paramArrayOfString);
  }

  @Override
  @Generated
  public ResultSetMetaData getMetaData() throws SQLException {
    return statement.getMetaData();
  }

  @Override
  @Generated
  public boolean execute(String paramString, int paramInt) throws SQLException {
    return statement.execute(paramString, paramInt);
  }

  @Override
  @Generated
  public void setDate(int paramInt, Date paramDate, Calendar paramCalendar) throws SQLException {
    statement.setDate(paramInt, paramDate, paramCalendar);
  }

  @Override
  @Generated
  public boolean execute(String paramString, int[] paramArrayOfInt) throws SQLException {
    return statement.execute(paramString, paramArrayOfInt);
  }

  @Override
  @Generated
  public void setTime(int paramInt, Time paramTime, Calendar paramCalendar) throws SQLException {
    statement.setTime(paramInt, paramTime, paramCalendar);
  }

  @Override
  @Generated
  public boolean execute(String paramString, String[] paramArrayOfString) throws SQLException {
    return statement.execute(paramString, paramArrayOfString);
  }

  @Override
  @Generated
  public void setTimestamp(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar)
      throws SQLException {
    statement.setTimestamp(paramInt, paramTimestamp, paramCalendar);
  }

  @Override
  @Generated
  public int getResultSetHoldability() throws SQLException {
    return statement.getResultSetHoldability();
  }

  @Override
  @Generated
  public boolean isClosed() throws SQLException {
    return statement.isClosed();
  }

  @Override
  @Generated
  public void setNull(int paramInt1, int paramInt2, String paramString) throws SQLException {
    statement.setNull(paramInt1, paramInt2, paramString);
  }

  @Override
  @Generated
  public void setPoolable(boolean paramBoolean) throws SQLException {
    statement.setPoolable(paramBoolean);
  }

  @Override
  @Generated
  public void setURL(int paramInt, URL paramURL) throws SQLException {
    statement.setURL(paramInt, paramURL);
  }

  @Override
  @Generated
  public boolean isPoolable() throws SQLException {
    return statement.isPoolable();
  }

  @Override
  @Generated
  public ParameterMetaData getParameterMetaData() throws SQLException {
    return statement.getParameterMetaData();
  }

  @Override
  @Generated
  public void closeOnCompletion() throws SQLException {
    statement.closeOnCompletion();
  }

  @Override
  @Generated
  public void setRowId(int paramInt, RowId paramRowId) throws SQLException {
    statement.setRowId(paramInt, paramRowId);
  }

  @Override
  @Generated
  public boolean isCloseOnCompletion() throws SQLException {
    return statement.isCloseOnCompletion();
  }

  @Override
  @Generated
  public long getLargeUpdateCount() throws SQLException {
    return statement.getLargeUpdateCount();
  }

  @Override
  @Generated
  public void setNString(int paramInt, String paramString) throws SQLException {
    statement.setNString(paramInt, paramString);
  }

  @Override
  @Generated
  public void setNCharacterStream(int paramInt, Reader paramReader, long paramLong)
      throws SQLException {
    statement.setNCharacterStream(paramInt, paramReader, paramLong);
  }

  @Override
  @Generated
  public void setLargeMaxRows(long paramLong) throws SQLException {
    statement.setLargeMaxRows(paramLong);
  }

  @Override
  @Generated
  public void setNClob(int paramInt, NClob paramNClob) throws SQLException {
    statement.setNClob(paramInt, paramNClob);
  }

  @Override
  @Generated
  public void setClob(int paramInt, Reader paramReader, long paramLong) throws SQLException {
    statement.setClob(paramInt, paramReader, paramLong);
  }

  @Override
  @Generated
  public long getLargeMaxRows() throws SQLException {
    return statement.getLargeMaxRows();
  }

  @Override
  @Generated
  public long[] executeLargeBatch() throws SQLException {
    return statement.executeLargeBatch();
  }

  @Override
  @Generated
  public void setBlob(int paramInt, InputStream paramInputStream, long paramLong)
      throws SQLException {
    statement.setBlob(paramInt, paramInputStream, paramLong);
  }

  @Override
  @Generated
  public void setNClob(int paramInt, Reader paramReader, long paramLong) throws SQLException {
    statement.setNClob(paramInt, paramReader, paramLong);
  }

  @Override
  @Generated
  public long executeLargeUpdate(String paramString) throws SQLException {
    return statement.executeLargeUpdate(paramString);
  }

  @Override
  @Generated
  public void setSQLXML(int paramInt, SQLXML paramSQLXML) throws SQLException {
    statement.setSQLXML(paramInt, paramSQLXML);
  }

  @Override
  @Generated
  public long executeLargeUpdate(String paramString, int paramInt) throws SQLException {
    return statement.executeLargeUpdate(paramString, paramInt);
  }

  @Override
  @Generated
  public void setObject(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
      throws SQLException {
    statement.setObject(paramInt1, paramObject, paramInt2, paramInt3);
  }

  @Override
  @Generated
  public void setAsciiStream(int paramInt, InputStream paramInputStream, long paramLong)
      throws SQLException {
    statement.setAsciiStream(paramInt, paramInputStream, paramLong);
  }

  @Override
  @Generated
  public long executeLargeUpdate(String paramString, int[] paramArrayOfInt) throws SQLException {
    return statement.executeLargeUpdate(paramString, paramArrayOfInt);
  }

  @Override
  @Generated
  public void setBinaryStream(int paramInt, InputStream paramInputStream, long paramLong)
      throws SQLException {
    statement.setBinaryStream(paramInt, paramInputStream, paramLong);
  }

  @Override
  @Generated
  public long executeLargeUpdate(String paramString, String[] paramArrayOfString)
      throws SQLException {
    return statement.executeLargeUpdate(paramString, paramArrayOfString);
  }

  @Override
  @Generated
  public void setCharacterStream(int paramInt, Reader paramReader, long paramLong)
      throws SQLException {
    statement.setCharacterStream(paramInt, paramReader, paramLong);
  }

  @Override
  @Generated
  public void setAsciiStream(int paramInt, InputStream paramInputStream) throws SQLException {
    statement.setAsciiStream(paramInt, paramInputStream);
  }

  @Override
  @Generated
  public void setBinaryStream(int paramInt, InputStream paramInputStream) throws SQLException {
    statement.setBinaryStream(paramInt, paramInputStream);
  }

  @Override
  @Generated
  public void setCharacterStream(int paramInt, Reader paramReader) throws SQLException {
    statement.setCharacterStream(paramInt, paramReader);
  }

  @Override
  @Generated
  public void setNCharacterStream(int paramInt, Reader paramReader) throws SQLException {
    statement.setNCharacterStream(paramInt, paramReader);
  }

  @Override
  @Generated
  public void setClob(int paramInt, Reader paramReader) throws SQLException {
    statement.setClob(paramInt, paramReader);
  }

  @Override
  @Generated
  public void setBlob(int paramInt, InputStream paramInputStream) throws SQLException {
    statement.setBlob(paramInt, paramInputStream);
  }

  @Override
  @Generated
  public void setNClob(int paramInt, Reader paramReader) throws SQLException {
    statement.setNClob(paramInt, paramReader);
  }

  @Override
  @Generated
  public void setObject(int paramInt1, Object paramObject, SQLType paramSQLType, int paramInt2)
      throws SQLException {
    statement.setObject(paramInt1, paramObject, paramSQLType, paramInt2);
  }

  @Override
  @Generated
  public void setObject(int paramInt, Object paramObject, SQLType paramSQLType)
      throws SQLException {
    statement.setObject(paramInt, paramObject, paramSQLType);
  }

  @Override
  @Generated
  public long executeLargeUpdate() throws SQLException {
    return statement.executeLargeUpdate();
  }
}

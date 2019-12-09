package team.aura_dev.auraban.platform.common.storage.sql;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import team.aura_dev.auraban.platform.common.storage.engine.MySQLStorageEngine;
import team.aura_dev.auraban.platform.common.storage.engine.TestDatabase;
import team.aura_dev.auraban.platform.common.util.UuidUtils;

public class NamedPreparedStatementTest {
  private static final int REPEATS = 1000;

  private static final TestDatabase testDatabase = new TestDatabase();
  private static final String name1 = "obj1";
  private static final String name2 = "obj2";
  private static final String str1 = UUID.randomUUID().toString() + '\'';
  private static final String str2 = UUID.randomUUID().toString() + '"';
  private static final String string1 = "string1";
  private static final String string2 = "string2";
  private static final String query =
      "SELECT '"
          + str1.replace("'", "''")
          + "' AS `"
          + string1
          + "`, \""
          + str2.replace("\"", "\"\"")
          + "\" AS `"
          + string2
          + "`, :"
          + name1
          + " AS "
          + name1
          + ", :"
          + name2
          + " AS "
          + name2;

  private MySQLStorageEngine database;

  @SuppressWarnings("deprecation")
  private static Time getTime(UUID uuid) {
    IntBuffer intBuf =
        ByteBuffer.wrap(UuidUtils.asBytes(uuid)).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
    int[] array = new int[intBuf.remaining()];
    intBuf.get(array);

    return new Time(Math.abs(array[0]) % 24, Math.abs(array[1]) % 60, Math.abs(array[2] % 60));
  }

  @SuppressWarnings("deprecation")
  private static Date getDate(UUID uuid) {
    IntBuffer intBuf =
        ByteBuffer.wrap(UuidUtils.asBytes(uuid)).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
    int[] array = new int[intBuf.remaining()];
    intBuf.get(array);

    return new Date(
        Math.abs(array[0]) % 1000, Math.abs(array[1]) % 12, Math.abs(array[2]) % 28 + 1);
  }

  @BeforeClass
  public static void setUpBeforeClass() {
    testDatabase.startDatabase();
  }

  @AfterClass
  public static void tearDownAfterClass() {
    testDatabase.stopDatabase();
  }

  private <T> void runSetterTest(Function<UUID, T> supplier, Setter<T> setter, Getter<T> getter) {
    runSetterTest(supplier, setter, getter, Assert::assertEquals);
  }

  @SneakyThrows(SQLException.class)
  private <T> void runSetterTest(
      Function<UUID, T> supplier, Setter<T> setter, Getter<T> getter, Tester<T> tester) {
    for (int i = 0; i < REPEATS; ++i) {
      final T expected = supplier.apply(UUID.randomUUID());

      try (NamedPreparedStatement statement = database.prepareStatement(query)) {
        setter.set(statement, name1, expected);
        setter.set(statement, name2, expected);

        try (ResultSet result = statement.executeQuery()) {
          assertTrue(result.next());
          tester.assertEquals(expected, getter.get(result, name1));
          tester.assertEquals(expected, getter.get(result, name2));
          assertEquals(str1, result.getString(string1));
          assertEquals(str2, result.getString(string2));
          assertFalse(result.next());
        }
      }
    }
  }

  @Before
  public void setUp() {
    database = testDatabase.getDatabaseInstance();
  }

  @After
  public void tearDown() throws Exception {
    testDatabase.closeDatabaseInstance(database);
  }

  @Test
  public void setObjectTest() {
    runSetterTest(UUID::toString, NamedPreparedStatement::setObject, ResultSet::getObject);
  }

  @Test
  public void setStringTest() {
    runSetterTest(UUID::toString, NamedPreparedStatement::setString, ResultSet::getString);
  }

  @Test
  public void setBytesTest() {
    runSetterTest(
        UuidUtils::asBytes,
        NamedPreparedStatement::setBytes,
        ResultSet::getBytes,
        (expected, actual) -> assertArrayEquals((byte[]) expected, (byte[]) actual));
  }

  @Test
  public void setBooleanTest() {
    runSetterTest(
        uuid -> uuid.getLeastSignificantBits() > 0,
        NamedPreparedStatement::setBoolean,
        ResultSet::getBoolean);
  }

  @Test
  public void setByteTest() {
    runSetterTest(
        uuid -> (byte) uuid.getLeastSignificantBits(),
        NamedPreparedStatement::setByte,
        ResultSet::getByte);
  }

  @Test
  public void setShortTest() {
    runSetterTest(
        uuid -> (short) uuid.getLeastSignificantBits(),
        NamedPreparedStatement::setShort,
        ResultSet::getShort);
  }

  @Test
  public void setIntTest() {
    runSetterTest(
        uuid -> (int) uuid.getLeastSignificantBits(),
        NamedPreparedStatement::setInt,
        ResultSet::getInt);
  }

  @Test
  public void setLongTest() {
    runSetterTest(
        UUID::getLeastSignificantBits, NamedPreparedStatement::setLong, ResultSet::getLong);
  }

  @Test
  public void setFloatTest() {
    runSetterTest(
        uuid -> ((float) uuid.getLeastSignificantBits()) / ((float) uuid.getMostSignificantBits()),
        NamedPreparedStatement::setFloat,
        ResultSet::getFloat);
  }

  @Test
  public void setDoubleTest() throws SQLException {
    runSetterTest(
        uuid ->
            ((double) uuid.getLeastSignificantBits()) / ((double) uuid.getMostSignificantBits()),
        NamedPreparedStatement::setDouble,
        ResultSet::getDouble);
  }

  @Test
  public void setBigDecimalTest() throws SQLException {
    final MathContext context = MathContext.DECIMAL64;

    runSetterTest(
        uuid ->
            new BigDecimal(uuid.getLeastSignificantBits(), context)
                .divide(new BigDecimal(uuid.getMostSignificantBits(), context), context),
        NamedPreparedStatement::setBigDecimal,
        ResultSet::getBigDecimal);
  }

  @Test
  public void setTimestampTest() throws SQLException {
    runSetterTest(
        uuid -> Timestamp.from(Instant.ofEpochMilli((int) uuid.getLeastSignificantBits())),
        NamedPreparedStatement::setTimestamp,
        ResultSet::getTimestamp);
  }

  @Test
  public void setTimeTest() throws SQLException {
    runSetterTest(
        NamedPreparedStatementTest::getTime, NamedPreparedStatement::setTime, ResultSet::getTime);
  }

  @Test
  public void setDateTest() throws SQLException {
    runSetterTest(
        NamedPreparedStatementTest::getDate, NamedPreparedStatement::setDate, ResultSet::getDate);
  }

  @Test
  public void multipleTest() throws SQLException {
    final String query = "SELECT :" + name1 + " AS " + name1 + ", :" + name1 + " AS " + name2;

    for (int i = 0; i < REPEATS; ++i) {
      final String expected = UUID.randomUUID().toString();

      try (NamedPreparedStatement statement = database.prepareStatement(query)) {
        statement.setString(name1, expected);
        statement.setString(name2, expected);

        try (ResultSet result = statement.executeQuery()) {
          assertTrue(result.next());
          assertEquals(expected, result.getString(name1));
          assertEquals(expected, result.getString(name2));
          assertFalse(result.next());
        }
      }
    }
  }

  private static interface Setter<T> {
    void set(NamedPreparedStatement statement, String name, T value) throws SQLException;
  }

  private static interface Getter<T> {
    T get(ResultSet result, String name) throws SQLException;
  }

  private static interface Tester<T> {
    void assertEquals(T expected, T actual);
  }
}

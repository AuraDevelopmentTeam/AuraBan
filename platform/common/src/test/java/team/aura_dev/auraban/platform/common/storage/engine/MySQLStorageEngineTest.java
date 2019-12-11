package team.aura_dev.auraban.platform.common.storage.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import team.aura_dev.auraban.platform.common.config.Config;
import team.aura_dev.auraban.platform.common.storage.sql.NamedPreparedStatement;

public class MySQLStorageEngineTest {
  private static final TestDatabase testDatabase = new TestDatabase();

  @BeforeClass
  public static void setUpBeforeClass() {
    testDatabase.startDatabase();
  }

  @AfterClass
  public static void tearDownAfterClass() {
    testDatabase.stopDatabase();
  }

  @Test
  public void nullPointerTest() {
    try (MySQLStorageEngine eninge =
        new MySQLStorageEngine(null, 0, null, null, null, null, 0, 0, 0, 0, null)) {
      fail("Expected NPE");
    } catch (NullPointerException e) {
      assertEquals("host is marked @NonNull but is null", e.getMessage());
    }

    try (MySQLStorageEngine eninge =
        new MySQLStorageEngine("", 0, null, null, null, null, 0, 0, 0, 0, null)) {
      fail("Expected NPE");
    } catch (NullPointerException e) {
      assertEquals("database is marked @NonNull but is null", e.getMessage());
    }

    try (MySQLStorageEngine eninge =
        new MySQLStorageEngine("", 0, "", null, null, null, 0, 0, 0, 0, null)) {
      fail("Expected NPE");
    } catch (NullPointerException e) {
      assertEquals("user is marked @NonNull but is null", e.getMessage());
    }

    try (MySQLStorageEngine eninge =
        new MySQLStorageEngine("", 0, "", "", null, null, 0, 0, 0, 0, null)) {
      fail("Expected NPE");
    } catch (NullPointerException e) {
      assertEquals("password is marked @NonNull but is null", e.getMessage());
    }

    try (MySQLStorageEngine eninge =
        new MySQLStorageEngine("", 0, "", "", "", null, 0, 0, 0, 0, null)) {
      fail("Expected NPE");
    } catch (NullPointerException e) {
      assertEquals("tablePrefix is marked @NonNull but is null", e.getMessage());
    }

    try (MySQLStorageEngine eninge =
        new MySQLStorageEngine("", 0, "", "", "", "", 0, 0, 0, 0, null)) {
      fail("Expected NPE");
    } catch (NullPointerException e) {
      assertEquals("properties is marked @NonNull but is null", e.getMessage());
    }
  }

  @Test
  public void noConnectionTest() {
    getStorageEngine("").close();
  }

  @Test
  public void noTablePrefixTest() {
    runInitializationTest(getStorageEngine(""));
  }

  @Test
  public void tablePrefixTest() {
    runInitializationTest(getStorageEngine("auraban_"));
  }

  @Test
  public void noVersionTest() throws SQLException {
    final MySQLStorageEngineHelper engine = getStorageEngine("no_version__");

    engine.connect();

    engine.executeUpdateQuery("CREATE TABLE no_version__players (id INT)");
    engine.executeUpdateQuery("CREATE TABLE no_version__ladders (id INT)");
    engine.executeUpdateQuery("CREATE TABLE no_version__ladder_steps (id INT)");
    engine.executeUpdateQuery("CREATE TABLE no_version__punishments (id INT)");
    engine.executeUpdateQuery("CREATE TABLE no_version__punishment_points (id INT)");

    runInitializationTest(engine);
  }

  @Test
  public void invalidVersionTest() throws SQLException {
    final MySQLStorageEngineHelper engine = getStorageEngine("invalid_version__");

    engine.connect();

    engine.executeUpdateQuery("CREATE TABLE invalid_version__players (id INT) COMMENT = 'v-1'");
    engine.executeUpdateQuery("CREATE TABLE invalid_version__ladders (id INT) COMMENT = 'vX'");
    engine.executeUpdateQuery("CREATE TABLE invalid_version__ladder_steps (id INT) COMMENT = '1'");
    engine.executeUpdateQuery("CREATE TABLE invalid_version__punishments (id INT) COMMENT = '-1'");
    engine.executeUpdateQuery(
        "CREATE TABLE invalid_version__punishment_points (id INT) COMMENT = '...'");

    runInitializationTest(engine);
  }

  @Test
  public void highVersionTest() throws SQLException {
    final MySQLStorageEngineHelper engine = getStorageEngine("high_version__");

    engine.connect();

    engine.executeUpdateQuery("CREATE TABLE high_version__players (id INT) COMMENT = 'v1000'");
    engine.executeUpdateQuery("CREATE TABLE high_version__ladders (id INT) COMMENT = 'v1000'");
    engine.executeUpdateQuery("CREATE TABLE high_version__ladder_steps (id INT) COMMENT = 'v1000'");
    engine.executeUpdateQuery("CREATE TABLE high_version__punishments (id INT) COMMENT = 'v1000'");
    engine.executeUpdateQuery(
        "CREATE TABLE high_version__punishment_points (id INT) COMMENT = 'v1000'");

    runInitializationTest(engine);
  }

  private MySQLStorageEngineHelper getStorageEngine(String tablePrefix) {
    // Just use default values except tablePrefix
    final Config.Storage.MySQL.PoolSettings poolSettings = new Config.Storage.MySQL.PoolSettings();

    return new MySQLStorageEngineHelper(
        testDatabase.getHost(),
        testDatabase.getPort(),
        "test",
        "test",
        "",
        tablePrefix,
        poolSettings.getConnectionTimeout(),
        poolSettings.getMaximumLifetime(),
        poolSettings.getMaximumPoolSize(),
        poolSettings.getMinimumIdle(),
        poolSettings.getProperties());
  }

  private void runInitializationTest(MySQLStorageEngine instance) {
    try {
      instance.initialize();
    } catch (SQLException e) {
      throw new AssertionError("Error while initializing database", e);
    } finally {
      instance.close();
    }
  }

  /** Used to make the query methods public */
  private static class MySQLStorageEngineHelper extends MySQLStorageEngine {
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
}

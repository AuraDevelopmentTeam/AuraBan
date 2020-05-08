package team.aura_dev.auraban.platform.common.storage.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import team.aura_dev.auraban.api.player.PlayerData;
import team.aura_dev.auraban.platform.common.config.Config;
import team.aura_dev.auraban.platform.common.storage.sql.NamedPreparedStatement;

public class MySQLStorageEngineTest {
  private static final UUID CONSOLE_UUID = new UUID(0, 0);
  private static final UUID SPECIFIC_UUID = new UUID(1, 1);
  private static final TestMySQLDatabase testDatabase = new TestMySQLDatabase();

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
    try (MySQLStorageEngine engine =
        new MySQLStorageEngine(null, 0, null, null, null, null, 0, 0, 0, 0, null)) {
      fail("Expected NPE");
    } catch (NullPointerException e) {
      assertEquals("host is marked non-null but is null", e.getMessage());
    }

    try (MySQLStorageEngine engine =
        new MySQLStorageEngine("", 0, null, null, null, null, 0, 0, 0, 0, null)) {
      fail("Expected NPE");
    } catch (NullPointerException e) {
      assertEquals("database is marked non-null but is null", e.getMessage());
    }

    try (MySQLStorageEngine engine =
        new MySQLStorageEngine("", 0, "", null, null, null, 0, 0, 0, 0, null)) {
      fail("Expected NPE");
    } catch (NullPointerException e) {
      assertEquals("user is marked non-null but is null", e.getMessage());
    }

    try (MySQLStorageEngine engine =
        new MySQLStorageEngine("", 0, "", "", null, null, 0, 0, 0, 0, null)) {
      fail("Expected NPE");
    } catch (NullPointerException e) {
      assertEquals("password is marked non-null but is null", e.getMessage());
    }

    try (MySQLStorageEngine engine =
        new MySQLStorageEngine("", 0, "", "", "", null, 0, 0, 0, 0, null)) {
      fail("Expected NPE");
    } catch (NullPointerException e) {
      assertEquals("tablePrefix is marked non-null but is null", e.getMessage());
    }

    try (MySQLStorageEngine engine =
        new MySQLStorageEngine("", 0, "", "", "", "", 0, 0, 0, 0, null)) {
      fail("Expected NPE");
    } catch (NullPointerException e) {
      assertEquals("properties is marked non-null but is null", e.getMessage());
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
    engine.executeUpdateQuery("CREATE TABLE invalid_version__ladder_steps (id INT) COMMENT = 'v0'");
    engine.executeUpdateQuery("CREATE TABLE invalid_version__punishments (id INT) COMMENT = '-1'");
    engine.executeUpdateQuery(
        "CREATE TABLE invalid_version__punishment_points (id INT) COMMENT = '...'");

    runInitializationTest(engine);
  }

  @Test
  public void highVersionTest() throws SQLException {
    final MySQLStorageEngineHelper engine = getStorageEngine("high_version__");

    engine.connect();

    // Proper table and procedure needed because the initialization succeeds
    engine.executeUpdateQuery(
        "CREATE TABLE high_version__players (id INT AUTO_INCREMENT, uuid BINARY(16), name VARCHAR(16), PRIMARY KEY(id)) COMMENT = 'v1000'");
    engine.executeUpdateQuery(
        "CREATE PROCEDURE `high_version__procdure_update_player_data` (IN `pi_uuid` BINARY(16), IN `pi_name` VARCHAR(16)) "
            + "IF EXISTS (SELECT * FROM `high_version__players` WHERE `uuid` = pi_uuid LIMIT 1) THEN "
            + "UPDATE `high_version__players` SET `name` = pi_name WHERE `uuid` = pi_uuid; ELSE "
            + "INSERT INTO `high_version__players` (`uuid`, `name`) VALUES (pi_uuid, pi_name); END IF");
    engine.executeUpdateQuery("CREATE TABLE high_version__ladders (id INT) COMMENT = 'v1000'");
    engine.executeUpdateQuery("CREATE TABLE high_version__ladder_steps (id INT) COMMENT = 'v1000'");
    engine.executeUpdateQuery("CREATE TABLE high_version__punishments (id INT) COMMENT = 'v1000'");
    engine.executeUpdateQuery(
        "CREATE TABLE high_version__punishment_points (id INT) COMMENT = 'v1000'");

    runInitializationTest(engine);
  }

  @Test
  public void unknownUserTest() throws SQLException, InterruptedException, ExecutionException {
    final MySQLStorageEngineHelper engine = getStorageEngine("unknown_user__");

    engine.initialize();

    assertFalse(engine.loadPlayerData(SPECIFIC_UUID).get().isPresent());
  }

  @Test
  public void consoleUserTest() throws SQLException, InterruptedException, ExecutionException {
    final MySQLStorageEngineHelper engine = getStorageEngine("console_user__");

    engine.initialize();

    Optional<PlayerData> consoleUser = engine.loadPlayerData(CONSOLE_UUID).get();

    assertTrue(consoleUser.isPresent());
    assertEquals("Console", consoleUser.get().getPlayerName());
  }

  @Test
  public void specificUserTest() throws SQLException, InterruptedException, ExecutionException {
    final MySQLStorageEngineHelper engine = getStorageEngine("specific_user__");

    engine.initialize();

    // Test before adding to database.
    assertFalse(engine.loadPlayerData(SPECIFIC_UUID).get().isPresent());

    // Add user and verify
    PlayerData specificUser = engine.loadAndUpdatePlayerData(SPECIFIC_UUID, "Dummy").get();

    assertEquals("Dummy", specificUser.getPlayerName());

    // Load user again
    Optional<PlayerData> specificUserOpt = engine.loadPlayerData(SPECIFIC_UUID).get();

    assertTrue(specificUserOpt.isPresent());
    assertEquals("Dummy", specificUserOpt.get().getPlayerName());
  }

  @Test
  public void updatePlayerDataTest() throws SQLException, InterruptedException, ExecutionException {
    final MySQLStorageEngineHelper engine = getStorageEngine("update_player_data__");

    engine.initialize();

    // Console users always gets added
    assertEquals(2, getAutoIncrement(engine, "players"));

    // Add user and update their data
    engine.loadAndUpdatePlayerData(SPECIFIC_UUID, "Dummy").join();
    engine.loadAndUpdatePlayerData(SPECIFIC_UUID, "Dummy").join();
    engine.loadAndUpdatePlayerData(SPECIFIC_UUID, "Dummy2").join();
    engine.loadAndUpdatePlayerData(SPECIFIC_UUID, "Dummy").join();

    // Only one user added, so only one more
    assertEquals(3, getAutoIncrement(engine, "players"));
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

  private static int getAutoIncrement(MySQLStorageEngineHelper engine, String table)
      throws SQLException {
    try (NamedPreparedStatement statement =
        engine.prepareStatement(
            "SELECT `AUTO_INCREMENT` FROM `INFORMATION_SCHEMA`.`TABLES` WHERE `TABLE_SCHEMA` = 'test' AND `TABLE_NAME` = :table_name LIMIT 1")) {
      statement.setString("table_name", engine.tablePrefix + table);

      try (ResultSet result = statement.executeQuery()) {
        if (!result.next())
          throw new IllegalStateException(
              "Can't find auto increment value for table " + engine.tablePrefix + table);

        return result.getInt("AUTO_INCREMENT");
      }
    }
  }
}

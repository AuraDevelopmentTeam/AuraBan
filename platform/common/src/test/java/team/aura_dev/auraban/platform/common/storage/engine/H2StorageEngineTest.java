package team.aura_dev.auraban.platform.common.storage.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import team.aura_dev.auraban.api.player.PlayerData;

public class H2StorageEngineTest {
  private static final UUID CONSOLE_UUID = new UUID(0, 0);
  private static final UUID SPECIFIC_UUID = new UUID(1, 1);
  private static final Random rng = new Random();

  static Path getDatabaseFilePath(Path databasePath) {
    return Paths.get(databasePath.toString() + ".mv.db");
  }

  @Test
  public void nullPointerTest() throws SQLException {
    try (H2StorageEngine engine = new H2StorageEngine(null, false)) {
      fail("Expected NPE");
    } catch (NullPointerException e) {
      assertEquals("databasePath is marked @NonNull but is null", e.getMessage());
    }
  }

  @Test
  public void tableCreationTest() throws SQLException {
    runInitializationTest(getStorageEngine());
  }

  @Test
  public void noVersionTest() throws SQLException {
    final H2StorageEngineHelper engine = getStorageEngine();

    engine.connect();

    engine.executeUpdateQuery("CREATE TABLE players (id INT)");
    engine.executeUpdateQuery("CREATE TABLE ladders (id INT)");
    engine.executeUpdateQuery("CREATE TABLE ladder_steps (id INT)");
    engine.executeUpdateQuery("CREATE TABLE punishments (id INT)");
    engine.executeUpdateQuery("CREATE TABLE punishment_points (id INT)");

    runInitializationTest(engine);
  }

  @Test
  public void invalidVersionTest() throws SQLException {
    final H2StorageEngineHelper engine = getStorageEngine();

    engine.connect();

    engine.createTables();
    engine.executeUpdateQuery(
        "MERGE INTO table_versions (name, version) VALUES "
            + "('players', -1), "
            + "('ladders', -2), "
            + "('ladder_steps', -100), "
            + "('punishments', 0), "
            + "('punishment_points', -4)");

    runInitializationTest(engine);
  }

  @Test
  public void highVersionTest() throws SQLException {
    final H2StorageEngineHelper engine = getStorageEngine();

    engine.connect();

    engine.createTables();
    engine.executeUpdateQuery(
        "MERGE INTO table_versions (name, version) VALUES "
            + "('players', 1000), "
            + "('ladders', 1000), "
            + "('ladder_steps', 1000), "
            + "('punishments', 1000), "
            + "('punishment_points', 1000)");

    runInitializationTest(engine);
  }

  @Test
  public void unknownUserTest() throws SQLException, InterruptedException, ExecutionException {
    final H2StorageEngineHelper engine = getStorageEngine();

    engine.initialize();

    assertFalse(engine.loadPlayerData(SPECIFIC_UUID).get().isPresent());
  }

  @Test
  public void consoleUserTest() throws SQLException, InterruptedException, ExecutionException {
    final H2StorageEngineHelper engine = getStorageEngine();

    engine.initialize();

    Optional<PlayerData> consoleUser = engine.loadPlayerData(CONSOLE_UUID).get();

    assertTrue(consoleUser.isPresent());
    assertEquals("Console", consoleUser.get().getPlayerName());
  }

  @Test
  public void specificUserTest() throws SQLException, InterruptedException, ExecutionException {
    final H2StorageEngineHelper engine = getStorageEngine();

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

  @SneakyThrows(IOException.class)
  private H2StorageEngineHelper getStorageEngine() {
    final Path basePath = Paths.get(SystemUtils.JAVA_IO_TMPDIR);
    Path databasePath;
    Path databaseFilePath;

    synchronized (rng) {
      do {
        databasePath = basePath.resolve("h2_test_file_" + rng.nextInt());
        databaseFilePath = getDatabaseFilePath(databasePath);
      } while (Files.exists(databaseFilePath));

      Files.createFile(databaseFilePath);
    }

    return new H2StorageEngineHelper(databasePath);
  }

  private void runInitializationTest(H2StorageEngine instance) throws SQLException {
    try {
      instance.initialize();
    } catch (SQLException e) {
      throw new AssertionError("Error while initializing database", e);
    } finally {
      instance.close();
    }
  }
}

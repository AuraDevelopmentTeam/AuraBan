package team.aura_dev.auraban.platform.common.storage.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Random;
import lombok.SneakyThrows;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import team.aura_dev.auraban.platform.common.storage.sql.NamedPreparedStatement;

public class H2StorageEngineTest {
  private static final Random rng = new Random();

  private static Path getDatabaseFilePath(Path databasePath) {
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

  /** Used to make the query methods public */
  private static class H2StorageEngineHelper extends H2StorageEngine {
    public H2StorageEngineHelper(Path databasePath) {
      super(databasePath, false);
    }

    @Override
    public void connect() throws SQLException {
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

    @SneakyThrows({IOException.class, InterruptedException.class})
    @Override
    public void close() throws SQLException {
      super.close();

      final Path databaseFilePath = getDatabaseFilePath(databasePath);

      try {
        Files.delete(databaseFilePath);
      } catch (IOException e) {
        Thread.sleep(100);

        Files.delete(databaseFilePath);
      }
    }
  }
}

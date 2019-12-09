package team.aura_dev.auraban.platform.common.storage.engine;

import java.sql.SQLException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import team.aura_dev.auraban.platform.common.config.Config;

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
  public void noTablePrefixTest() {
    runInitializationTest("");
  }

  @Test
  public void tablePrefixTest() {
    runInitializationTest("auraban_");
  }

  private void runInitializationTest(String tablePrefix) {
    // Just use default values except tablePrefix
    final Config.Storage.MySQL.PoolSettings poolSettings = new Config.Storage.MySQL.PoolSettings();
    final MySQLStorageEngine instance =
        new MySQLStorageEngine(
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

    runInitializationTest(instance);
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
}

package team.aura_dev.auraban.platform.common.storage.engine;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import team.aura_dev.auraban.platform.common.config.Config;

@UtilityClass
public class TestDatabase {
  private static final String baseDir = SystemUtils.JAVA_IO_TMPDIR + "/MariaDB4j/base/";
  private static final String localhost = "localhost";
  private static DB databaseInstance;
  @Getter private static String host;
  @Getter private static int port;

  @SneakyThrows(ManagedProcessException.class)
  public static void startDatabase() {
    final int limit = 100;
    int count = 0;
    String actualBaseDir;
    String actualDataDir;

    do {
      actualBaseDir = baseDir + count;
    } while ((++count < limit) && (new File(actualBaseDir)).exists());

    Preconditions.checkElementIndex(count, limit, "count must be less than " + limit);

    actualDataDir = actualBaseDir + "/data";
    final DBConfiguration config =
        DBConfigurationBuilder.newBuilder()
            .setPort(0)
            .setSocket(localhost)
            .setBaseDir(actualBaseDir)
            .setDataDir(actualDataDir)
            .build();
    databaseInstance = DB.newEmbeddedDB(config);
    databaseInstance.start();

    host = localhost;
    port = databaseInstance.getConfiguration().getPort();

    databaseInstance.createDB("test");
  }

  @SneakyThrows({ManagedProcessException.class})
  public static void stopDatabase() {
    databaseInstance.stop();

    try {
      Thread.sleep(500);

      FileUtils.deleteDirectory(new File(databaseInstance.getConfiguration().getBaseDir()));
    } catch (IOException | InterruptedException e) {
      // Ignore
    }
  }

  public static MySQLStorageEngine getDatabaseInstance() {
    // Just use default values
    final Config.Storage.MySQL.PoolSettings poolSettings = new Config.Storage.MySQL.PoolSettings();
    final MySQLStorageEngine instance =
        new MySQLStorageEngine(
            host,
            port,
            "test",
            "test",
            "",
            poolSettings.getConnectionTimeout(),
            poolSettings.getMaximumLifetime(),
            poolSettings.getMaximumPoolSize(),
            poolSettings.getMinimumIdle(),
            poolSettings.getProperties());

    // Just connect. We don't want to initialize the database normally
    instance.connect();

    return instance;
  }

  public static void closeDatabaseInstance(MySQLStorageEngine database) {
    database.close();
  }
}

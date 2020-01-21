package team.aura_dev.auraban.platform.common.storage.engine;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Synchronized;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import team.aura_dev.auraban.platform.common.config.Config;

public class TestMySQLDatabase {
  private static final String baseDir = SystemUtils.JAVA_IO_TMPDIR + "/MariaDB4j/base/";
  private static final int dirLimit = 100;
  private static final String localhost = "localhost";

  private DB databaseInstance;
  @Getter private String host;
  @Getter private int port;

  @Synchronized
  @SneakyThrows(IOException.class)
  private static String findAvailableDatabaseDir() {
    int count = 0;
    String actualBaseDir;

    do {
      actualBaseDir = baseDir + count;
    } while ((++count < dirLimit) && (new File(actualBaseDir)).exists());

    Preconditions.checkElementIndex(count, dirLimit, "count must be less than " + dirLimit);

    // Create the dir right here
    Files.createDirectories(Paths.get(actualBaseDir));

    return actualBaseDir;
  }

  @SneakyThrows(ManagedProcessException.class)
  public void startDatabase() {
    final String actualBaseDir = findAvailableDatabaseDir();
    final String actualDataDir = actualBaseDir + "/data";
    final DBConfiguration config =
        DBConfigurationBuilder.newBuilder()
            .setPort(0)
            .setSocket(localhost)
            .setBaseDir(actualBaseDir)
            .setDataDir(actualDataDir)
            .setDeletingTemporaryBaseAndDataDirsOnShutdown(true)
            .build();
    databaseInstance = DB.newEmbeddedDB(config);
    databaseInstance.start();

    host = localhost;
    port = databaseInstance.getConfiguration().getPort();

    databaseInstance.createDB("test");
  }

  @SneakyThrows(ManagedProcessException.class)
  public void stopDatabase() {
    databaseInstance.stop();

    try {
      Thread.sleep(500);

      FileUtils.deleteDirectory(new File(databaseInstance.getConfiguration().getBaseDir()));
    } catch (IOException | InterruptedException e) {
      // Ignore
    }
  }

  public MySQLStorageEngineHelper getDatabaseInstance() {
    // Just use default values
    final Config.Storage.MySQL.PoolSettings poolSettings = new Config.Storage.MySQL.PoolSettings();
    final MySQLStorageEngineHelper instance =
        new MySQLStorageEngineHelper(
            host,
            port,
            "test",
            "test",
            "",
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

  public void closeDatabaseInstance(MySQLStorageEngine database) {
    database.close();
  }
}

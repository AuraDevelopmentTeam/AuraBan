package team.aura_dev.auraban.platform.common.config;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.storage.StorageEngineData;
import team.aura_dev.auraban.platform.common.storage.engine.H2StorageEngineData;
import team.aura_dev.auraban.platform.common.storage.engine.MySQLStorageEngineData;

@ConfigSerializable
@Getter
public class Config {
  @Setting private General general = new General();

  @Setting private Storage storage = new Storage();

  @ConfigSerializable
  @Getter
  public static class General {
    // Nothing yet
  }

  @ConfigSerializable
  @Getter
  public static class Storage {
    @Setting(comment = "The storage engine that should be used.\n" + "Allowed values: H2, MySQL")
    private StorageEngineType storageEngine = StorageEngineType.H2;

    @Setting(comment = "Settings for the h2 storage engine.")
    private H2 H2 = new H2();

    @Setting(value = "MySQL", comment = "Settings for the MySQL storage engine.")
    private MySQL mysql = new MySQL();

    public boolean isH2() {
      return getStorageEngine() == Config.Storage.StorageEngineType.H2;
    }

    public boolean isMySQL() {
      return getStorageEngine() == Config.Storage.StorageEngineType.MySQL;
    }

    public StorageEngineData getStorageEngineData() {
      switch (getStorageEngine()) {
        case H2:
          return new H2StorageEngineData(getH2());
        case MySQL:
          return new MySQLStorageEngineData(getMysql());
        default:
          throw new IllegalStateException(
              "Unknown storage engin \"" + getStorageEngine().name() + '"');
      }
    }

    public static enum StorageEngineType {
      H2,
      MySQL;

      public static final String allowedValues =
          Arrays.stream(Config.Storage.StorageEngineType.values())
              .map(Enum::name)
              .collect(Collectors.joining(", "));
    }

    @ConfigSerializable
    @Getter
    public static class H2 {
      @Setting(
        comment =
            "If this is a relative path, it will be relative to the AuraBan config dir (should be \"config/auraban\" or\n"
                + "\"plugins/AuraBan\"). Absolute paths work too of course."
      )
      private String databaseFile = "bandata";

      public Path getAbsoluteDatabasePath() {
        return AuraBanBase.getInstance().getConfigDir().resolve(getDatabaseFile()).toAbsolutePath();
      }
    }

    @ConfigSerializable
    @Getter
    public static class MySQL {
      @Setting(comment = "Credentials for the database.")
      private Credentials crendentials = new Credentials();

      @Setting(
        comment =
            "These settings are for fine tuning the MySQL connection pool.\n"
                + "- The default values will be suitable for the majority of users.\n"
                + "- Do not change these settings unless you know what you're doing!"
      )
      private PoolSettings poolSettings = new PoolSettings();

      @ConfigSerializable
      @Getter
      public static class Credentials {
        @Setting private String host = "localhost";
        @Setting private int port = 3306;
        @Setting private String database = "auraban";
        @Setting private String user = "auraban";
        @Setting private String password = "sup3rS3cur3Pa55w0rd!";

        @Setting(comment = "Prefix for the plugin tables.")
        private String tablePrefix = "auraban_";
      }

      @ConfigSerializable
      @Getter
      public static class PoolSettings {
        @Setting(
          comment =
              "This setting controls the maximum number of milliseconds that the plugin will wait for a connection from the\n"
                  + "pool, before timing out."
        )
        private long connectionTimeout = 5000;

        @Setting(
          comment =
              "This setting controls the maximum lifetime of a connection in the pool in milliseconds.\n"
                  + "- The value should be at least 30 seconds less than any database or infrastructure imposed connection time\n"
                  + "  limit."
        )
        private long maximumLifetime = 1800000;

        @Setting(
          comment =
              "Sets the maximum size of the MySQL connection pool\n"
                  + "- Basically this value will determine the maximum number of actual connections to the database backend.\n"
                  + "- More information about determining the size of connection pools can be found here:\n"
                  + "  https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing"
        )
        private int maximumPoolSize = 10;

        @Setting(
          comment =
              "Sets the minimum number of idle connections that the pool will try to maintain.\n"
                  + "- For maximum performance and responsiveness to spike demands, it is recommended to set this value to the same\n"
                  + "  value as maximmPoolSize to allow the pool to act as a fixed size connection pool."
        )
        private int minimumIdle = 10;

        @Setting(comment = "This setting allows you to define extra properties for connections.")
        private Map<String, String> properties = propertiesDefaultValue();

        private static Map<String, String> propertiesDefaultValue() {
          Map<String, String> out = new HashMap<>();
          out.put("useUnicode", "true");
          out.put("characterEncoding", "utf8");

          return out;
        }
      }
    }
  }
}

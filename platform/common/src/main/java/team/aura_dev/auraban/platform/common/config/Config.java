package team.aura_dev.auraban.platform.common.config;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.storage.StorageEngineData;
import team.aura_dev.auraban.platform.common.storage.engine.H2StorageEngineData;
import team.aura_dev.auraban.platform.common.storage.engine.MySQLStorageEngineData;
import team.aura_dev.auraban.platform.common.util.StringUtilities;

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

    @Setting(comment = "Settings for the h2 storage engine")
    private H2 h2 = new H2();

    @Setting(value = "MySQL", comment = "Settings for the MySQL storage engine")
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
      @Setting private String host = "localhost";
      @Setting private int port = 3306;
      @Setting private String database = "auraban";
      @Setting private String user = "auraban";
      @Setting private String password = "sup3rS3cur3Pa55w0rd!";

      @Setting(comment = "Prefix for the plugin tables")
      private String tablePrefix = "auraban_";

      public String getUserEncoded() {
        return StringUtilities.urlEncode(getUser());
      }

      public String getPasswordEncoded() {
        return StringUtilities.urlEncode(getPassword());
      }
    }
  }
}

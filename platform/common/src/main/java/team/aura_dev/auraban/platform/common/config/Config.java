package team.aura_dev.auraban.platform.common.config;

import com.google.common.annotations.VisibleForTesting;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.SneakyThrows;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import team.aura_dev.auraban.platform.common.AuraBanBase;

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
    private StorageEngine storageEngine = StorageEngine.H2;

    @Setting(comment = "Settings for the h2 storage engine")
    private H2 h2 = new H2();

    @Setting(value = "MySQL", comment = "Settings for the MySQL storage engine")
    private MySQL mysql = new MySQL();

    public boolean isH2() {
      return getStorageEngine() == Config.Storage.StorageEngine.H2;
    }

    public boolean isMySQL() {
      return getStorageEngine() == Config.Storage.StorageEngine.MySQL;
    }

    public static enum StorageEngine {
      H2,
      MySQL;

      public static final String allowedValues =
          Arrays.stream(Config.Storage.StorageEngine.values())
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
      private static final String UTF_8 = StandardCharsets.UTF_8.name();

      @Setting private String host = "localhost";
      @Setting private int port = 3306;
      @Setting private String database = "auraban";
      @Setting private String user = "auraban";
      @Setting private String password = "sup3rS3cur3Pa55w0rd!";

      @Setting(comment = "Prefix for the plugin tables")
      private String tablePrefix = "auraban_";

      public String getUserEncoded() {
        return urlEncode(getUser());
      }

      public String getPasswordEncoded() {
        return urlEncode(getPassword());
      }

      @VisibleForTesting
      @SneakyThrows(UnsupportedEncodingException.class)
      static String urlEncode(String str) {
        return URLEncoder.encode(str, UTF_8);
      }
    }
  }
}

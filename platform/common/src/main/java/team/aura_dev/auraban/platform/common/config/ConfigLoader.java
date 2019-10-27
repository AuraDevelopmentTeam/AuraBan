package team.aura_dev.auraban.platform.common.config;

import com.google.common.reflect.TypeToken;
import java.io.IOException;
import lombok.Getter;
import lombok.NonNull;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import team.aura_dev.auraban.platform.common.AuraBanBase;

/** This class is required because it references Configurate classes, which are loaded later. */
public class ConfigLoader {
  private final AuraBanBase plugin;
  private ConfigurationLoader<CommentedConfigurationNode> loader = null;

  @Getter @NonNull private Config config;

  public ConfigLoader(AuraBanBase plugin) {
    this.plugin = plugin;
    this.loader = getConfigLoader();
  }

  private ConfigurationLoader<CommentedConfigurationNode> getConfigLoader() {
    // TODO: Fine tune options
    return HoconConfigurationLoader.builder().setPath(plugin.getConfigFile()).build();
  }

  public void loadConfig() throws IOException, ObjectMappingException {
    final TypeToken<Config> configToken = TypeToken.of(Config.class);

    AuraBanBase.logger.debug("Loading config...");

    CommentedConfigurationNode node = loader.load();

    final Object globalValue = node.getNode("global").getValue();

    if (globalValue != null) {
      node.getNode("general").setValue(globalValue);
      node.removeChild("global");
    }

    try {
      config = node.<Config>getValue(configToken, Config::new);
    } catch (ObjectMappingException e) {
      final String message = e.getMessage();

      if (!message.startsWith("Invalid enum constant provided for storageEngine:")) throw e;

      final String defaultStorageEngine = (new Config.Storage()).getStorageEngine().name();

      AuraBanBase.logger.error(message);
      AuraBanBase.logger.warn("Possible values are: " + Config.Storage.StorageEngine.allowedValues);
      AuraBanBase.logger.warn(
          "To fix your config we changed the storage engine to " + defaultStorageEngine);

      node.getNode("storage", "storageEngine").setValue(defaultStorageEngine);

      config = node.<Config>getValue(configToken, Config::new);
    }

    AuraBanBase.logger.debug("Saving/Formatting config...");
    node.setValue(configToken, config);
    loader.save(node);
  }
}

package team.aura_dev.auraban.platform.sponge;

import com.google.inject.Inject;
import java.nio.file.Path;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import team.aura_dev.auraban.platform.common.AuraBanBaseBootstrap;
import team.aura_dev.auraban.platform.common.AuraBanBootstrapper;

@Plugin(
    id = AuraBanBootstrapper.ID,
    name = AuraBanBootstrapper.NAME,
    version = AuraBanBootstrapper.VERSION,
    description = AuraBanBootstrapper.DESCRIPTION,
    url = AuraBanBootstrapper.URL,
    authors = {AuraBanBootstrapper.AUTHOR})
public class AuraBanSpongeBootstrap {
  private final AuraBanBaseBootstrap bootstrappedPlugin;

  @Inject
  public AuraBanSpongeBootstrap(@ConfigDir(sharedRoot = false) Path configDir) {
    final AuraBanBootstrapper bootstrapper = new AuraBanBootstrapper();
    bootstrapper.initializePlugin(this, configDir);

    bootstrappedPlugin = bootstrapper.getPlugin();
  }

  @Listener
  public void preInit(GamePreInitializationEvent event) {
    bootstrappedPlugin.preInitPlugin();
  }

  @Listener
  public void init(GameInitializationEvent event) {
    bootstrappedPlugin.initPlugin();
  }
}

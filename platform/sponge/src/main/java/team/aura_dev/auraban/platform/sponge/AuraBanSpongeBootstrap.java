package team.aura_dev.auraban.platform.sponge;

import com.google.inject.Inject;
import java.io.File;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import team.aura_dev.auraban.platform.common.AuraBanBaseBootstrap;

@Plugin(
  id = AuraBanBaseBootstrap.ID,
  name = AuraBanBaseBootstrap.NAME,
  version = AuraBanBaseBootstrap.VERSION,
  description = AuraBanBaseBootstrap.DESCRIPTION,
  url = AuraBanBaseBootstrap.URL,
  authors = {AuraBanBaseBootstrap.AUTHOR}
)
public class AuraBanSpongeBootstrap {
  private final AuraBanBaseBootstrap bootstrapPlugin;

  @Inject
  public AuraBanSpongeBootstrap(@ConfigDir(sharedRoot = false) File configDir) {
    bootstrapPlugin = new AuraBanBaseBootstrap(this, configDir);
  }

  @Listener
  public void preInit(GamePreInitializationEvent event) {
    bootstrapPlugin.preInitPlugin();
  }

  @Listener
  public void init(GameInitializationEvent event) {
    bootstrapPlugin.initPlugin();
  }
}

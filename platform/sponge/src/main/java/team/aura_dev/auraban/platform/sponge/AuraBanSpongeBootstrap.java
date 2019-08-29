package team.aura_dev.auraban.platform.sponge;

import com.google.inject.Inject;
import java.io.File;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import team.aura_dev.auraban.api.AuraBanApi;

@Plugin(
  id = AuraBanApi.ID,
  name = AuraBanApi.NAME,
  version = AuraBanApi.VERSION,
  description = AuraBanApi.DESCRIPTION,
  url = AuraBanApi.URL,
  authors = {AuraBanApi.AUTHOR}
)
public class AuraBanSpongeBootstrap {
  private final AuraBanSponge plugin;

  @Inject
  public AuraBanSpongeBootstrap(@ConfigDir(sharedRoot = false) File configDir) {
    plugin = new AuraBanSponge(configDir);
  }

  @Listener
  public void preInit(GamePreInitializationEvent event) {
    plugin.preInitPlugin();
  }

  @Listener
  public void init(GameInitializationEvent event) {
    plugin.initPlugin();
  }
}

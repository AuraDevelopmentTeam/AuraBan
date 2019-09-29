package team.aura_dev.auraban.platform.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import team.aura_dev.auraban.platform.common.AuraBanBaseBootstrap;

@Plugin(
  id = AuraBanBaseBootstrap.ID,
  name = AuraBanBaseBootstrap.NAME,
  version = AuraBanBaseBootstrap.VERSION,
  description = AuraBanBaseBootstrap.DESCRIPTION,
  url = AuraBanBaseBootstrap.URL,
  authors = {AuraBanBaseBootstrap.AUTHOR}
)
public class AuraBanVelocityBootstrap {
  private final AuraBanBaseBootstrap bootstrapPlugin;

  @Inject
  public AuraBanVelocityBootstrap(ProxyServer server, @DataDirectory Path dataDir) {
    bootstrapPlugin = new AuraBanBaseBootstrap(this, server, dataDir.toFile());
  }

  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    bootstrapPlugin.preInitPlugin();
    bootstrapPlugin.initPlugin();
  }
}

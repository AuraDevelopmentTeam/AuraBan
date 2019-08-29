package team.aura_dev.auraban.platform.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import team.aura_dev.auraban.api.AuraBanApi;

@Plugin(
  id = AuraBanApi.ID,
  name = AuraBanApi.NAME,
  version = AuraBanApi.VERSION,
  description = AuraBanApi.DESCRIPTION,
  url = AuraBanApi.URL,
  authors = {AuraBanApi.AUTHOR}
)
public class AuraBanVelocityBootstrap {
  private final AuraBanVelocity plugin;

  @Inject
  public AuraBanVelocityBootstrap(ProxyServer server, @DataDirectory Path dataDir) {
    plugin = new AuraBanVelocity(server, dataDir.toFile());
  }

  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    plugin.preInitPlugin();
    plugin.initPlugin();
  }
}

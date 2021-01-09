package team.aura_dev.auraban.platform.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import team.aura_dev.auraban.platform.common.AuraBanBaseBootstrap;
import team.aura_dev.auraban.platform.common.AuraBanBootstrapper;

@Plugin(
    id = AuraBanBootstrapper.ID,
    name = AuraBanBootstrapper.NAME,
    version = AuraBanBootstrapper.VERSION,
    description = AuraBanBootstrapper.DESCRIPTION,
    url = AuraBanBootstrapper.URL,
    authors = {AuraBanBootstrapper.AUTHOR})
public class AuraBanVelocityBootstrap {
  private final AuraBanBaseBootstrap bootstrappedPlugin;

  @Inject
  public AuraBanVelocityBootstrap(ProxyServer server, @DataDirectory Path dataDir) {
    final AuraBanBootstrapper bootstrapper = new AuraBanBootstrapper();
    bootstrapper.initializePlugin(this, server, dataDir);

    bootstrappedPlugin = bootstrapper.getPlugin();
  }

  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    bootstrappedPlugin.preInitPlugin();
    bootstrappedPlugin.initPlugin();
  }
}

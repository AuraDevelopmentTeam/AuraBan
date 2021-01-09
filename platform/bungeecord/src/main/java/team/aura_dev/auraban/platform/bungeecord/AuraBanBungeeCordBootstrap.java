package team.aura_dev.auraban.platform.bungeecord;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import team.aura_dev.auraban.platform.common.AuraBanBaseBootstrap;
import team.aura_dev.auraban.platform.common.AuraBanBootstrapper;

public class AuraBanBungeeCordBootstrap extends Plugin {
  private AuraBanBaseBootstrap bootstrappedPlugin;

  @Override
  public void onLoad() {
    final AuraBanBootstrapper bootstrapper = new AuraBanBootstrapper();
    bootstrapper.checkAndLoadSLF4J(getDataFolder().toPath().resolve("libs"), "bungeecord");
    bootstrapper.initializePlugin(this, ProxyServer.getInstance(), getDataFolder().toPath());

    bootstrappedPlugin = bootstrapper.getPlugin();
  }

  @Override
  public void onEnable() {
    bootstrappedPlugin.preInitPlugin();
    bootstrappedPlugin.initPlugin();
  }
}

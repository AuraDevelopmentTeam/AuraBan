package team.aura_dev.auraban.platform.bungeecord;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import team.aura_dev.auraban.platform.common.AuraBanBase;

public class AuraBanBungeeCordBootstrap extends Plugin {
  private AuraBanBase plugin;

  @Override
  public void onLoad() {
    plugin = AuraBanBase.initializePlugin(this, ProxyServer.getInstance(), getDataFolder());
  }

  @Override
  public void onEnable() {
    plugin.preInitPlugin();
    plugin.initPlugin();
  }
}

package team.aura_dev.auraban.platform.bungeecord;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class AuraBanBungeeCordBootstrap extends Plugin {
  private final AuraBanBungeeCord plugin;

  public AuraBanBungeeCordBootstrap() {
    plugin = new AuraBanBungeeCord(ProxyServer.getInstance(), getDataFolder());
  }

  @Override
  public void onEnable() {
    plugin.preInitPlugin();
    plugin.initPlugin();
  }
}

package team.aura_dev.auraban.platform.bungeecord;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import team.aura_dev.auraban.platform.common.AuraBanBaseBootstrap;

public class AuraBanBungeeCordBootstrap extends Plugin {
  private AuraBanBaseBootstrap bootstrapPlugin;

  @Override
  public void onLoad() {
    bootstrapPlugin =
        new AuraBanBaseBootstrap(this, ProxyServer.getInstance(), getDataFolder().toPath());
  }

  @Override
  public void onEnable() {
    bootstrapPlugin.preInitPlugin();
    bootstrapPlugin.initPlugin();
  }
}

package team.aura_dev.auraban.platform.bungeecord;

import java.nio.file.Path;
import net.md_5.bungee.api.ProxyServer;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.bungeecord.listener.PlayerEventListenerBungeeCord;
import team.aura_dev.auraban.platform.bungeecord.player.PlayerManagerBungeeCord;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.player.PlayerManagerCommon;

public class AuraBanBungeeCord extends AuraBanBase {
  private final AuraBanBungeeCordBootstrap plugin;
  private final ProxyServer server;

  public AuraBanBungeeCord(AuraBanBungeeCordBootstrap plugin, ProxyServer server, Path configDir) {
    super(configDir);

    this.plugin = plugin;
    this.server = server;

    // Instance is initialized
    AuraBan.setApi(this);
  }

  @Override
  public String getBasePlatform() {
    return "BungeeCord";
  }

  @Override
  public String getPlatformVariant() {
    return server.getName();
  }

  @Override
  protected PlayerManagerCommon generatePlayerManager() {
    return new PlayerManagerBungeeCord(storageEngine);
  }

  @Override
  protected void registerEventListeners() {
    server.getPluginManager().registerListener(plugin, new PlayerEventListenerBungeeCord(this));
  }
}

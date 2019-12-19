package team.aura_dev.auraban.platform.bungeecord;

import java.nio.file.Path;
import net.md_5.bungee.api.ProxyServer;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.api.player.PlayerManager;
import team.aura_dev.auraban.platform.bungeecord.player.PlayerManagerBungeeCord;
import team.aura_dev.auraban.platform.common.AuraBanBase;

public class AuraBanBungeeCord extends AuraBanBase {
  private final ProxyServer server;

  public AuraBanBungeeCord(ProxyServer server, Path configDir) {
    super(configDir);

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
  protected PlayerManager generatePlayerManager() {
    return new PlayerManagerBungeeCord(storageEngine);
  }
}

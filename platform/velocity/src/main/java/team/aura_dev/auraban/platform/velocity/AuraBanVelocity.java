package team.aura_dev.auraban.platform.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.player.PlayerManagerCommon;
import team.aura_dev.auraban.platform.velocity.listener.PlayerEventListenerVelocity;
import team.aura_dev.auraban.platform.velocity.player.PlayerManagerVelocity;
import team.aura_dev.lib.multiplatformcore.DependencyClassLoader;

public class AuraBanVelocity extends AuraBanBase {
  private final AuraBanVelocityBootstrap plugin;
  private final ProxyServer server;

  public AuraBanVelocity(
      DependencyClassLoader classLoader,
      AuraBanVelocityBootstrap plugin,
      ProxyServer server,
      Path configDir) {
    super(classLoader, configDir);

    this.plugin = plugin;
    this.server = server;

    // Instance is initialized
    AuraBan.setApi(this);
  }

  @Override
  public String getBasePlatform() {
    return "Velocity";
  }

  @Override
  public String getPlatformVariant() {
    return server.getVersion().getName();
  }

  @Override
  protected PlayerManagerCommon generatePlayerManager() {
    return new PlayerManagerVelocity(storageEngine);
  }

  @Override
  protected void registerEventListeners() {
    server.getEventManager().register(plugin, new PlayerEventListenerVelocity(this));
  }
}

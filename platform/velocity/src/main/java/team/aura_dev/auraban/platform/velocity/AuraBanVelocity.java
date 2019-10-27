package team.aura_dev.auraban.platform.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.common.AuraBanBase;

public class AuraBanVelocity extends AuraBanBase {
  private final ProxyServer server;

  public AuraBanVelocity(ProxyServer server, Path configDir) {
    super(configDir);

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
}

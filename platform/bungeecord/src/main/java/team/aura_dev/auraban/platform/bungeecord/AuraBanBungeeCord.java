package team.aura_dev.auraban.platform.bungeecord;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import net.md_5.bungee.api.ProxyServer;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependency;

public class AuraBanBungeeCord extends AuraBanBase {
  private final ProxyServer server;

  public AuraBanBungeeCord(ProxyServer server, File configDir) {
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
  public Collection<RuntimeDependency> getEarlyDependencies() {
    return Arrays.asList(RuntimeDependency.CONFIGURATE_HOCON);
  }

  @Override
  public Collection<RuntimeDependency> getDependencies() {
    return Arrays.asList(RuntimeDependency.HIKARI_CP);
  }
}

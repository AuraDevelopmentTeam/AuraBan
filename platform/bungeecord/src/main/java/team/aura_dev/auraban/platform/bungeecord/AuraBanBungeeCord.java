package team.aura_dev.auraban.platform.bungeecord;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependency;

public class AuraBanBungeeCord extends Plugin implements AuraBanBase {
  private final ProxyServer server;

  public AuraBanBungeeCord() {
    AuraBan.setApi(this);

    server = ProxyServer.getInstance();
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
  public File getConfigDir() {
    return getDataFolder();
  }

  @Override
  public void onEnable() {
    preInitPlugin();
    initPlugin();
  }

  @Override
  public Collection<RuntimeDependency> getDependencies() {
    return Arrays.asList(RuntimeDependency.HIKARI_CP);
  }
}

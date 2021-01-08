package team.aura_dev.auraban.platform.spigot;

import java.nio.file.Path;
import org.bukkit.Bukkit;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.player.PlayerManagerCommon;
import team.aura_dev.auraban.platform.spigot.listener.PlayerEventListenerSpigot;
import team.aura_dev.auraban.platform.spigot.player.PlayerManagerSpigot;
import team.aura_dev.lib.multiplatformcore.DependencyClassLoader;

public class AuraBanSpigot extends AuraBanBase {
  private final AuraBanSpigotBootstrap plugin;

  public AuraBanSpigot(
      DependencyClassLoader classLoader, AuraBanSpigotBootstrap plugin, Path configDir) {
    super(classLoader, configDir);

    this.plugin = plugin;

    // Instance is initialized
    AuraBan.setApi(this);
  }

  @Override
  public String getBasePlatform() {
    return "Spigot";
  }

  @Override
  public String getPlatformVariant() {
    return Bukkit.getName();
  }

  @Override
  protected PlayerManagerCommon generatePlayerManager() {
    return new PlayerManagerSpigot(storageEngine);
  }

  @Override
  protected void registerEventListeners() {
    Bukkit.getPluginManager().registerEvents(new PlayerEventListenerSpigot(this), plugin);
  }
}

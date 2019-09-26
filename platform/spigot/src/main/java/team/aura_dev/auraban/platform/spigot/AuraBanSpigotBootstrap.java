package team.aura_dev.auraban.platform.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import team.aura_dev.auraban.platform.common.AuraBanBase;

public class AuraBanSpigotBootstrap extends JavaPlugin {
  private final AuraBanBase plugin;

  public AuraBanSpigotBootstrap() {
    plugin = AuraBanBase.initializePlugin(this, getDataFolder());
  }

  @Override
  public void onEnable() {
    plugin.preInitPlugin();
    plugin.initPlugin();
  }
}

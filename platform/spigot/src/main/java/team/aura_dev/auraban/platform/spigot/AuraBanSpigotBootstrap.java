package team.aura_dev.auraban.platform.spigot;

import org.bukkit.plugin.java.JavaPlugin;

public class AuraBanSpigotBootstrap extends JavaPlugin {
  private final AuraBanSpigot plugin;

  public AuraBanSpigotBootstrap() {
    plugin = new AuraBanSpigot(getDataFolder());
  }

  @Override
  public void onEnable() {
    plugin.preInitPlugin();
    plugin.initPlugin();
  }
}

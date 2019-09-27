package team.aura_dev.auraban.platform.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import team.aura_dev.auraban.platform.common.AuraBanBaseBootstrap;

public class AuraBanSpigotBootstrap extends JavaPlugin {
  private final AuraBanBaseBootstrap bootstrapPlugin;

  public AuraBanSpigotBootstrap() {
    bootstrapPlugin = new AuraBanBaseBootstrap(this, getDataFolder());
  }

  @Override
  public void onEnable() {
    bootstrapPlugin.preInitPlugin();
    bootstrapPlugin.initPlugin();
  }
}

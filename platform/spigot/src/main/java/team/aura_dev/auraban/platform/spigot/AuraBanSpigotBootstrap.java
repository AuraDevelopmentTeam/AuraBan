package team.aura_dev.auraban.platform.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import team.aura_dev.auraban.platform.common.AuraBanBaseBootstrap;
import team.aura_dev.auraban.platform.common.AuraBanBootstrapper;

public class AuraBanSpigotBootstrap extends JavaPlugin {
  private final AuraBanBaseBootstrap bootstrappedPlugin;

  public AuraBanSpigotBootstrap() {
    final AuraBanBootstrapper bootstrapper = new AuraBanBootstrapper();
    bootstrapper.checkAndLoadSLF4J(getDataFolder().toPath().resolve("libs"), "spigot");
    bootstrapper.initializePlugin(this, getDataFolder().toPath());

    bootstrappedPlugin = bootstrapper.getPlugin();
  }

  @Override
  public void onEnable() {
    bootstrappedPlugin.preInitPlugin();
    bootstrappedPlugin.initPlugin();
  }
}

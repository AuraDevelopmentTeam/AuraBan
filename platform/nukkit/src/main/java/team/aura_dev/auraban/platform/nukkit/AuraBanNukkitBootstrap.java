package team.aura_dev.auraban.platform.nukkit;

import cn.nukkit.plugin.PluginBase;
import team.aura_dev.auraban.platform.common.AuraBanBaseBootstrap;
import team.aura_dev.auraban.platform.common.AuraBanBootstrapper;

public class AuraBanNukkitBootstrap extends PluginBase {
  private AuraBanBaseBootstrap bootstrappedPlugin;

  @Override
  public void onLoad() {
    final AuraBanBootstrapper bootstrapper = new AuraBanBootstrapper();
    bootstrapper.checkAndLoadSLF4J(getDataFolder().toPath().resolve("libs"), "nukkit");
    bootstrapper.initializePlugin(this, getServer(), getDataFolder().toPath());

    bootstrappedPlugin = bootstrapper.getPlugin();
  }

  @Override
  public void onEnable() {
    bootstrappedPlugin.preInitPlugin();
    bootstrappedPlugin.initPlugin();
  }
}

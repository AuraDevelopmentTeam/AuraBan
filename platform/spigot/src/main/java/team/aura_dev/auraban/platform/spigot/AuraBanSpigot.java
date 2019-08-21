package team.aura_dev.auraban.platform.spigot;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependency;

public class AuraBanSpigot extends JavaPlugin implements AuraBanBase {
  public AuraBanSpigot() {
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

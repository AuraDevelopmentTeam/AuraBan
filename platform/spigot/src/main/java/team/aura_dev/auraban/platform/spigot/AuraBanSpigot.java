package team.aura_dev.auraban.platform.spigot;

import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.common.AuraBanBase;

public class AuraBanSpigot extends JavaPlugin implements AuraBanBase {
  public AuraBanSpigot() {
    AuraBan.setApi(this);
  }

  @Override
  public File getConfigDir() {
    return getDataFolder();
  }

  @Override
  public void onEnable() {
    startPlugin();
  }
}

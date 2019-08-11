package team.aura_dev.auraban.platform.bungeecord;

import java.io.File;
import net.md_5.bungee.api.plugin.Plugin;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.common.AuraBanBase;

public class AuraBanBungeeCord extends Plugin implements AuraBanBase {
  public AuraBanBungeeCord() {
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

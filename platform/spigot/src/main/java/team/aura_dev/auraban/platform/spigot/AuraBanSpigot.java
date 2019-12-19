package team.aura_dev.auraban.platform.spigot;

import java.nio.file.Path;
import org.bukkit.Bukkit;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.api.player.PlayerManager;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.spigot.player.PlayerManagerSpigot;

public class AuraBanSpigot extends AuraBanBase {
  public AuraBanSpigot(Path configDir) {
    super(configDir);

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
  protected PlayerManager generatePlayerManager() {
    return new PlayerManagerSpigot(storageEngine);
  }
}

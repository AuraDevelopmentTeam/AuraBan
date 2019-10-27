package team.aura_dev.auraban.platform.spigot;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import org.bukkit.Bukkit;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependency;

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
  public Collection<RuntimeDependency> getEarlyDependencies() {
    return Arrays.asList(RuntimeDependency.CONFIGURATE_HOCON);
  }
}

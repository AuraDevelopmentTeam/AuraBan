package team.aura_dev.auraban.platform.sponge;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependency;

public class AuraBanSponge extends AuraBanBase {
  public AuraBanSponge(Path configDir) {
    super(configDir);

    // Instance is initialized
    AuraBan.setApi(this);
  }

  @Override
  public String getBasePlatform() {
    return "Sponge";
  }

  @Override
  public String getPlatformVariant() {
    return Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getName();
  }

  @Override
  public Collection<RuntimeDependency> getPlatformDependencies() {
    return Arrays.asList(RuntimeDependency.CONFIGURATE_HOCON, RuntimeDependency.HIKARI_CP);
  }
}

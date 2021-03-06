package team.aura_dev.auraban.platform.sponge;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependencies;
import team.aura_dev.auraban.platform.common.player.PlayerManagerCommon;
import team.aura_dev.auraban.platform.sponge.listener.PlayerEventListenerSponge;
import team.aura_dev.auraban.platform.sponge.player.PlayerManagerSponge;
import team.aura_dev.lib.multiplatformcore.DependencyClassLoader;
import team.aura_dev.lib.multiplatformcore.dependency.RuntimeDependency;

public class AuraBanSponge extends AuraBanBase {
  private final AuraBanSpongeBootstrap plugin;

  public AuraBanSponge(
      DependencyClassLoader classLoader, AuraBanSpongeBootstrap plugin, Path configDir) {
    super(classLoader, configDir);

    this.plugin = plugin;

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
    // MARIADB_CLIENT, HIKARI_CP and CAFFEINE is present but outdated, so we use the newer version
    return Arrays.asList(RuntimeDependencies.CONFIGURATE_HOCON, RuntimeDependencies.H2_DATABASE);
  }

  @Override
  protected PlayerManagerCommon generatePlayerManager() {
    return new PlayerManagerSponge(storageEngine);
  }

  @Override
  protected void registerEventListeners() {
    Sponge.getEventManager().registerListeners(plugin, new PlayerEventListenerSponge(this));
  }
}

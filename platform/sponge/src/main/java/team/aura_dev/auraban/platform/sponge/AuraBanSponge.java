package team.aura_dev.auraban.platform.sponge;

import com.google.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import lombok.Getter;
import org.slf4j.Logger;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.api.AuraBanApi;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependency;

@Plugin(
  id = AuraBanApi.ID,
  name = AuraBanApi.NAME,
  version = AuraBanApi.VERSION,
  description = AuraBanApi.DESCRIPTION,
  url = AuraBanApi.URL,
  authors = {AuraBanApi.AUTHOR}
)
public class AuraBanSponge implements AuraBanBase {
  @Getter private final File configDir;

  @Inject
  public AuraBanSponge(@ConfigDir(sharedRoot = false) File configDir, Logger logger) {
    AuraBan.setApi(this);

    this.configDir = configDir;
  }

  @Override
  public String getBasePlatform() {
    return "Sponge";
  }

  @Override
  public String getPlatformVariant() {
    return Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getName();
  }

  @Listener
  public void preInit(GamePreInitializationEvent event) {
    preInitPlugin();
  }

  @Listener
  public void init(GameInitializationEvent event) {
    initPlugin();
  }

  @Override
  public Collection<RuntimeDependency> getDependencies() {
    return Arrays.asList(RuntimeDependency.HIKARI_CP);
  }
}

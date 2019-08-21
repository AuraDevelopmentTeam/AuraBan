package team.aura_dev.auraban.platform.sponge;

import com.google.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import lombok.Getter;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
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
  @Getter private final Logger logger;

  @Inject
  public AuraBanSponge(@ConfigDir(sharedRoot = false) File configDir, Logger logger) {
    AuraBan.setApi(this);

    this.configDir = configDir;
    this.logger = logger;
  }

  @Listener
  public void init(GameInitializationEvent event) {
    startPlugin();
  }

  @Override
  public Collection<RuntimeDependency> getDependencies() {
    return Arrays.asList(RuntimeDependency.HIKARI_CP);
  }
}

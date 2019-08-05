package team.aura_dev.auraban.platform.sponge;

import com.google.inject.Inject;
import java.io.File;
import lombok.Getter;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.plugin.Plugin;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.common.AuraBanBase;

@Plugin(
  id = AuraBanSponge.ID,
  name = AuraBanSponge.NAME,
  version = AuraBanSponge.VERSION,
  description = AuraBanSponge.DESCRIPTION,
  url = AuraBanSponge.URL,
  authors = {AuraBanSponge.AUTHOR}
)
public class AuraBanSponge implements AuraBanBase {
  public static final String ID = "@id@";
  public static final String NAME = "@name@";
  public static final String VERSION = "@version@";
  public static final String DESCRIPTION = "@description@";
  public static final String URL = "https://github.com/AuraDevelopmentTeam/AuraBan";
  public static final String AUTHOR = "The_BrainStone";

  @Getter private final File configDir;
  @Getter private final Logger logger;

  @Inject
  public AuraBanSponge(@ConfigDir(sharedRoot = false) File configDir, Logger logger) {
    AuraBan.setApi(this);

    this.configDir = configDir;
    this.logger = logger;
  }
}

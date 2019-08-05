package team.aura_dev.auraban.platform.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.File;
import java.nio.file.Path;
import lombok.Getter;
import org.slf4j.Logger;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.common.AuraBanBase;

@Plugin(
  id = AuraBanVelocity.ID,
  name = AuraBanVelocity.NAME,
  version = AuraBanVelocity.VERSION,
  description = AuraBanVelocity.DESCRIPTION,
  url = AuraBanVelocity.URL,
  authors = {AuraBanVelocity.AUTHOR}
)
public class AuraBanVelocity implements AuraBanBase {
  public static final String ID = "@id@";
  public static final String NAME = "@name@";
  public static final String VERSION = "@version@";
  public static final String DESCRIPTION = "@description@";
  public static final String URL = "https://github.com/AuraDevelopmentTeam/AuraBan";
  public static final String AUTHOR = "The_BrainStone";

  private final ProxyServer server;
  @Getter private final Logger logger;
  @Getter private final File configDir;

  @Inject
  public AuraBanVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDir) {
    AuraBan.setApi(this);

    this.server = server;
    this.logger = logger;
    this.configDir = dataDir.toFile();
  }
}

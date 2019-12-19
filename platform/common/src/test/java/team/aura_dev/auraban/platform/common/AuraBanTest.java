package team.aura_dev.auraban.platform.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import team.aura_dev.auraban.api.player.PlayerManager;

public class AuraBanTest extends AuraBanBase {
  public AuraBanTest() {
    this(Paths.get(""));
  }

  public AuraBanTest(Path configDir) {
    super(configDir);
  }

  @Override
  public String getBasePlatform() {
    return "Testing";
  }

  @Override
  public String getPlatformVariant() {
    return "Unittests";
  }

  @Override
  protected PlayerManager generatePlayerManager() {
    // TODO Implement when needed
    return null;
  }
}

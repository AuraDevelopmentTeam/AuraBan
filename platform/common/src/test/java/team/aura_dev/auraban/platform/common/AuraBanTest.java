package team.aura_dev.auraban.platform.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import team.aura_dev.auraban.platform.common.player.PlayerManagerCommon;

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
    // Some extra text so long version names aren't an issue
    return "Unittests (Here's some extra text)";
  }

  @Override
  protected PlayerManagerCommon generatePlayerManager() {
    // TODO: Implement when needed
    return null;
  }

  @Override
  protected void registerEventListeners() {
    // TODO: Implement when needed
  }
}

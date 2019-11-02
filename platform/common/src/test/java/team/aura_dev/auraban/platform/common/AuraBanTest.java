package team.aura_dev.auraban.platform.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import team.aura_dev.auraban.api.player.PlayerData;

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
  public Optional<PlayerData> getPlayerData(@Nonnull UUID uuid) {
    // TODO: Implement when needed
    return Optional.empty();
  }
}

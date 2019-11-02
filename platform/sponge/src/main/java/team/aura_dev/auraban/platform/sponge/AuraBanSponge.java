package team.aura_dev.auraban.platform.sponge;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.api.player.PlayerData;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependency;
import team.aura_dev.auraban.platform.common.storage.PlayerDataCommon;

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
    // MARIADB_CLIENT is present but outdated, so we use the newer version
    // HIKARI_CP is also fairly old/outdated and might need to removed from this list too
    return Arrays.asList(
        RuntimeDependency.CONFIGURATE_HOCON,
        RuntimeDependency.H2_DATABASE,
        RuntimeDependency.HIKARI_CP);
  }

  @Override
  public Optional<PlayerData> getPlayerData(@Nonnull UUID uuid) {
    final Optional<Player> player = Sponge.getServer().getPlayer(uuid);

    return player.map(p -> new PlayerDataCommon(uuid, p.getName()));
  }
}

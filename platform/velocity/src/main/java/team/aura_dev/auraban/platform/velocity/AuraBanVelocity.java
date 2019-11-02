package team.aura_dev.auraban.platform.velocity;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.api.player.PlayerData;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.storage.PlayerDataCommon;

public class AuraBanVelocity extends AuraBanBase {
  private final ProxyServer server;

  public AuraBanVelocity(ProxyServer server, Path configDir) {
    super(configDir);

    this.server = server;

    // Instance is initialized
    AuraBan.setApi(this);
  }

  @Override
  public String getBasePlatform() {
    return "Velocity";
  }

  @Override
  public String getPlatformVariant() {
    return server.getVersion().getName();
  }

  @Override
  public Optional<PlayerData> getPlayerData(@Nonnull UUID uuid) {
    final Optional<Player> player = server.getPlayer(uuid);

    return player.map(p -> new PlayerDataCommon(uuid, p.getUsername()));
  }
}

package team.aura_dev.auraban.platform.bungeecord;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.api.player.PlayerData;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.player.PlayerDataCommon;

public class AuraBanBungeeCord extends AuraBanBase {
  private final ProxyServer server;

  public AuraBanBungeeCord(ProxyServer server, Path configDir) {
    super(configDir);

    this.server = server;

    // Instance is initialized
    AuraBan.setApi(this);
  }

  @Override
  public String getBasePlatform() {
    return "BungeeCord";
  }

  @Override
  public String getPlatformVariant() {
    return server.getName();
  }

  @Override
  public Optional<PlayerData> getPlayerData(@Nonnull UUID uuid) {
    final Optional<ProxiedPlayer> player = Optional.ofNullable(server.getPlayer(uuid));

    return player.map(p -> new PlayerDataCommon(uuid, p.getName()));
  }
}

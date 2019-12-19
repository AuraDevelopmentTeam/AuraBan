package team.aura_dev.auraban.platform.bungeecord.player;

import java.util.UUID;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import team.aura_dev.auraban.platform.common.player.PlayerManagerCommon;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;

public class PlayerManagerBungeeCord extends PlayerManagerCommon {
  public PlayerManagerBungeeCord(StorageEngine storageEngine) {
    super(storageEngine);
  }

  @Override
  protected BasePlayerData nativePlayerToBasePlayerData(Object player)
      throws IllegalArgumentException {
    UUID uuid;
    String playerName;

    if (player instanceof BasePlayerData) {
      return (BasePlayerData) player;
    } else if (player instanceof ProxiedPlayer) {
      final ProxiedPlayer nativePlayer = (ProxiedPlayer) player;

      uuid = nativePlayer.getUniqueId();
      playerName = nativePlayer.getName();
    } else if (player instanceof PendingConnection) {
      final PendingConnection nativePlayer = (PendingConnection) player;

      uuid = nativePlayer.getUniqueId();
      playerName = nativePlayer.getName();
    } else {
      throw new IllegalArgumentException(
          "The passed player object ("
              + player
              + ") is not of type "
              + ProxiedPlayer.class.getName()
              + " or "
              + PendingConnection.class.getName());
    }

    return new BasePlayerData(uuid, playerName);
  }
}

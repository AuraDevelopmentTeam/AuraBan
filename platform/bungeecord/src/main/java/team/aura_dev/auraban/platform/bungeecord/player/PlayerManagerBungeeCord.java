package team.aura_dev.auraban.platform.bungeecord.player;

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
    if (!(player instanceof ProxiedPlayer)) {
      throw new IllegalArgumentException(
          "The passed player object ("
              + player
              + ") is not of type "
              + ProxiedPlayer.class.getName());
    }

    final ProxiedPlayer nativePlayer = (ProxiedPlayer) player;

    return new BasePlayerData(nativePlayer.getUniqueId(), nativePlayer.getName());
  }
}

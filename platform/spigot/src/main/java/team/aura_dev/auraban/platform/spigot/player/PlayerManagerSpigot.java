package team.aura_dev.auraban.platform.spigot.player;

import java.util.UUID;
import org.bukkit.entity.Player;
import team.aura_dev.auraban.platform.common.player.PlayerManagerCommon;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;

public class PlayerManagerSpigot extends PlayerManagerCommon {
  public PlayerManagerSpigot(StorageEngine storageEngine) {
    super(storageEngine);
  }

  @Override
  protected BasePlayerData nativePlayerToBasePlayerData(Object player)
      throws IllegalArgumentException {
    UUID uuid;
    String playerName;

    if (player instanceof BasePlayerData) {
      return (BasePlayerData) player;
    } else if (player instanceof Player) {
      final Player nativePlayer = (Player) player;

      uuid = nativePlayer.getUniqueId();
      playerName = nativePlayer.getName();
    } else {
      throw new IllegalArgumentException(
          "The passed player object (" + player + ") is not of type " + Player.class.getName());
    }

    return new BasePlayerData(uuid, playerName);
  }
}

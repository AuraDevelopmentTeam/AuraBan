package team.aura_dev.auraban.platform.velocity.player;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import java.util.UUID;
import team.aura_dev.auraban.platform.common.player.PlayerManagerCommon;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;

public class PlayerManagerVelocity extends PlayerManagerCommon {
  public PlayerManagerVelocity(StorageEngine storageEngine) {
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
      playerName = nativePlayer.getUsername();
    } else if (player instanceof GameProfile) {
      final GameProfile nativePlayer = (GameProfile) player;

      uuid = nativePlayer.getId();
      playerName = nativePlayer.getName();
    } else {
      throw new IllegalArgumentException(
          "The passed player object ("
              + player
              + ") is not of type "
              + Player.class.getName()
              + " or "
              + GameProfile.class.getName());
    }

    return new BasePlayerData(uuid, playerName);
  }
}

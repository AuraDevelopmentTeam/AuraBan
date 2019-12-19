package team.aura_dev.auraban.platform.velocity.player;

import com.velocitypowered.api.proxy.Player;
import team.aura_dev.auraban.platform.common.player.PlayerManagerCommon;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;

public class PlayerManagerVelocity extends PlayerManagerCommon {
  public PlayerManagerVelocity(StorageEngine storageEngine) {
    super(storageEngine);
  }

  @Override
  protected BasePlayerData nativePlayerToBasePlayerData(Object player)
      throws IllegalArgumentException {
    if (!(player instanceof Player)) {
      throw new IllegalArgumentException(
          "The passed player object (" + player + ") is not of type " + Player.class.getName());
    }

    final Player nativePlayer = (Player) player;

    return new BasePlayerData(nativePlayer.getUniqueId(), nativePlayer.getUsername());
  }
}

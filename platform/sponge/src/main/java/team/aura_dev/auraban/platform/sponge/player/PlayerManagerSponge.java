package team.aura_dev.auraban.platform.sponge.player;

import org.spongepowered.api.entity.living.player.Player;
import team.aura_dev.auraban.platform.common.player.PlayerManagerCommon;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;

public class PlayerManagerSponge extends PlayerManagerCommon {
  public PlayerManagerSponge(StorageEngine storageEngine) {
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

    return new BasePlayerData(nativePlayer.getUniqueId(), nativePlayer.getName());
  }
}

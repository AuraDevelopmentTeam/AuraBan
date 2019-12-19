package team.aura_dev.auraban.platform.sponge.player;

import java.util.UUID;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfile;
import team.aura_dev.auraban.platform.common.player.PlayerManagerCommon;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;

public class PlayerManagerSponge extends PlayerManagerCommon {
  public PlayerManagerSponge(StorageEngine storageEngine) {
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
    } else if (player instanceof GameProfile) {
      final GameProfile nativePlayer = (GameProfile) player;

      uuid = nativePlayer.getUniqueId();
      playerName = nativePlayer.getName().get();
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

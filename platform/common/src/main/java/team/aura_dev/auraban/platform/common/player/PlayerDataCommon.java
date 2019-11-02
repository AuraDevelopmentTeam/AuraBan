package team.aura_dev.auraban.platform.common.player;

import java.util.UUID;
import lombok.Data;
import team.aura_dev.auraban.api.player.PlayerData;

/**
 * Simple class to represent players in a platform independent way.
 *
 * <p>If the platform offers a way to display a display name (like with added prefixes/suffixes or a
 * nickname) then it needs to override this class.
 */
@Data
public class PlayerDataCommon implements PlayerData {
  private final UUID uuid;
  private final String playerName;

  /**
   * A nice name for the player.<br>
   * Can be overridden to allow showing of prefixes and nicknames.
   *
   * @return a nicer variant of the player name
   */
  @Override
  public String getDisplayName() {
    return playerName;
  }
}

package team.aura_dev.auraban.platform.common.storage;

import java.util.UUID;
import lombok.Data;

/**
 * Simple class to represent players in a platform independent way.
 *
 * <p>If the platform offers a way to display a display name (like with added prefixes/suffixes or a
 * nickname) then it needs to override this class.
 */
@Data
public class PlayerData {
  private final UUID uuid;
  private final String userName;

  /**
   * A nice name for the player.<br>
   * Can be overridden to allow showing of prefixes and nicknames.
   *
   * @return a nicer variant of the player name
   */
  public String getDisplayName() {
    return userName;
  }
}

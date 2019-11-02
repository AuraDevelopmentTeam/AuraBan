package team.aura_dev.auraban.api.player;

import java.util.UUID;

/**
 * Simple class to represent players in a platform independent way.
 *
 * <p>It is also used to get all ban data, etc.
 */
public interface PlayerData {
  public UUID getUuid();

  public String getPlayerName();

  public String getDisplayName();
}

package team.aura_dev.auraban.api.player;

import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * Simple class to represent players in a platform independent way.
 *
 * <p>It is also used to get all ban data, etc.
 */
public interface PlayerData {
  @Nonnull
  public UUID getUuid();

  @Nonnull
  public String getPlayerName();

  @Nonnull
  public String getDisplayName();
}

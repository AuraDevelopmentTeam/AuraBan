package team.aura_dev.auraban.api.player;

import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import team.aura_dev.auraban.api.punishment.Punishment;

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

  @Nonnull
  public Map<Integer, Punishment> getPunishments();
}

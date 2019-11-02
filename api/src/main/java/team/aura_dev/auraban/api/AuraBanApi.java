package team.aura_dev.auraban.api;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import team.aura_dev.auraban.api.player.PlayerData;

public interface AuraBanApi {
  public static final String ID = "@id@";
  public static final String NAME = "@name@";
  public static final String VERSION = "@version@";
  public static final String DESCRIPTION = "@description@";
  public static final String URL = "https://github.com/AuraDevelopmentTeam/AuraBan";
  public static final String AUTHOR = "The_BrainStone";

  /**
   * Gets the {@link PlayerData} object of the player associated with the passed {@link UUID}.<br>
   * This also does load offline players.
   *
   * @param uuid The UUID used to identify the player.<br>
   *     Must not be <code>null</code>
   * @return the data of the player associated with the passed UUID wrapped in an {@link Optional}.
   *     Or an empty Optional if the player does not exist.
   */
  public Optional<PlayerData> getPlayerData(@Nonnull UUID uuid);

  /**
   * Does the same as {@link #getPlayerData(UUID)} but returns the {@link PlayerData} object
   * directly or <code>null</code> if it doesn't exist.
   *
   * @param uuid The {@link UUID} used to identify the player.<br>
   *     Must not be <code>null</code>
   * @return the data of the player associated with the passed UUID. Or <code>null</code> if the
   *     player does not exist.
   * @see #getPlayerData(UUID)
   */
  public default PlayerData getPlayerDataUnsafe(@Nonnull UUID uuid) {
    return getPlayerData(uuid).orElse(null);
  }
}

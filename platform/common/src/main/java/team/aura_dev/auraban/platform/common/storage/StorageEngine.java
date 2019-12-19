package team.aura_dev.auraban.platform.common.storage;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import team.aura_dev.auraban.api.player.PlayerData;
import team.aura_dev.auraban.platform.common.AuraBanBase;

public interface StorageEngine extends AutoCloseable {
  // Global logger
  public static final Logger logger = AuraBanBase.logger;

  public void initialize() throws SQLException;

  /**
   * Loads the data for the specified player, only if that player already exists in the database.
   * <br>
   * <b>NOTE:</b> This method does not cache entries!
   *
   * @param uuid The {@link UUID} to identify the player by.
   * @return the loaded data of the player associated with the passed UUID wrapped in an {@link
   *     Optional}. Or an empty Optional if the player does not exist.<br>
   *     That all wrapped in a {@link CompletableFuture} because it requires IO.
   */
  public CompletableFuture<Optional<PlayerData>> loadPlayerData(@Nonnull UUID uuid);

  /**
   * Loads and updates the data for the specified player. Adds them to the database if they don't
   * already exist there.<br>
   * <b>NOTE:</b> This method does not cache entries!
   *
   * @param uuid The {@link UUID} to identify the player by.
   * @param playerName The potentially new player name associated with that player.
   * @return the loaded data of the player associated with the passed UUID wrapped in a {@link
   *     CompletableFuture} because it requires IO.
   */
  public CompletableFuture<PlayerData> loadAndUpdatePlayerData(
      @Nonnull UUID uuid, @Nonnull String playerName);

  // TODO: Methods to store and retrieve data
}

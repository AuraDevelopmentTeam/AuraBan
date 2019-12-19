package team.aura_dev.auraban.platform.common.player;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import lombok.Data;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import team.aura_dev.auraban.api.player.PlayerData;
import team.aura_dev.auraban.api.player.PlayerManager;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;

public abstract class PlayerManagerCommon implements PlayerManager {
  protected static Logger logger = AuraBanBase.logger;

  protected final StorageEngine storageEngine;
  protected final @NonNull LoadingCache<UUID, CompletableFuture<Optional<PlayerData>>> playerCache;

  protected PlayerManagerCommon(StorageEngine storageEngine) {
    this.storageEngine = storageEngine;
    playerCache =
        Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(storageEngine::loadPlayerData);
  }

  @Override
  public Optional<PlayerData> getPlayerData(UUID uuid) {
    try {
      return playerCache.get(uuid).get();
    } catch (InterruptedException | ExecutionException e) {
      logger.warn("Exception while trying to load player " + uuid, e);

      return Optional.empty();
    }
  }

  @Override
  public PlayerData fromNativePlayer(@Nonnull Object player) throws IllegalArgumentException {
    final BasePlayerData playerData = nativePlayerToBasePlayerData(player);
    final UUID uuid = playerData.getUuid();
    CompletableFuture<Optional<PlayerData>> cacheData = playerCache.getIfPresent(uuid);

    if (cacheData != null) {
      try {
        Optional<PlayerData> optionalPlayerData = cacheData.get();

        if (optionalPlayerData.isPresent()) {
          return optionalPlayerData.get();
        }
      } catch (InterruptedException | ExecutionException e) {
        logger.warn("Exception while trying to load player " + uuid, e);
      }
    }

    CompletableFuture<PlayerData> newData =
        storageEngine.loadAndUpdatePlayerData(uuid, playerData.getPlayerName());

    playerCache.put(uuid, newData.thenApply(Optional::of));

    try {
      return newData.get();
    } catch (InterruptedException | ExecutionException e) {
      logger.warn("Exception while trying to load player " + uuid, e);

      return null;
    }
  }

  /**
   * This method removes the specified player from the cache
   *
   * @param uuid The {@link UUID} of the player to remove
   */
  public void unloadPlayer(UUID uuid) {
    playerCache.invalidate(uuid);
  }

  /**
   * This method removes the specified player from the cache
   *
   * @param player The player to remove
   */
  public void unloadPlayer(PlayerData player) {
    unloadPlayer(player.getUuid());
  }

  /**
   * Collects the {@link UUID} and player name of a native player
   *
   * @param player The native player object to convert
   * @return A {@link BasePlayerData} object that contains the data from the native player.
   * @throws IllegalArgumentException when the object is not a native player object.
   */
  protected abstract BasePlayerData nativePlayerToBasePlayerData(@Nonnull Object player)
      throws IllegalArgumentException;

  @Data
  public static class BasePlayerData {
    private final UUID uuid;
    private final String playerName;
  }
}

package team.aura_dev.auraban.platform.common.player;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.Optional;
import java.util.UUID;
import team.aura_dev.auraban.api.player.PlayerData;
import team.aura_dev.auraban.api.player.PlayerManager;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;

public abstract class PlayerManagerCommon implements PlayerManager {
  protected final StorageEngine storageEngine;
  protected final LoadingCache<UUID, Optional<PlayerData>> playerCache;

  protected PlayerManagerCommon(StorageEngine storageEngine) {
    this.storageEngine = storageEngine;
    // TODO: Add eviction strategy
    playerCache = Caffeine.newBuilder().build(uuid -> storageEngine.loadPlayerData(uuid).get());
  }

  @Override
  public Optional<PlayerData> getPlayerData(UUID uuid) {
    return playerCache.get(uuid);
  }
}

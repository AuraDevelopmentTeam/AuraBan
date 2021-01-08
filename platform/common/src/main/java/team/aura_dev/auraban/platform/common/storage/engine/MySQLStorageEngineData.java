package team.aura_dev.auraban.platform.common.storage.engine;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import team.aura_dev.auraban.platform.common.config.Config;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependencies;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;
import team.aura_dev.auraban.platform.common.storage.StorageEngineData;
import team.aura_dev.lib.multiplatformcore.dependency.RuntimeDependency;

@RequiredArgsConstructor
public class MySQLStorageEngineData implements StorageEngineData {
  private final Config.Storage.MySQL configData;

  @Override
  public String getName() {
    return "MySQL";
  }

  @Override
  public List<RuntimeDependency> getRequiredRuntimeDependencies() {
    return Arrays.asList(RuntimeDependencies.HIKARI_CP, RuntimeDependencies.MARIADB_CLIENT);
  }

  @Override
  public StorageEngine createInstance() {
    final Config.Storage.MySQL.Credentials crendentials = configData.getCrendentials();
    final Config.Storage.MySQL.PoolSettings poolSettings = configData.getPoolSettings();

    return new MySQLStorageEngine(
        crendentials.getHost(),
        crendentials.getPort(),
        crendentials.getDatabase(),
        crendentials.getUser(),
        crendentials.getPassword(),
        crendentials.getTablePrefix(),
        poolSettings.getConnectionTimeout(),
        poolSettings.getMaximumLifetime(),
        poolSettings.getMaximumPoolSize(),
        poolSettings.getMinimumIdle(),
        poolSettings.getProperties());
  }
}

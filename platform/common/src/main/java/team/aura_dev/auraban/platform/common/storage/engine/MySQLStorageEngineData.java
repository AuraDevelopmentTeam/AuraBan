package team.aura_dev.auraban.platform.common.storage.engine;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import team.aura_dev.auraban.platform.common.config.Config;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependency;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;
import team.aura_dev.auraban.platform.common.storage.StorageEngineData;

@RequiredArgsConstructor
public class MySQLStorageEngineData implements StorageEngineData {
  private final Config.Storage.MySQL configData;

  @Override
  public String getName() {
    return "MySQL";
  }

  @Override
  public List<RuntimeDependency> getRequiredRuntimeDependencies() {
    return Arrays.asList(RuntimeDependency.HIKARI_CP, RuntimeDependency.MARIADB_CLIENT);
  }

  @Override
  public StorageEngine createInstance() {
    return new MySQLStorageEngine(
        configData.getHost(),
        configData.getPort(),
        configData.getDatabase(),
        configData.getUserEncoded(),
        configData.getPasswordEncoded());
  }
}

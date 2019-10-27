package team.aura_dev.auraban.platform.common.storage.engine;

import java.util.List;
import lombok.RequiredArgsConstructor;
import team.aura_dev.auraban.platform.common.config.Config;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependency;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;
import team.aura_dev.auraban.platform.common.storage.StorageEngineData;

@RequiredArgsConstructor
public class H2StorageEngineData implements StorageEngineData {
  private final Config.Storage.H2 configData;

  @Override
  public String getName() {
    return "H2";
  }

  @Override
  public List<RuntimeDependency> getRequiredRuntimeDependencies() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public StorageEngine createInstance() {
    return new H2StorageEngine(configData.getAbsoluteDatabasePath());
  }
}

package team.aura_dev.auraban.platform.common.storage;

import java.util.List;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependency;

public interface StorageEngineData {
  String getName();
  /**
   * Returns a list of all the {@link RuntimeDependency}s.<br>
   * The list can be immutable.
   *
   * @return an immutable list of all required {@link RuntimeDependency}s
   */
  List<RuntimeDependency> getRequiredRuntimeDependencies();

  StorageEngine createInstance();
}

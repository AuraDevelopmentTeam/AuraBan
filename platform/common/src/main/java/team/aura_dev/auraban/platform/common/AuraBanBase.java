package team.aura_dev.auraban.platform.common;

import java.io.File;
import java.util.Collections;
import java.util.List;
import team.aura_dev.auraban.api.AuraBanApi;
import team.aura_dev.auraban.platform.common.dependency.DependencyDownloader;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependency;

public interface AuraBanBase extends AuraBanApi {
  public File getConfigDir();

  public default void startPlugin() {
    DependencyDownloader.downloadAndInjectInClasspath(getEarlyDependencies(), getLibsDir());

    // TODO

    DependencyDownloader.downloadAndInjectInClasspath(getDependencies(), getLibsDir());
  }

  public default File getLibsDir() {
    return new File(getConfigDir(), "libs");
  }

  public default List<RuntimeDependency> getEarlyDependencies() {
    return Collections.emptyList();
  }

  public default List<RuntimeDependency> getDependencies() {
    return Collections.emptyList();
  }
}

package team.aura_dev.auraban.platform.common;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.aura_dev.auraban.api.AuraBanApi;
import team.aura_dev.auraban.platform.common.dependency.DependencyDownloader;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependency;

public interface AuraBanBase extends AuraBanApi {
  public static final Logger logger = LoggerFactory.getLogger(NAME);

  public File getConfigDir();

  public default void startPlugin() {
    logger.info("Downloading early dependencies");
    DependencyDownloader.downloadAndInjectInClasspath(getEarlyDependencies(), getLibsDir());

    // TODO

    logger.info("Downloading dependencies");
    DependencyDownloader.downloadAndInjectInClasspath(getDependencies(), getLibsDir());
  }

  public default File getLibsDir() {
    return new File(getConfigDir(), "libs");
  }

  public default Collection<RuntimeDependency> getEarlyDependencies() {
    return Collections.emptyList();
  }

  public default Collection<RuntimeDependency> getDependencies() {
    return Collections.emptyList();
  }
}

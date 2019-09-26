package team.aura_dev.auraban.platform.common;

import java.io.File;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.aura_dev.auraban.api.AuraBanApi;
import team.aura_dev.auraban.platform.common.dependency.DependencyClassLoader;
import team.aura_dev.auraban.platform.common.dependency.DependencyDownloader;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependency;

public abstract class AuraBanBase implements AuraBanApi {
  public static final Logger logger = LoggerFactory.getLogger(NAME);

  @Getter
  private static final DependencyClassLoader dependencyClassLoader =
      AccessController.doPrivileged(
          (PrivilegedAction<DependencyClassLoader>) DependencyClassLoader::new);

  @Getter protected final File configDir;
  @Getter protected final File libsDir;

  protected AuraBanBase(File configDir) {
    this.configDir = configDir;
    this.libsDir = new File(configDir, "libs");
  }

  public abstract String getBasePlatform();

  public abstract String getPlatformVariant();

  public String getFullPlatform() {
    return getBasePlatform() + " - " + getPlatformVariant();
  }

  public Collection<RuntimeDependency> getEarlyDependencies() {
    return Collections.emptyList();
  }

  public Collection<RuntimeDependency> getDependencies() {
    return Collections.emptyList();
  }

  // ============================================================================================
  // Actual plugin functionality starts here
  // ============================================================================================
  public final void preInitPlugin() {
    // Get logger without name to nicely print the banner
    final Logger bannerLogger = LoggerFactory.getLogger("");

    // TODO: Figure out how to print bold (§l (both before and after) doesn't work)
    // Print ASCII banner
    bannerLogger.info("  §a            §4 __");
    bannerLogger.info("  §a /\\     _ _ §4|__) _  _    §aAuraBan §4v" + VERSION);
    bannerLogger.info("  §a/--\\|_|| (_|§4|__)(_|| )   §8Proudly running on " + getFullPlatform());
    bannerLogger.info("");

    logger.info("Preinitializing " + NAME + " Version " + VERSION);

    if (VERSION.contains("SNAPSHOT")) {
      logger.warn("WARNING! This is a snapshot version!");
      logger.warn("Use at your own risk!");
    } else if (VERSION.contains("DEV")) {
      logger.info("This is a unreleased development version!");
      logger.info("Things might not work properly!");
    }

    logger.info("Downloading early dependencies");
    DependencyDownloader.downloadAndInjectInClasspath(getEarlyDependencies(), getLibsDir());

    // load config
    // TODO
  }

  public final void initPlugin() {
    logger.info("Initializing " + NAME + " Version " + VERSION);

    DependencyDownloader.downloadAndInjectInClasspath(getDependencies(), getLibsDir());

    // TODO
  }

  public static AuraBanBase initializePlugin(Object bootstrapPlugin, Object... params) {
    return initializePlugin(bootstrapPlugin.getClass().getName().replace("Bootstrap", ""), params);
  }

  public static AuraBanBase initializePlugin(String pluginClassName, Object... params) {
    pluginClassName.getClass();

    try {
      final Class<?> pluginClass = Class.forName(pluginClassName, true, dependencyClassLoader);
      // Checking if the parameter count matches is good enough of a way to find the matching
      // constructor in this case
      // 10/10 parameter matching
      final Constructor<?> constructor =
          Arrays.stream(pluginClass.getConstructors())
              .filter(con -> con.getParameterCount() == params.length)
              .findFirst()
              .orElseThrow(NoSuchMethodException::new);
      return (AuraBanBase) constructor.newInstance(params);
    } catch (Exception e) {
      // Catch all checked and unchecked exceptions
      logger.error("Loading the plugin class failed! Rethrowing because we can't continue...", e);
      throw new IllegalStateException("Loading the plugin class failed", e);
    }
  }
}

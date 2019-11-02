package team.aura_dev.auraban.platform.common;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.aura_dev.auraban.api.AuraBanApi;
import team.aura_dev.auraban.platform.common.config.ConfigLoader;
import team.aura_dev.auraban.platform.common.dependency.DependencyDownloader;
import team.aura_dev.auraban.platform.common.dependency.RuntimeDependency;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;
import team.aura_dev.auraban.platform.common.storage.StorageEngineData;

@SuppressFBWarnings(
  value = {"JLM_JSR166_UTILCONCURRENT_MONITORENTER", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"},
  justification = "Code is generated by lombok which means I don't have any influence on it."
)
public abstract class AuraBanBase implements AuraBanApi {
  public static final Logger logger = LoggerFactory.getLogger(NAME);

  @Getter private static AuraBanBase instance = null;

  @Getter protected final Path configDir;
  @Getter protected final Path libsDir;

  @Getter(lazy = true)
  private final List<String> asciiBanner = generateAsciiBanner();

  protected ConfigLoader configLoader;
  protected StorageEngineData storageEngineData;
  protected StorageEngine storageEngine;

  protected AuraBanBase(Path configDir) {
    this.configDir = configDir;
    this.libsDir = configDir.resolve("libs");

    if (instance != null) {
      throw new IllegalStateException("AuraBan has already been initialized!");
    }

    instance = this;
  }

  public abstract String getBasePlatform();

  public abstract String getPlatformVariant();

  public String getFullPlatform() {
    return getBasePlatform() + " - " + getPlatformVariant();
  }

  public Path getConfigFile() {
    return getConfigDir().resolve(ID + ".conf");
  }

  public Collection<RuntimeDependency> getEarlyDependencies() {
    final List<RuntimeDependency> dependencies = new LinkedList<>();

    // We need Configurate for the config
    dependencies.add(RuntimeDependency.CONFIGURATE_HOCON);

    // We don't need to download dependencies already present
    dependencies.removeAll(getPlatformDependencies());

    return dependencies;
  }

  public Collection<RuntimeDependency> getDependencies() {
    final List<RuntimeDependency> dependencies = new LinkedList<>();

    // We need all dependencies of the storage type
    dependencies.addAll(storageEngineData.getRequiredRuntimeDependencies());

    // We don't need to download dependencies already present
    dependencies.removeAll(getPlatformDependencies());

    return dependencies;
  }

  /**
   * This method returns a {@link Collection} of all the dependencies that are already present on
   * the target platform.<br>
   * This allows making sure that they are not downloaded unnecessarily.
   *
   * @return a {@link Collection} of already present dependencies
   */
  public Collection<RuntimeDependency> getPlatformDependencies() {
    return Collections.emptyList();
  }

  // ============================================================================================
  // Actual plugin functionality starts here
  // ============================================================================================
  public final void preInitPlugin() throws Exception {
    // Get logger without name to nicely print the banner
    final Logger bannerLogger = LoggerFactory.getLogger("");

    // Print ASCII banner
    for (final String bannerLine : getAsciiBanner()) {
      bannerLogger.info(bannerLine);
    }

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

    configLoader = new ConfigLoader(this);
    configLoader.loadConfig();
  }

  public final void initPlugin() throws SQLException {
    logger.info("Initializing " + NAME + " Version " + VERSION);

    // Get the storage engine information first
    storageEngineData = configLoader.getConfig().getStorage().getStorageEngineData();

    logger.info("Downloading dependencies");
    DependencyDownloader.downloadAndInjectInClasspath(getDependencies(), getLibsDir());

    logger.info("Storage Engine: " + storageEngineData.getName());
    storageEngine = storageEngineData.createInstance();

    logger.info("Intializing Storage Engine...");
    storageEngine.initialize();

    // TODO
  }

  // Private helper methods
  private List<String> generateAsciiBanner() {
    return Arrays.asList(
        "  §a            §4 __",
        "  §a /\\     _ _ §4|__) _  _    §aAuraBan §4v" + VERSION,
        "  §a/--\\|_|| (_|§4|__)(_|| )   §8Proudly running on " + getFullPlatform(),
        "");
  }
}

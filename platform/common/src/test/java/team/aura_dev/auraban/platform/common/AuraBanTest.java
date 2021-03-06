package team.aura_dev.auraban.platform.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import team.aura_dev.auraban.platform.common.player.PlayerManagerCommon;
import team.aura_dev.lib.multiplatformcore.DependencyClassLoader;

public class AuraBanTest extends AuraBanBase {
  private static final DependencyClassLoader dependencyClassLoader =
      AccessController.doPrivileged(
          (PrivilegedAction<DependencyClassLoader>) () -> new DependencyClassLoader("@group@"));

  public AuraBanTest() {
    this(dependencyClassLoader, Paths.get(""));
  }

  public AuraBanTest(DependencyClassLoader classLoader, Path configDir) {
    super(classLoader, configDir);
  }

  @Override
  public String getBasePlatform() {
    return "Testing";
  }

  @Override
  public String getPlatformVariant() {
    // Some extra text so long version names aren't an issue
    return "Unittests (Here's some extra text)";
  }

  @Override
  protected PlayerManagerCommon generatePlayerManager() {
    // TODO: Implement when needed
    return null;
  }

  @Override
  protected void registerEventListeners() {
    // TODO: Implement when needed
  }
}

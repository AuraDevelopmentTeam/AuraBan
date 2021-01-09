package team.aura_dev.auraban.platform.common;

import java.nio.file.Path;
import team.aura_dev.lib.multiplatformcore.MultiProjectSLF4JBootstrapper;

public class AuraBanBootstrapper extends MultiProjectSLF4JBootstrapper<AuraBanBaseBootstrap> {
  public static final String ID = "@id@";
  public static final String NAME = "@name@";
  public static final String VERSION = "@version@";
  public static final String DESCRIPTION = "@description@";
  public static final String URL = "https://github.com/AuraDevelopmentTeam/AuraBan";
  public static final String AUTHOR = "The_BrainStone";

  public AuraBanBootstrapper() {
    super(AuraBanBaseBootstrap.class);
  }

  @Override
  protected String getPackageName() {
    return "@group@";
  }

  /**
   * Checks if SLF4J is present and loads it if not.<br>
   * {@code slf4jVersion} defaults to @slf4jVersion@
   *
   * @param libsPath Where to unpack the jar files to
   * @param type Which type of the slf4j-plugin-xxx to use
   * @see #checkAndLoadSLF4J(Path, String, String)
   */
  public void checkAndLoadSLF4JPlugin(Path libsPath, String type) {
    checkAndLoadSLF4J(libsPath, "@slf4jVersion@", "plugin-" + type + "-@slf4jPluginVersion@");
  }
}

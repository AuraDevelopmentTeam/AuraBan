package team.aura_dev.auraban.platform.common;

import java.nio.file.Path;
import team.aura_dev.lib.multiplatformcore.MultiProjectSLF4JBootstrap;

public class AuraBanBaseBootstrap extends MultiProjectSLF4JBootstrap {
  public static final String ID = "@id@";
  public static final String NAME = "@name@";
  public static final String VERSION = "@version@";
  public static final String DESCRIPTION = "@description@";
  public static final String URL = "https://github.com/AuraDevelopmentTeam/AuraBan";
  public static final String AUTHOR = "The_BrainStone";

  @Override
  protected String getPackageName() {
    return "@group@";
  }

  /**
   * Checks if SLF4J is present and loads it if not.<br>
   * {@code slf4jVersion} defaults to @slf4jVersion@
   *
   * @param libsPath Where to unpack the jar files to
   * @param version Which version of the slf4j-plugin-xxx to use
   * @see #checkAndLoadSLF4J(Path, String, String)
   */
  @Override
  public void checkAndLoadSLF4J(Path libsPath, String version) {
    checkAndLoadSLF4J(libsPath, "@slf4jVersion@", version);
  }

  public void preInitPlugin() {
    callMethod("preInitPlugin");
  }

  public void initPlugin() {
    callMethod("initPlugin");
  }
}

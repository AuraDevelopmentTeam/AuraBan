package team.aura_dev.auraban.platform.common;

import java.io.File;
import team.aura_dev.auraban.api.AuraBanApi;

public interface AuraBanBase extends AuraBanApi {
  public File getConfigDir();

  public default File getLibsDir() {
    return new File(getConfigDir(), "libs");
  }
}

package team.aura_dev.auraban.platform.velocity;

import com.velocitypowered.api.plugin.Plugin;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.platform.common.AuraBanBase;

@Plugin(
  id = AuraBanVelocity.ID,
  name = AuraBanVelocity.NAME,
  version = AuraBanVelocity.VERSION,
  description = AuraBanVelocity.DESCRIPTION,
  url = AuraBanVelocity.URL,
  authors = {AuraBanVelocity.AUTHOR}
)
public class AuraBanVelocity implements AuraBanBase {
  public static final String ID = "@id@";
  public static final String NAME = "@name@";
  public static final String VERSION = "@version@";
  public static final String DESCRIPTION = "@description@";
  public static final String URL = "https://github.com/AuraDevelopmentTeam/AuraBan";
  public static final String AUTHOR = "The_BrainStone";

  public AuraBanVelocity() {
    AuraBan.setApi(this);
  }
}

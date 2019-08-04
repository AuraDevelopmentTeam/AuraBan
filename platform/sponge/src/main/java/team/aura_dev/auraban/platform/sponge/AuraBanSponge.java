package team.aura_dev.auraban.platform.sponge;

import org.spongepowered.api.plugin.Plugin;
import team.aura_dev.auraban.platform.common.AuraBanBase;

@Plugin(
  id = AuraBanSponge.ID,
  name = AuraBanSponge.NAME,
  version = AuraBanSponge.VERSION,
  description = AuraBanSponge.DESCRIPTION,
  url = AuraBanSponge.URL,
  authors = {AuraBanSponge.AUTHOR}
)
public class AuraBanSponge implements AuraBanBase {
  public static final String ID = "@id@";
  public static final String NAME = "@name@";
  public static final String VERSION = "@version@";
  public static final String DESCRIPTION = "@description@";
  public static final String URL = "https://github.com/AuraDevelopmentTeam/AuraBan";
  public static final String AUTHOR = "The_BrainStone";
}

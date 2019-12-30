package team.aura_dev.auraban.api;

import team.aura_dev.auraban.api.player.PlayerManager;
import team.aura_dev.auraban.api.punishment.PunishmentManager;

public interface AuraBanApi {
  public static final String ID = "@id@";
  public static final String NAME = "@name@";
  public static final String VERSION = "@version@";
  public static final String DESCRIPTION = "@description@";
  public static final String URL = "https://github.com/AuraDevelopmentTeam/AuraBan";
  public static final String AUTHOR = "The_BrainStone";

  public PlayerManager getPlayerManager();

  public PunishmentManager getPunishmentManager();
}

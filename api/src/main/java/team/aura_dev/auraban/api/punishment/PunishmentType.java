package team.aura_dev.auraban.api.punishment;

import team.aura_dev.auraban.api.AuraBan;

public enum PunishmentType {
  WARNING,
  MUTE,
  KICK,
  BAN;

  public PunishmentBuilder builder() {
    return AuraBan.getApi().getPunishmentManager().builder().type(this);
  }
}

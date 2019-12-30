package team.aura_dev.auraban.platform.common.punishment;

import team.aura_dev.auraban.api.punishment.PunishmentManager;

public class PunishmentManagerCommon implements PunishmentManager {
  @Override
  public PunishmentBuilderCommon builder() {
    return new PunishmentBuilderCommon();
  }
}

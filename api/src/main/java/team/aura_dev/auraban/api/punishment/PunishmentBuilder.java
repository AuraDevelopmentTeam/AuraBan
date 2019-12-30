package team.aura_dev.auraban.api.punishment;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import team.aura_dev.auraban.api.player.PlayerData;

public interface PunishmentBuilder {
  public PunishmentBuilder player(UUID playerUUID);

  public default PunishmentBuilder player(PlayerData player) {
    return operator(player.getUuid());
  }

  public PunishmentBuilder operator(UUID operatorUUID);

  public default PunishmentBuilder operator(PlayerData operator) {
    return operator(operator.getUuid());
  }

  public PunishmentBuilder type(PunishmentType punishmentType);

  public PunishmentBuilder active(boolean activePunishment);

  public default PunishmentBuilder active() {
    return active(true);
  }

  public default PunishmentBuilder inactive() {
    return active(false);
  }

  public PunishmentBuilder timestamp(Optional<Timestamp> punishmentTimestamp);

  public default PunishmentBuilder timestamp(Timestamp punishmentTimestamp) {
    return timestamp(Optional.ofNullable(punishmentTimestamp));
  }

  public default PunishmentBuilder now() {
    return timestamp(Optional.empty());
  }

  public PunishmentBuilder end(Optional<Timestamp> punishmentEnd);

  public default PunishmentBuilder end(Timestamp punishmentEnd) {
    return end(Optional.ofNullable(punishmentEnd));
  }

  public default PunishmentBuilder permanent() {
    return end(Optional.empty());
  }

  public PunishmentBuilder reason(String punishmentReason);

  public Punishment build();
}

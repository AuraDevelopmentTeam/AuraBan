package team.aura_dev.auraban.platform.common.punishment;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import team.aura_dev.auraban.api.player.PlayerData;
import team.aura_dev.auraban.api.punishment.Punishment;
import team.aura_dev.auraban.api.punishment.PunishmentBuilder;
import team.aura_dev.auraban.api.punishment.PunishmentType;

public class PunishmentBuilderCommon implements PunishmentBuilder {
  private Optional<Integer> id;
  private UUID player;
  private UUID operator;
  private PunishmentType type;
  private boolean active;
  // TODO: Add ladder stuff
  private Optional<Timestamp> timestamp;
  private Optional<Timestamp> end;
  private String reason;

  public PunishmentBuilderCommon() {
    id = Optional.empty();
    player = null;
    operator = null;
    type = null;
    active = true;
    timestamp = Optional.empty();
    end = Optional.empty();
    reason = null;
  }

  public PunishmentBuilderCommon id(Optional<Integer> punishmentId) {
    id = punishmentId;

    return this;
  }

  public PunishmentBuilderCommon id(int punishmentId) {
    return id(Optional.of(punishmentId));
  }

  @Override
  public PunishmentBuilderCommon player(UUID playerUUID) {
    player = playerUUID;

    return this;
  }

  @Override
  public PunishmentBuilderCommon player(PlayerData player) {
    return player(player.getUuid());
  }

  @Override
  public PunishmentBuilderCommon operator(UUID operatorUUID) {
    operator = operatorUUID;

    return this;
  }

  @Override
  public PunishmentBuilderCommon operator(PlayerData operator) {
    return operator(operator.getUuid());
  }

  @Override
  public PunishmentBuilderCommon type(PunishmentType punishmentType) {
    type = punishmentType;

    return this;
  }

  @Override
  public PunishmentBuilderCommon active(boolean activePunishment) {
    active = activePunishment;

    return this;
  }

  @Override
  public PunishmentBuilderCommon active() {
    return active(true);
  }

  @Override
  public PunishmentBuilderCommon inactive() {
    return active(false);
  }

  @Override
  public PunishmentBuilderCommon timestamp(Optional<Timestamp> punishmentTimestamp) {
    timestamp = punishmentTimestamp;

    return this;
  }

  @Override
  public PunishmentBuilderCommon timestamp(Timestamp punishmentTimestamp) {
    return timestamp(Optional.ofNullable(punishmentTimestamp));
  }

  @Override
  public PunishmentBuilderCommon now() {
    return timestamp(Optional.empty());
  }

  @Override
  public PunishmentBuilderCommon end(Optional<Timestamp> punishmentEnd) {
    end = punishmentEnd;

    return this;
  }

  @Override
  public PunishmentBuilderCommon end(Timestamp punishmentEnd) {
    return end(Optional.ofNullable(punishmentEnd));
  }

  @Override
  public PunishmentBuilderCommon permanent() {
    return end(Optional.empty());
  }

  @Override
  public PunishmentBuilderCommon reason(String punishmentReason) {
    reason = punishmentReason;

    return this;
  }

  @Override
  public Punishment build() {
    if (player == null) throw new IllegalStateException("player has not been set but is required");
    if (operator == null)
      throw new IllegalStateException("operator has not been set but is required");
    if (type == null) throw new IllegalStateException("type has not been set but is required");
    if (reason == null) throw new IllegalStateException("reason has not been set but is required");

    return new PunishmentCommon(id, player, operator, type, active, timestamp, end, reason);
  }
}

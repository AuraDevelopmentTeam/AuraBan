package team.aura_dev.auraban.platform.common.punishment;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.Value;
import team.aura_dev.auraban.api.punishment.Punishment;
import team.aura_dev.auraban.api.punishment.PunishmentType;

@Value
@NonNull
public class PunishmentCommon implements Punishment {
  private final Optional<Integer> id;
  private final UUID player;
  private final UUID operator;
  private final PunishmentType type;
  private final boolean active;
  // TODO: Add ladder stuff
  private final Optional<Timestamp> timestamp;
  private final Optional<Timestamp> end;
  private final String reason;

  @Override
  public PunishmentBuilderCommon toBuilder() {
    return new PunishmentBuilderCommon()
        .id(id)
        .player(player)
        .operator(operator)
        .type(type)
        .active(active)
        .timestamp(timestamp)
        .end(end)
        .reason(reason);
  }
}

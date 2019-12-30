package team.aura_dev.auraban.api.punishment;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public interface Punishment {
  public Optional<Integer> getId();

  public UUID getPlayer();

  public UUID getOperator();

  public PunishmentType getType();

  public boolean isActive();

  public Optional<Timestamp> getTimestamp();

  public Optional<Timestamp> getEnd();

  public String getReason();

  public PunishmentBuilder toBuilder();
}

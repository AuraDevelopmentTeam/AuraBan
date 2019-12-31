package team.aura_dev.auraban.api.punishment;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.api.player.PlayerData;

public interface PunishmentManager {
  public PunishmentBuilder builder();

  public default Map<Integer, Punishment> getPunishments(PlayerData player) {
    return player.getPunishments();
  }

  public default Optional<Map<Integer, Punishment>> getPunishments(UUID playerUUID) {
    return AuraBan.getApi().getPlayerManager().getPlayerData(playerUUID).map(this::getPunishments);
  }
}

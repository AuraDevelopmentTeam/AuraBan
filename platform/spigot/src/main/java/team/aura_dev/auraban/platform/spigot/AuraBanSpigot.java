package team.aura_dev.auraban.platform.spigot;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import team.aura_dev.auraban.api.AuraBan;
import team.aura_dev.auraban.api.player.PlayerData;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.storage.PlayerDataCommon;

public class AuraBanSpigot extends AuraBanBase {
  public AuraBanSpigot(Path configDir) {
    super(configDir);

    // Instance is initialized
    AuraBan.setApi(this);
  }

  @Override
  public String getBasePlatform() {
    return "Spigot";
  }

  @Override
  public String getPlatformVariant() {
    return Bukkit.getName();
  }

  @Override
  public Optional<PlayerData> getPlayerData(@Nonnull UUID uuid) {
    final Optional<Player> player = Optional.ofNullable(Bukkit.getPlayer(uuid));

    return player.map(p -> new PlayerDataCommon(uuid, p.getName()));
  }
}

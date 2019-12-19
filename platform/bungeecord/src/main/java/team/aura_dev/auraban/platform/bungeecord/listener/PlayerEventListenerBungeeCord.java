package team.aura_dev.auraban.platform.bungeecord.listener;

import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.player.PlayerManagerCommon;

public class PlayerEventListenerBungeeCord implements Listener {
  private final PlayerManagerCommon playerManager;

  public PlayerEventListenerBungeeCord(AuraBanBase plugin) {
    this.playerManager = plugin.getPlayerManager();
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerJoinAsync(LoginEvent event) {
    playerManager.fromNativePlayer(event.getConnection());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerLeave(PlayerDisconnectEvent event) {
    playerManager.unloadPlayer(event.getPlayer().getUniqueId());
  }
}

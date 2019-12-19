package team.aura_dev.auraban.platform.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.player.PlayerManagerCommon;

public class PlayerEventListenerVelocity {
  private final PlayerManagerCommon playerManager;

  public PlayerEventListenerVelocity(AuraBanBase plugin) {
    this.playerManager = plugin.getPlayerManager();
  }

  @Subscribe(order = PostOrder.EARLY)
  public void onPlayerJoinAsync(LoginEvent event) {
    playerManager.fromNativePlayer(event.getPlayer());
  }

  @Subscribe(order = PostOrder.LAST)
  public void onPlayerLeave(DisconnectEvent event) {
    playerManager.unloadPlayer(event.getPlayer().getUniqueId());
  }
}

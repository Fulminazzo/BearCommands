package it.angrybear.Velocity.Listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Listeners.BearPlayerListener;
import it.angrybear.Velocity.Objects.VelocityBearPlayer;

public class VelocityBearPlayerListener<OnlinePlayer extends VelocityBearPlayer> extends BearPlayerListener<OnlinePlayer> {

    public VelocityBearPlayerListener(IBearPlugin<OnlinePlayer> plugin) {
        super(plugin);
    }

    @Subscribe(order = PostOrder.NORMAL)
    public void onPlayerJoin(PostLoginEvent event) {
        onPlayerJoin(event.getPlayer());
    }

    @Subscribe(order = PostOrder.NORMAL)
    public void onPlayerQuit(DisconnectEvent event) {
        onPlayerQuit(event.getPlayer());
    }
}

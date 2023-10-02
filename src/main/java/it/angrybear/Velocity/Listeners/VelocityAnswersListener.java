package it.angrybear.Velocity.Listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Listeners.AbstractAnswersListener;
import it.angrybear.Velocity.Objects.VelocityBearPlayer;

public class VelocityAnswersListener<P extends VelocityBearPlayer> extends AbstractAnswersListener<P> {

    public VelocityAnswersListener(IBearPlugin<?> plugin) {
        super(plugin);
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        if (!event.getResult().isAllowed()) return;
        if (answerQuestion(event.getPlayer(), event.getMessage()))
            event.setResult(PlayerChatEvent.ChatResult.denied());
    }
}

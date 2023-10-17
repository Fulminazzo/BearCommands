package it.angrybear.Bukkit.Listeners;

import it.angrybear.Bukkit.Objects.BearPlayer;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Listeners.AbstractAnswersListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AnswersListener<P extends BearPlayer<?>> extends AbstractAnswersListener<P> implements Listener {

    public AnswersListener(IBearPlugin<?> plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(event.isCancelled() || answerQuestion(event.getPlayer(), event.getMessage()));
    }
}
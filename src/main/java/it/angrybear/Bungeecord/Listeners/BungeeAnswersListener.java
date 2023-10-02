package it.angrybear.Bungeecord.Listeners;

import it.angrybear.Bungeecord.Objects.BungeeBearPlayer;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Listeners.AbstractAnswersListener;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeAnswersListener<P extends BungeeBearPlayer> extends AbstractAnswersListener<P> implements Listener {

    public BungeeAnswersListener(IBearPlugin<?> plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerChat(ChatEvent event) {
        event.setCancelled(event.isCancelled() || answerQuestion(event.getSender(), event.getMessage()));
    }
}

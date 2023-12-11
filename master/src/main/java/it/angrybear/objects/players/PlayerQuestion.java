package it.angrybear.objects.players;

import it.angrybear.interfaces.IBearPlugin;
import it.angrybear.interfaces.functions.BiConsumerException;
import it.angrybear.objects.wrappers.WPlayer;

import java.util.Date;

public class PlayerQuestion {
    private final IBearPlugin plugin;
    private final BiConsumerException<WPlayer, String> action;
    private final Date expireDate;

    public PlayerQuestion(IBearPlugin plugin, BiConsumerException<WPlayer, String> action) {
        this(plugin, action, null);
    }

    public PlayerQuestion(IBearPlugin plugin, BiConsumerException<WPlayer, String> action, Date expireDate) {
        this.plugin = plugin;
        this.action = action;
        this.expireDate = expireDate;
    }

    public boolean isExpired() {
        return expireDate != null && (new Date().getTime() - expireDate.getTime()) / 1000 >= 0;
    }

    public <Player> void accept(Player player, String message) {
        try {
            action.accept(new WPlayer(plugin, player), message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

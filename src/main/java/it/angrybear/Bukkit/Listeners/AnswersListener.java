package it.angrybear.Bukkit.Listeners;

import it.angrybear.Bukkit.BearPlugin;
import it.angrybear.Bukkit.Managers.OfflineBearPlayerManager;
import it.angrybear.Bukkit.Objects.BearPlayer;
import it.angrybear.Managers.BearPlayerManager;
import it.angrybear.Utils.HexUtils;
import it.angrybear.Utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class AnswersListener<P extends BearPlayer> implements Listener {
    private final BearPlugin<?, ?> plugin;

    public AnswersListener(BearPlugin<?, ?> plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = HexUtils.unParseHexColor(event.getMessage()).replace(StringUtils.getColorCode(), "&");
        Player player = event.getPlayer();
        P bearPlayer = getBearPlayer(player);
        if (bearPlayer == null) return;
        event.setCancelled(event.isCancelled() || bearPlayer.answerQuestion(message));
    }

    public P getBearPlayer(Player player) {
        List<ClassCastException> exceptionList = new ArrayList<>();
        BearPlayerManager<?> playerManager = plugin.getPlayersManager();
        if (playerManager != null) {
            try {return (P) playerManager.getPlayer(player);}
            catch (ClassCastException e) {exceptionList.add(e);}
        }
        OfflineBearPlayerManager<?> offlinePlayersManager = plugin.getOfflinePlayersManager();
        if (offlinePlayersManager != null) {
            try {return (P) offlinePlayersManager.getPlayer(player);}
            catch (ClassCastException e) {exceptionList.add(e);}
        }
        exceptionList.forEach(Throwable::printStackTrace);
        return null;
    }
}
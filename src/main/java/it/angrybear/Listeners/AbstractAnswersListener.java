package it.angrybear.Listeners;

import it.angrybear.Bukkit.BearPlugin;
import it.angrybear.Bukkit.Managers.OfflineBearPlayersManager;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Managers.BearPlayersManager;
import it.angrybear.Objects.ABearPlayer;
import it.angrybear.Utils.HexUtils;
import it.angrybear.Utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class AbstractAnswersListener<P extends ABearPlayer<?>> {
    private final IBearPlugin<?> plugin;

    public AbstractAnswersListener(IBearPlugin<?> plugin) {
        this.plugin = plugin;
    }

    protected boolean answerQuestion(Object player, String message) {
        message = HexUtils.unParseHexColor(message).replace(StringUtils.getColorCode(), "&");
        P bearPlayer = getBearPlayer(player);
        if (bearPlayer == null) return false;
        return bearPlayer.answerQuestion(message);
    }

    public P getBearPlayer(Object player) {
        List<ClassCastException> exceptionList = new ArrayList<>();
        BearPlayersManager<?> playerManager = plugin.getPlayersManager();
        if (playerManager != null) {
            try {return (P) playerManager.getPlayer(player);}
            catch (ClassCastException e) {exceptionList.add(e);}
        }
        if (plugin instanceof BearPlugin<?, ?>) {
            OfflineBearPlayersManager<?> offlinePlayersManager = ((BearPlugin<?, ?>) plugin).getOfflinePlayersManager();
            if (offlinePlayersManager != null) {
                try {
                    return (P) offlinePlayersManager.getPlayer(player);
                } catch (ClassCastException e) {
                    exceptionList.add(e);
                }
            }
        }
        exceptionList.forEach(Throwable::printStackTrace);
        return null;
    }
}
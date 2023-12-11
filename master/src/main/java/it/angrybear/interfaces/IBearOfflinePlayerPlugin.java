package it.angrybear.interfaces;

import it.angrybear.managers.BearPlayersManager;
import it.angrybear.objects.players.ABearPlayer;
import it.fulminazzo.reflectionutils.objects.ReflObject;

public interface IBearOfflinePlayerPlugin<Player extends ABearPlayer<IBearPlayerPlugin<Player>>,
        OfflinePlayer extends ABearPlayer<IBearOfflinePlayerPlugin<Player, OfflinePlayer>>> extends IBearPlayerPlugin<Player> {
    
    /*
        OFFLINE PLAYERS MANAGER SECTION
     */

    @Override
    default void loadManagers() throws Exception {
        IBearPlayerPlugin.super.loadManagers();
        loadOfflinePlayersManager();
    }

    default void loadOfflinePlayersManager() {
        Class<OfflinePlayer> playerClass = getOfflinePlayerClass();
        Class<? extends BearPlayersManager<OfflinePlayer, IBearOfflinePlayerPlugin<Player, OfflinePlayer>>> playersManagerClass =
                getOfflinePlayersManagerClass();
        if (playerClass == null) return;
        ReflObject<? extends BearPlayersManager<OfflinePlayer, IBearOfflinePlayerPlugin<Player, OfflinePlayer>>> reflPlayersManager =
                new ReflObject<>(playersManagerClass, this, "Players", playerClass);
        setOfflinePlayersManager(reflPlayersManager.getObject());
    }

    <M extends BearPlayersManager<OfflinePlayer, IBearOfflinePlayerPlugin<Player, OfflinePlayer>>> M getOfflinePlayersManager();

    <M extends BearPlayersManager<OfflinePlayer, IBearOfflinePlayerPlugin<Player, OfflinePlayer>>> void setOfflinePlayersManager(M offlinePlayersManager);

    default Class<? extends BearPlayersManager<OfflinePlayer, IBearOfflinePlayerPlugin<Player, OfflinePlayer>>> getOfflinePlayersManagerClass() {
        return null;
    }

    default Class<OfflinePlayer> getOfflinePlayerClass() {
        return null;
    }
}

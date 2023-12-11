package it.angrybear.interfaces;

import it.angrybear.managers.BearPlayersManager;
import it.angrybear.objects.players.ABearPlayer;
import it.fulminazzo.reflectionutils.objects.ReflObject;

public interface IBearPlayerPlugin<Player extends ABearPlayer<IBearPlayerPlugin<Player>>> extends IBearPlugin {

    /*
        PLAYERS MANAGER SECTION
     */

    @Override
    default void loadManagers() throws Exception {
        IBearPlugin.super.loadManagers();
        loadPlayersManager();
    }

    default void loadPlayersManager() {
        Class<Player> playerClass = getPlayerClass();
        Class<? extends BearPlayersManager<Player, IBearPlayerPlugin<Player>>> playersManagerClass = getPlayersManagerClass();
        if (playerClass == null) return;
        ReflObject<? extends BearPlayersManager<Player, IBearPlayerPlugin<Player>>> reflPlayersManager =
                new ReflObject<>(playersManagerClass, this, "Players", playerClass);
        setPlayersManager(reflPlayersManager.getObject());
    }

    <M extends BearPlayersManager<Player, IBearPlayerPlugin<Player>>> M getPlayersManager();

    <M extends BearPlayersManager<Player, IBearPlayerPlugin<Player>>> void setPlayersManager(M playersManager);

    default Class<? extends BearPlayersManager<Player, IBearPlayerPlugin<Player>>> getPlayersManagerClass() {
        return null;
    }

    default Class<Player> getPlayerClass() {
        return null;
    }
}

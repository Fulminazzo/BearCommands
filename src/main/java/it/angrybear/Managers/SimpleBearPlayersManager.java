package it.angrybear.Managers;

import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.ABearPlayer;

public abstract class SimpleBearPlayersManager<Player extends ABearPlayer<?>> extends BearPlayersManager<Player> {

    public SimpleBearPlayersManager(IBearPlugin<?> plugin, Class<Player> customPlayerClass) {
        super(plugin, customPlayerClass);
        disableSave();
    }
}
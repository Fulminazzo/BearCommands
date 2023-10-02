package it.angrybear.Managers;

import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.ABearPlayer;

public abstract class SimpleBearPlayersManager<P extends ABearPlayer> extends BearPlayersManager<P> {

    public SimpleBearPlayersManager(IBearPlugin<?> plugin, Class<P> customPlayerClass) {
        super(plugin, customPlayerClass);
        disableSave();
    }
}
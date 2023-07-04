package it.angrybear.Managers;

import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.ABearPlayer;

public abstract class SimpleBearPlayerManager<P extends ABearPlayer> extends BearPlayerManager<P> {

    public SimpleBearPlayerManager(IBearPlugin<?> plugin, Class<P> customPlayerClass) {
        super(plugin, customPlayerClass);
        disableSave();
    }
}
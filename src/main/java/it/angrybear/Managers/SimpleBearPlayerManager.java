package it.angrybear.Managers;

import it.angrybear.BearPlugin;
import it.angrybear.Objects.BearPlayer;

public class SimpleBearPlayerManager<P extends BearPlayer> extends BearPlayerManager<P> {
    public SimpleBearPlayerManager(BearPlugin<?, ?> plugin, Class<P> customPlayerClass) {
        super(plugin, customPlayerClass);
    }
}
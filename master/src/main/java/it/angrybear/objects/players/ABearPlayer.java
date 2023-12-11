package it.angrybear.objects.players;

import it.angrybear.exceptions.PlayerCannotBeNullException;
import it.angrybear.interfaces.IBearPlugin;
import it.angrybear.objects.Savable;
import it.angrybear.objects.wrappers.WPlayer;
import it.fulminazzo.reflectionutils.objects.ReflObject;
import lombok.Getter;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

@Getter
public abstract class ABearPlayer<P extends IBearPlugin> extends Savable<P> {
    private UUID uuid;
    private String name;

    public ABearPlayer(P plugin, File file) {
        super(plugin, file);
    }

    public ABearPlayer(P plugin, File folder, UUID uuid) {
        super(plugin, folder, uuid.toString());
        WPlayer wPlayer = plugin.getPlayer(uuid);
        if (wPlayer == null) throw new PlayerCannotBeNullException();
        this.uuid = wPlayer.getUuid();
        this.name = wPlayer.getName();
    }

    public boolean isOnline() {
        return getWPlayer().isOnline();
    }

    public boolean isOffline() {
        return getWPlayer().isOffline();
    }

    public <Player> Player getPlayer() {
        ReflObject<Player> reflPlayer = getWPlayer().getPlayer();
        return reflPlayer == null ? null : reflPlayer.getObject();
    }

    public WPlayer getWPlayer() {
        return new WPlayer(plugin, uuid);
    }

    public boolean equals(ABearPlayer<P> aBearPlayer) {
        return aBearPlayer != null && aBearPlayer.uuid.equals(uuid);
    }

    public boolean equals(WPlayer wPlayer) {
        return Objects.equals(wPlayer, getWPlayer());
    }
}

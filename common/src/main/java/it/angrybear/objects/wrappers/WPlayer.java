package it.angrybear.objects.wrappers;

import it.angrybear.interfaces.IBearPlugin;
import it.fulminazzo.reflectionutils.objects.ReflObject;
import lombok.Getter;

import java.util.UUID;

//TODO: Full doc
@Getter
public class WPlayer extends WCommandSender {
    protected final UUID uuid;

    public WPlayer(IBearPlugin plugin, UUID uuid) {
        this(plugin, new ReflObject<>(plugin.getRawPlayer(uuid)));
    }

    public WPlayer(IBearPlugin plugin, String name) {
        this(plugin, new ReflObject<>(plugin.getRawPlayer(name)));
    }

    public WPlayer(IBearPlugin plugin, ReflObject<?> player) {
        super(plugin, player);
        uuid = player.getMethodObject("getUniqueId");
    }

    public <P> WPlayer(IBearPlugin plugin, P player) {
        this(plugin, new ReflObject<>(player));
    }

    public boolean isOnline() {
        return plugin.getPlayer(name) != null;
    }

    public boolean isOffline() {
        return !isOnline();
    }

    public <P> ReflObject<P> getPlayer() {
        return getCommandSender();
    }

    public boolean equals(WPlayer wPlayer) {
        return wPlayer != null && wPlayer.uuid.equals(uuid);
    }
}
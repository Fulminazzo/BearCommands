package it.angrybear.Objects;

import it.angrybear.Bukkit.Utils.PacketsUtils;
import it.angrybear.Exceptions.ExpectedPlayerException;
import it.angrybear.Utils.ServerUtils;
import it.angrybear.Utils.VersionsUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;

import java.util.UUID;

@SuppressWarnings("unchecked")
public class UtilPlayer {
    private final Object player;
    private final Class<?> playerClass;

    public UtilPlayer(Object player) throws ExpectedPlayerException {
        if (!ServerUtils.isPlayer(player)) throw new ExpectedPlayerException(player);
        this.player = player;
        this.playerClass = player.getClass();
    }

    public String getName() {
        return getPlayerReflObject().getMethodObject("getName");
    }

    public UUID getUniqueId() {
        return getPlayerReflObject().getMethodObject("getUniqueId");
    }

    public int getPing() {
        if (!ServerUtils.isBukkit() || VersionsUtils.is1_17()) return getPlayerReflObject().getMethodObject("getPing");
        else return PacketsUtils.getEntityPlayer(player).getFieldObject("ping");
    }

    public <P> P getPlayer() {
        return (P) player;
    }

    public <P> ReflObject<P> getPlayerReflObject() {
        return new ReflObject<>((P) player, (Class<P>) playerClass);
    }
}
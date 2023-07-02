package it.angrybear.Objects;

import it.angrybear.Utils.PacketsUtils;
import it.angrybear.Utils.VersionsUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.entity.Player;

public class UtilPlayer {
    private final Player player;

    public UtilPlayer(Player player) {
        this.player = player;
    }

    public int getPing() {
        if (VersionsUtils.is1_17()) return getPlayerReflObject().getMethodObject("getPing");
        else return PacketsUtils.getEntityPlayer(player).getFieldObject("ping");
    }

    public ReflObject<Player> getPlayerReflObject() {
        return new ReflObject<>(player, Player.class);
    }
}
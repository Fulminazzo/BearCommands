package it.angrybear.Objects.Wrappers;

import it.angrybear.Bukkit.Utils.PacketsUtils;
import it.angrybear.Exceptions.ExpectedPlayerException;
import it.angrybear.Utils.ServerUtils;
import it.angrybear.Utils.VersionsUtils;

import java.util.UUID;

@SuppressWarnings("unchecked")
public class PlayerWrapper extends CommandSenderWrapper {

    public PlayerWrapper(Object player) throws ExpectedPlayerException {
        super(player);
        if (!ServerUtils.isPlayer(player)) throw new ExpectedPlayerException(player);
    }

    public int getPing() {
        if (!ServerUtils.isBukkit() || VersionsUtils.is1_17()) return commandSender.getMethodObject("getPing");
        else return PacketsUtils.getEntityPlayer(getPlayer()).getFieldObject("ping");
    }

    public UUID getUniqueId() {
        return commandSender.getMethodObject("getUniqueId");
    }

    public <P> P getPlayer() {
        return (P) commandSender;
    }
}
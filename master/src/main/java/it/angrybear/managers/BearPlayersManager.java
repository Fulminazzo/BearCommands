package it.angrybear.managers;

import it.angrybear.interfaces.IBearPlugin;
import it.angrybear.interfaces.functions.BiConsumerException;
import it.angrybear.objects.players.ABearPlayer;
import it.angrybear.objects.wrappers.WPlayer;
import it.fulminazzo.reflectionutils.objects.ReflObject;
import lombok.Setter;

import java.io.File;
import java.util.UUID;

@Setter
public class BearPlayersManager<Player extends ABearPlayer<P>, P extends IBearPlugin> extends SavableManager<Player, P> {
    private BiConsumerException<P, Player> onQuitAction;

    public BearPlayersManager(P plugin, String folderName, Class<Player> playerClass) {
        this(plugin, new File(plugin.getDataFolder(), folderName), playerClass);
    }

    public BearPlayersManager(P plugin, File folder, Class<Player> playerClass) {
        super(plugin, folder,
                (p, n) -> new ReflObject<>(playerClass, p.getPlugin(), p.getFolder(), n).getObject(),
                (p, f) -> new ReflObject<>(playerClass, p, f).getObject());
    }

    public void add(WPlayer wPlayer) {
        if (wPlayer != null) add(wPlayer.getUuid());
    }

    public void add(UUID uuid) {
        if (uuid != null) add(uuid.toString());
    }

    public Player get(WPlayer wPlayer) {
        return wPlayer == null ? null : get(wPlayer.getUuid());
    }

    public Player get(String name) {
        return name == null ? null : get((p, n) -> p.getName().equalsIgnoreCase(n), name);
    }

    public Player get(UUID uuid) {
        return uuid == null ? null : get((p, u) -> p.getUuid().equals(u), uuid);
    }

    public void remove(WPlayer wPlayer) {
        if (wPlayer != null) remove(wPlayer.getUuid());
    }

    public void remove(String name) {
        if (name != null) remove(get(name));
    }

    public void remove(UUID uuid) {
        if (uuid != null) remove(get(uuid));
    }

    @Override
    public void remove(Player player) {
        if (player == null) return;
        if (onQuitAction != null) {
            try {
                onQuitAction.accept(plugin, player);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        super.remove(player);
    }
}

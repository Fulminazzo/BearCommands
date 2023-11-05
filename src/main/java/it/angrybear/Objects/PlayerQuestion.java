package it.angrybear.Objects;

import it.angrybear.Objects.Wrappers.PlayerWrapper;

import java.util.Date;
import java.util.function.BiConsumer;

public class PlayerQuestion {
    private final BiConsumer<PlayerWrapper, String> action;
    private final Date expireDate;

    public PlayerQuestion(BiConsumer<PlayerWrapper, String> action) {
        this.action = action;
        this.expireDate = null;
    }

    public PlayerQuestion(BiConsumer<PlayerWrapper, String> action, int seconds) {
        this.action = action;
        this.expireDate = new Date(new Date().getTime() + seconds * 1000L);
    }

    public boolean isExpired() {
        return expireDate != null && (new Date().getTime() - expireDate.getTime()) / 1000 >= 0;
    }

    public <Player> void accept(Player player, String message) {
        action.accept(new PlayerWrapper(player), message);
    }
}
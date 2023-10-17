package it.angrybear.Bukkit.Interfaces;

import org.bukkit.entity.Player;

import java.util.List;

public interface IBossBar {

    String getTitle();

    void setTitle(String title);

    float getProgress();

    void setProgress(double progress);

    void setVisible(boolean visible);

    boolean isVisible();

    default void show() {
        setVisible(true);
    }

    default void hide() {
        setVisible(false);
    }

    void addPlayer(Player player);

    void removePlayer(Player player);

    void removeAll();

    List<Player> getPlayers();
}

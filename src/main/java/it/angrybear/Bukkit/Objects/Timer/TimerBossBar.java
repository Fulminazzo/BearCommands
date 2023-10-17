package it.angrybear.Bukkit.Objects.Timer;

import it.angrybear.Bukkit.Interfaces.IBossBar;
import it.angrybear.Bukkit.Objects.BossBar;
import it.angrybear.Objects.Timer.Timer;
import it.angrybear.Objects.Timer.TimerIntermediateAction;
import org.bukkit.entity.Player;

import java.util.List;

public class TimerBossBar extends Timer implements IBossBar {
    private final BossBar bossBar;

    public TimerBossBar(String title, TimerIntermediateAction... intermediateActions) {
        this(title, null, null, intermediateActions);
    }

    public TimerBossBar(String title, String barColor, TimerIntermediateAction... intermediateActions) {
        this(title, barColor, null, intermediateActions);
    }

    public TimerBossBar(String title, String barColor, String barStyle, TimerIntermediateAction... intermediateActions) {
        super(null, intermediateActions);
        this.bossBar = new BossBar(title, barColor, barStyle);
    }

    @Override
    protected Runnable createCounterRunnable() {
        return () -> {
            if (paused) return;
            if (counter >= duration) {
                bossBar.removeAll();
                if (duration >= 0) stop();
                return;
            }
            getIntermediateAction(counter).ifPresent(t -> t.runAction(counter));
            if (secondIntermediateAction != null) secondIntermediateAction.accept(counter);
            counter += interval;
            bossBar.setProgress((duration - counter) / duration);
        };
    }

    @Override
    public String getTitle() {
        return bossBar.getTitle();
    }

    @Override
    public void setTitle(String title) {
        bossBar.setTitle(title);
    }

    public <O> O getColor() {
        return bossBar.getColor();
    }

    public void setColor(String colorName) {
        bossBar.setColor(colorName);
    }

    public <O> O getStyle() {
        return bossBar.getStyle();
    }

    public void setStyle(String styleName) {
        bossBar.setStyle(styleName);
    }

    @Override
    public float getProgress() {
        return bossBar.getProgress();
    }

    @Override
    public void setProgress(double progress) {
        bossBar.setProgress(progress);
    }

    @Override
    public void setVisible(boolean visible) {
        bossBar.setVisible(visible);
    }

    @Override
    public boolean isVisible() {
        return bossBar.isVisible();
    }

    @Override
    public void addPlayer(Player player) {
        bossBar.addPlayer(player);
    }

    @Override
    public void removePlayer(Player player) {
        bossBar.removePlayer(player);
    }

    @Override
    public void removeAll() {
        bossBar.removeAll();
    }

    @Override
    public List<Player> getPlayers() {
        return bossBar.getPlayers();
    }

    public void removeFlag(String flagName) {
        bossBar.removeFlag(flagName);
    }

    public void addFlag(String flagName) {
        bossBar.addFlag(flagName);
    }

    public boolean hasFlag(String flagName) {
        return bossBar.hasFlag(flagName);
    }

    public BossBar getBossBar() {
        return bossBar;
    }
}

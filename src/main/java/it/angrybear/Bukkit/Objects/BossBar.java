package it.angrybear.Bukkit.Objects;

import it.angrybear.Bukkit.Interfaces.IBossBar;
import it.angrybear.Utils.StringUtils;
import it.angrybear.Utils.VersionsUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class BossBar implements IBossBar {
    private static final List<BossBar> bossBars = new ArrayList<>();
    private final ReflObject<?> realBossBar;
    private final List<ReflObject<?>> barFlags;

    public BossBar(String title) {
        this(title, null, null);
    }

    public BossBar(String title, String colorName) {
        this(title, colorName, null);
    }

    public BossBar(String title, String colorName, String styleName) {
        this.barFlags = new ArrayList<>();
        this.realBossBar = createEmptyBossBar();
        this.realBossBar.setShowErrors(false);
        setTitle(title);
        setColor(colorName);
        setStyle(styleName);
        setProgress(1.0);
        bossBars.add(this);
    }

    @Override
    public String getTitle() {
        return this.realBossBar.getMethodObject("getTitle");
    }

    @Override
    public void setTitle(String title) {
        if (title == null) return;
        this.realBossBar.callMethod("setTitle", StringUtils.parseMessage(title));
    }

    public <O> O getColor() {
        return this.realBossBar.getMethodObject("getColor");
    }

    public void setColor(String colorName) {
        if (!VersionsUtils.is1_9()) return;
        Object color = getBarColor(colorName);
        if (color == null) return;
        this.realBossBar.callMethod("setColor", color);
    }

    public <O> O getStyle() {
        return this.realBossBar.getMethodObject("getStyle");
    }

    public void setStyle(String styleName) {
        if (!VersionsUtils.is1_9()) return;
        Object style = getBarStyle(styleName);
        if (style == null) return;
        this.realBossBar.callMethod("setStyle", style);
    }

    @Override
    public float getProgress() {
        return this.realBossBar.getMethodObject("getProgress");
    }

    @Override
    public void setProgress(double progress) {
        this.realBossBar.callMethod("setProgress", VersionsUtils.is1_9() ? progress : (float) progress);
    }

    @Override
    public boolean isVisible() {
        return this.realBossBar.getMethodObject("isVisible");
    }

    @Override
    public void setVisible(boolean visible) {
        this.realBossBar.callMethod("setVisible", visible);
    }

    @Override
    public void addPlayer(Player player) {
        if (player != null) this.realBossBar.callMethod("addPlayer", player);
    }

    @Override
    public void removePlayer(Player player) {
        if (player != null) this.realBossBar.callMethod("removePlayer", player);
    }

    @Override
    public void removeAll() {
        this.realBossBar.callMethod("removeAll");
    }

    @Override
    public List<Player> getPlayers() {
        return this.realBossBar.getMethodObject("getPlayers");
    }

    public void addFlag(String flagName) {
        if (!VersionsUtils.is1_9() || flagName == null) return;
        removeFlag(flagName);
        ReflObject<?> barFlag = new ReflObject<>("org.bukkit.boss.BarFlag", false).obtainField(flagName);
        if (barFlag.getObject() == null) return;
        barFlags.add(barFlag);
        this.realBossBar.callMethod("addFlag", barFlag.getObject());
    }

    public void removeFlag(String flagName) {
        if (!VersionsUtils.is1_9() || flagName == null) return;
        new ReflObject<>(barFlags.removeIf(r -> ((String) r.getMethodObject("name")).equalsIgnoreCase(flagName)));
        this.realBossBar.callMethod("removeFlag",
                new ReflObject<>("org.bukkit.boss.BarFlag", false).getMethodObject(flagName));
    }

    public boolean hasFlag(String flagName) {
        if (!VersionsUtils.is1_9() || flagName == null) return false;
        return barFlags.stream()
                .map(r -> r.getMethodObject("name"))
                .anyMatch(s -> ((String) s).equalsIgnoreCase(flagName));
    }

    public ReflObject<?> getRealBossBar() {
        return realBossBar;
    }

    public static void removeAllBossBars() {
        bossBars.forEach(BossBar::removeAll);
    }

    public static <C> C getBarColor(String barColorName) {
        return (C) new ReflObject<>("org.bukkit.boss.BarColor", false).obtainField(barColorName).getObject();
    }

    public static <S> S getBarStyle(String barColorName) {
        return (S) new ReflObject<>("org.bukkit.boss.BarStyle", false).obtainField(barColorName).getObject();
    }

    public static ReflObject<?> createEmptyBossBar() {
        if (VersionsUtils.is1_9()) {
            Object barColor = getBarColor("PURPLE");
            Object barStyle = getBarStyle("SEGMENTED_12");
            Object[] barFlags = new ReflObject<>("org.bukkit.boss.BarFlag", false).getArray(0);
            // Bukkit.createBossBar("", BarColor.PURPLE, BarStyle.SEGMENTED_12);
            return new ReflObject<>(Bukkit.class.getCanonicalName(), false)
                    .callMethod("createBossBar",
                            new Class[]{String.class, barColor.getClass(), barStyle.getClass(), barFlags.getClass()},
                            "", barColor, barStyle, barFlags);
        } else return new ReflObject<>(new LegacyBossBar(""));
    }
}
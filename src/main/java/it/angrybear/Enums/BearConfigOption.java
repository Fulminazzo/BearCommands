package it.angrybear.Enums;

import it.angrybear.Bukkit.Objects.YamlElements.ItemStackYamlObject;
import it.angrybear.Bukkit.Utils.BukkitUtils;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.YamlElements.YamlObject;
import it.angrybear.Utils.StringUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class BearConfigOption extends ClassEnum {
    private final IBearPlugin<?> plugin;
    protected final String path;

    public BearConfigOption(IBearPlugin<?> plugin, String path) {
        this.plugin = plugin;
        this.path = path;
    }

    public String getString() {
        return plugin.getConfiguration().getString(path);
    }

    public String getMessage() {
        return StringUtils.parseMessage(getString());
    }

    public List<String> getStringList() {
        return plugin.getConfiguration().getStringList(path);
    }

    public int getInt() {
        return plugin.getConfiguration().getInt(path);
    }

    public List<Integer> getIntegerList() {
        return plugin.getConfiguration().getIntegerList(path);
    }

    public double getDouble() {
        return plugin.getConfiguration().getDouble(path);
    }

    public List<Double> getDoubleList() {
        return plugin.getConfiguration().getDoubleList(path);
    }

    public boolean getBoolean() {
        return plugin.getConfiguration().getBoolean(path);
    }

    public List<Boolean> getBooleanList() {
        return plugin.getConfiguration().getBooleanList(path);
    }

    public <C> C getSection() {
        return plugin.getConfiguration().getConfigurationSection(path);
    }

    public <I> I getItemStack() {
        try {
            YamlObject<I> itemStackYamlObject = (YamlObject<I>) new ItemStackYamlObject();
            return itemStackYamlObject.load(new Configuration(plugin.getConfiguration()), path);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <O> O getObject() {
        return (O) plugin.getConfiguration().get(path);
    }

    public String getPath() {
        return path;
    }

    public <M> M getMaterial() {
        String materialString = getString();
        if (materialString == null) return null;
        return (M) Arrays.stream(BukkitUtils.getMaterialValues())
                .filter(m -> ((String) new ReflObject<>(m).getMethodObject("name")).equalsIgnoreCase(materialString))
                .findAny().orElse(null);
    }

    public <M> List<M> getMaterialList() {
        return (List<M>) plugin.getConfiguration().getStringList(path).stream()
                .map(n -> Arrays.stream(BukkitUtils.getMaterialValues())
                        .filter(m -> ((String) new ReflObject<>(m).getMethodObject("name")).equalsIgnoreCase(n))
                        .findAny().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<ChatColor> getColorCodes() {
        return getStringList().stream()
                .map(c -> Arrays.stream(ChatColor.values())
                        .filter(v -> v.equals(ChatColor.getByChar(c.charAt(0))))
                        .findAny().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return name();
    }
}
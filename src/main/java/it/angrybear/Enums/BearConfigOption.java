package it.angrybear.Enums;

import it.angrybear.Objects.YamlElements.ItemStackYamlObject;
import it.angrybear.Utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class BearConfigOption extends ClassEnum {
    private final JavaPlugin plugin;
    protected final String path;

    public BearConfigOption(JavaPlugin plugin, String path) {
        this.plugin = plugin;
        this.path = path;
    }

    public String getString() {
        return plugin.getConfig().getString(path);
    }

    public String getMessage() {
        return StringUtils.parseMessage(getString());
    }

    public List<String> getStringList() {
        return plugin.getConfig().getStringList(path);
    }

    public int getInt() {
        return plugin.getConfig().getInt(path);
    }

    public List<Integer> getIntegerList() {
        return plugin.getConfig().getIntegerList(path);
    }

    public double getDouble() {
        return plugin.getConfig().getDouble(path);
    }

    public List<Double> getDoubleList() {
        return plugin.getConfig().getDoubleList(path);
    }

    public boolean getBoolean() {
        return plugin.getConfig().getBoolean(path);
    }

    public List<Boolean> getBooleanList() {
        return plugin.getConfig().getBooleanList(path);
    }

    public ConfigurationSection getSection() {
        return plugin.getConfig().getConfigurationSection(path);
    }

    public ItemStack getItemStack() {
        try {
            ItemStackYamlObject itemStackYamlObject = new ItemStackYamlObject();
            return itemStackYamlObject.load(plugin.getConfig(), path);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <O> O getObject() {
        return (O) plugin.getConfig().get(path);
    }

    public String getPath() {
        return path;
    }

    public Material getMaterial() {
        String materialString = getString();
        if (materialString == null) return null;
        return Arrays.stream(Material.values())
                .filter(m -> m.name().equalsIgnoreCase(materialString))
                .findAny().orElse(null);
    }

    public List<Material> getMaterialList() {
        return plugin.getConfig().getStringList(path).stream()
                .map(n -> Arrays.stream(Material.values()).filter(m -> m.name().equalsIgnoreCase(n)).findAny().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<ChatColor> getColorCodes() {
        return getStringList().stream()
                .map(c -> Arrays.stream(ChatColor.values())
                        .filter(v -> v.equals(ChatColor.getByChar(c)))
                        .findAny().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return name();
    }
}
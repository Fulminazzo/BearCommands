package it.angrybear.Bukkit.Objects.YamlElements;

import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.YamlElements.YamlObject;
import it.angrybear.Objects.YamlPair;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Arrays;

public class InventoryYamlObject extends YamlObject<Inventory> {

    public InventoryYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public InventoryYamlObject(Inventory object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
    }

    @Override
    public Inventory load(Configuration configurationSection, String path) throws Exception {
        Configuration inventorySection = configurationSection.getConfiguration(path);
        if (inventorySection == null) return null;

        String title = inventorySection.getString("title");
        int size = inventorySection.getInt("size");

        YamlObject<ItemStack[]> yamlContents = newObject(ItemStack[].class, yamlPairs);
        ItemStack[] contents = yamlContents.load(inventorySection, "contents");

        Inventory inventory = null;
        if (size % 9 != 0) {
            InventoryType inventoryType = Arrays.stream(InventoryType.values())
                    .filter(t -> t.getDefaultSize() == size)
                    .findAny().orElse(null);
            if (inventoryType != null)
                if (title == null) inventory = Bukkit.createInventory(null, inventoryType);
                else inventory = Bukkit.createInventory(null, inventoryType, title);
        }
        if (inventory == null)
            if (title == null) inventory = Bukkit.createInventory(null, size);
            else inventory = Bukkit.createInventory(null, size, title);
        if (contents != null) inventory.setContents(contents);
        return inventory;
    }

    @Override
    public void dump(Configuration configurationSection, String path) throws Exception {
        configurationSection.set(path, null);
        if (object == null) return;
        Configuration inventorySection = configurationSection.createSection(path);
        int size = object.getSize();

        Method getTitle = ReflUtil.getMethod(object.getClass(), "getTitle", String.class);
        Object title = null;
        if (getTitle != null) title = getTitle.invoke(object);
        inventorySection.set("title", title);
        inventorySection.set("size", size);
        YamlObject<ItemStack[]> yamlContents = YamlObject.newObject(object.getContents(), yamlPairs);
        yamlContents.dump(inventorySection, "contents");
    }
}

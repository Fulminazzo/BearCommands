package it.angrybear.Bukkit.Objects.YamlElements;

import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.YamlElements.CollectionYamlObject;
import it.angrybear.Objects.YamlElements.YamlObject;
import it.angrybear.Objects.YamlPair;
import it.angrybear.Utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemStackYamlObject extends YamlObject<ItemStack> {

    public ItemStackYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public ItemStackYamlObject(ItemStack object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
    }

    @Override
    public ItemStack load(Configuration configurationSection, String path) throws Exception {
        Configuration itemSection = configurationSection.getConfiguration(path);
        if (itemSection == null) return null;

        Material material = Material.valueOf(itemSection.getString("material").toUpperCase());
        Integer amount = (Integer) itemSection.get("amount");
        if (amount == null) amount = 1;
        Integer durability = (Integer) itemSection.get("durability");
        if (durability == null) durability = 0;
        String displayName = itemSection.getString("display-name");
        if (displayName != null && !displayName.trim().isEmpty()) displayName = StringUtils.parseMessage(displayName);
        List<String> lore = itemSection.getStringList("lore").stream()
                .map(StringUtils::parseMessage)
                .collect(Collectors.toList());
        CollectionYamlObject<ItemFlag> flagsObject = newObject(List.class, yamlPairs);
        Collection<ItemFlag> flags = flagsObject.load(configurationSection, "flags");
        Map<Enchantment, Integer> enchants = EnchantmentYamlObject.getEnchantmentsMap().load(itemSection, "enchantments");

        ItemStack itemStack = new ItemStack(material, amount, (short) (int) durability);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            if (displayName != null) itemMeta.setDisplayName(displayName);
            itemMeta.setLore(lore);
            if (flags != null) itemMeta.addItemFlags(flags.toArray(new ItemFlag[0]));
            if (enchants != null) enchants.forEach((e, i) -> itemMeta.addEnchant(e, i, true));
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

    @Override
    public void dump(Configuration configurationSection, String path) throws Exception {
        configurationSection.set(path, null);
        if (object == null) return;
        Configuration itemSection = configurationSection.createSection(path);
        itemSection.set("material", object.getType().toString());
        itemSection.set("amount", object.getAmount());
        itemSection.set("durability", object.getDurability());
        ItemMeta itemMeta = object.getItemMeta();
        if (itemMeta != null) {
            itemSection.set("display-name", itemMeta.getDisplayName());
            itemSection.set("lore", itemMeta.getLore());
            CollectionYamlObject<ItemFlag> flagsObject = YamlObject.newObject(itemMeta.getItemFlags(), yamlPairs);
            flagsObject.dump(itemSection, "flags");
            EnchantmentYamlObject.getEnchantmentsMap(itemMeta.getEnchants()).dump(itemSection, "enchantments");
        }
    }
}

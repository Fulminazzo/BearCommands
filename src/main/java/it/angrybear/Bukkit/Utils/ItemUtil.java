package it.angrybear.Bukkit.Utils;

import it.angrybear.Utils.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {

    public static String getItemName(ItemStack itemStack) {
        if (itemStack == null) return "NULL";
        else {
            ItemMeta itemMeta = itemStack.getItemMeta();
            String displayName = itemMeta == null ? null : itemMeta.getDisplayName();
            if (displayName == null || displayName.isEmpty()) displayName = StringUtils.capitalize(itemStack.getType().name());
            return displayName;
        }
    }
}
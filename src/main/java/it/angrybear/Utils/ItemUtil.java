package it.angrybear.Utils;

import org.bukkit.inventory.ItemStack;

public class ItemUtil {

    public static String getItemName(ItemStack itemStack) {
        if (itemStack == null) return "";
        else {
            String displayName = itemStack.getItemMeta().getDisplayName();
            if (displayName.equals("")) displayName = StringUtils.capitalize(itemStack.getType().name());
            return displayName;
        }
    }
}
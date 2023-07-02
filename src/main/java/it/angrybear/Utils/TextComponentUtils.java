package it.angrybear.Utils;

import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Objects.Reflections.CraftBReflObject;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TextComponentUtils {

    public static String baseComponentsToString(BaseComponent... baseComponents) {
        if (baseComponents == null) return null;
        else return Arrays.stream(baseComponents).map(c -> c == null ? "" : c.toLegacyText()).collect(Collectors.joining());
    }

    public static TextComponent getItemComponent(ItemStack itemStack) throws Exception {
        return getItemComponent(itemStack, true);
    }

    public static TextComponent getItemComponent(ItemStack itemStack, boolean showAmount) throws Exception {
        try {
            TextComponent itemComponent = new TextComponent(getItemName(itemStack, showAmount));
            itemComponent.setHoverEvent(getItemHoverEvent(itemStack));
            return itemComponent;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(BearLoggingMessage.PARSE_ITEM_ERROR.getMessage("%item%", itemStack.toString()));
        }
    }

    public static String getItemName(ItemStack itemStack, boolean showAmount) {
        ItemMeta meta = itemStack.getItemMeta();
        String name = meta == null ? null : meta.getDisplayName();
        if (name == null || name.equals("")) name = StringUtils.capitalize(itemStack.getType().name());
        return ChatColor.translateAlternateColorCodes('&',
                (showAmount && itemStack.getAmount() > 1 ? String.format("&6%sx ", itemStack.getAmount()) : "") +
                        (itemStack.getEnchantments().size() == 0 ? "&f" : "&b")) + name;
    }

    public static HoverEvent getTextHoverEvent(String text) {
        if (VersionsUtils.is1_16()) {
            // Text textComponent = new Text(text);
            ReflObject<?> textComponent = new ReflObject<>("net.md_5.bungee.api.chat.hover.content.Text", StringUtils.parseMessage(text));
            // return new HoverEvent(HoverEvent.Action.SHOW_TEXT, textComponent);
            return new ReflObject<>(HoverEvent.class,
                    new Class<?>[]{HoverEvent.Action.class, new ReflObject<>("net.md_5.bungee.api.chat.hover.content.Content",
                            false).getArray().getClass()},
                    HoverEvent.Action.SHOW_TEXT, textComponent.getArray(textComponent.getObject())).getObject();
        } else return getTextHoverEventLegacy(text);
    }

    public static HoverEvent getTextHoverEventLegacy(String text) {
        // return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(text)});
        return new ReflObject<>(HoverEvent.class, HoverEvent.Action.SHOW_TEXT,
                new BaseComponent[]{new TextComponent(StringUtils.parseMessage(text))}).getObject();
    }

    public static HoverEvent getItemHoverEvent(ItemStack itemStack) throws Exception {
        if (VersionsUtils.is1_16()) {
            String jsonItem = convertItemStackToJson(itemStack);
            if (jsonItem == null) return getItemHoverEventLegacy(itemStack);
            // ItemTag itemTag = ItemTag.ofNbt(jsonItem);
            ReflObject<?> itemTag = new ReflObject<>("net.md_5.bungee.api.chat.ItemTag", false)
                    .callMethod("ofNbt", jsonItem);
            // Item item = new Item("minecraft:" + itemStack.getType().name().toLowerCase(), itemStack.getAmount(), itemTag);
            ReflObject<?> item = new ReflObject<>("net.md_5.bungee.api.chat.hover.content.Item",
                    "minecraft:" + itemStack.getType().name().toLowerCase(), itemStack.getAmount(),
                    itemTag.getObject());
            // return new HoverEvent(HoverEvent.Action.SHOW_ITEM, item);
            return new ReflObject<>(HoverEvent.class,
                    new Class<?>[]{HoverEvent.Action.class, new ReflObject<>("net.md_5.bungee.api.chat.hover.content.Content", false).getArray().getClass()},
                    HoverEvent.Action.SHOW_ITEM, item.getArray(item.getObject())).getObject();
        } else return getItemHoverEventLegacy(itemStack);
    }

    public static HoverEvent getItemHoverEventLegacy(ItemStack itemStack) throws Exception {
        itemStack = itemStack.clone();
        itemStack.setAmount(1);
        String jsonItem = convertItemStackToJsonLegacy(itemStack);
        if (jsonItem == null) throw new Exception("JsonItem was null");

        // return new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(jsonItem)});
        return new ReflObject<>(HoverEvent.class, HoverEvent.Action.SHOW_ITEM,
                new BaseComponent[]{new TextComponent(jsonItem)}).getObject();
    }

    public static String convertItemStackToJson(ItemStack itemStack) {
        // net.minecraft.server.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        ReflObject<?> CraftItemStack = new CraftBReflObject<>("inventory.CraftItemStack", false);
        ReflObject<?> nmsItemStack = CraftItemStack.callMethod("asNMSCopy", new Class[]{ItemStack.class}, itemStack);
        // return nmsItemStack.getTag().toString();
        return nmsItemStack.callMethodFromReturnType(NMSUtils.getNMSClass("NBTTagCompound", "nbt")).toString();
    }

    public static String convertItemStackToJsonLegacy(ItemStack itemStack) {
        // net.minecraft.server.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        ReflObject<?> CraftItemStack = new CraftBReflObject<>("inventory.CraftItemStack", false);
        ReflObject<?> nmsItemStack = CraftItemStack.callMethod("asNMSCopy", new Class[]{ItemStack.class}, itemStack);
        // net.minecraft.nbt.NBTTagCompound compound = new NBTTagCompound();
        Class<?> NBTTagCompound = NMSUtils.getNMSClass("NBTTagCompound", "nbt");
        if (NBTTagCompound == null) return null;
        ReflObject<?> compound = new ReflObject<>(NBTTagCompound.getCanonicalName(), true);
        // compound = nmsItemStack.save(compound);
        compound = nmsItemStack.callMethodFromReturnType(NBTTagCompound, compound.getObject());
        // return compound.toString();
        return compound.toString();
    }
}

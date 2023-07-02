package it.angrybear.Objects.YamlElements;

import it.fulminazzo.reflectionutils.Objects.ReflObject;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public class EnchantmentYamlObject extends YamlObject<Enchantment> {

    public EnchantmentYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public EnchantmentYamlObject(Enchantment object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
    }

    @Override
    public Enchantment load(ConfigurationSection configurationSection, String path) {
        String enchantName = configurationSection.getString(path);
        if (enchantName == null) return null;
        this.object = getEnchantmentFromId(enchantName);
        return object;
    }

    @Override
    public void dump(ConfigurationSection configurationSection, String path) {
        configurationSection.set(path, getEnchantmentId(object));
    }

    public static MapYamlObject<Enchantment, Integer> getEnchantmentsMap() {
        Function<String, Enchantment> getKey = EnchantmentYamlObject::getEnchantmentFromId;
        Function<Enchantment, String> convertKey = EnchantmentYamlObject::getEnchantmentId;
        return new MapYamlObject<>(getKey, convertKey);
    }

    public static MapYamlObject<Enchantment, Integer> getEnchantmentsMap(Map<Enchantment, Integer> enchantments) {
        Function<String, Enchantment> getKey = EnchantmentYamlObject::getEnchantmentFromId;
        Function<Enchantment, String> convertKey = EnchantmentYamlObject::getEnchantmentId;
        return new MapYamlObject<>(enchantments, getKey, convertKey);
    }

    public static Enchantment getEnchantmentFromId(String id) {
        return Arrays.stream(Enchantment.values())
                .filter(e -> getEnchantmentId(e).equalsIgnoreCase(id))
                .findAny().orElse(null);
    }

    public static String getEnchantmentId(Enchantment enchantment) {
        Method getKey = ReflUtil.getMethod(enchantment.getClass(), "getKey", null);
        if (getKey != null)
            return new ReflObject<>(enchantment).callMethod("getKey").getMethodObject("getKey").toString();
        else
            return new ReflObject<>(enchantment).getMethodObject("getName").toString();
    }
}

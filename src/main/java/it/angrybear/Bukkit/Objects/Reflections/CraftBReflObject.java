package it.angrybear.Bukkit.Objects.Reflections;

import it.angrybear.Bukkit.Utils.NMSUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;

public class CraftBReflObject<A> extends ReflObject<A> {
    public CraftBReflObject(String className, Object... params) {
        super(getCraftBClass(className), params);
    }

    public CraftBReflObject(String className, Class<?>[] paramTypes, Object... params) {
        super(getCraftBClass(className), paramTypes, params);
    }

    public CraftBReflObject(String className, boolean initiate) {
        super(getCraftBClass(className), initiate);
    }

    private static String getCraftBClass(String className) {
        Class<?> nmsClass = NMSUtils.getCraftBukkitClass(className);
        if (nmsClass == null) {
            try {
                throw new ClassNotFoundException(String.format("CraftBukkitClass not found. Class name: \"%s\"",
                        className == null ? "null" : className));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else return nmsClass.getCanonicalName();
    }
}

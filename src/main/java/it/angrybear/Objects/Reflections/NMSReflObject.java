package it.angrybear.Objects.Reflections;

import it.angrybear.Utils.NMSUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;

public class NMSReflObject<A> extends ReflObject<A> {
    public NMSReflObject(String className, Object... params) {
        super(getNMSClass(className), params);
    }

    public NMSReflObject(String className, String className117, Object... params) {
        super(getNMSClass(className, className117), params);
    }

    public NMSReflObject(String className, Class<?>[] paramTypes, Object... params) {
        super(getNMSClass(className), paramTypes, params);
    }

    public NMSReflObject(String className, String className117, Class<?>[] paramTypes, Object... params) {
        super(getNMSClass(className, className117), paramTypes, params);
    }

    public NMSReflObject(String className, boolean initiate) {
        super(getNMSClass(className), initiate);
    }

    public NMSReflObject(String className, String className117, boolean initiate) {
        super(getNMSClass(className, className117), initiate);
    }

    private static String getNMSClass(String className) {
        return getNMSClass(className, "");
    }
    
    private static String getNMSClass(String className, String className117) {
        Class<?> nmsClass = NMSUtils.getNMSClass(className, className117);
        if (nmsClass == null) {
            try {
                throw new ClassNotFoundException(String.format("NMSClass not found. Class name: \"%s\"; Class name 117: \"%s\"", 
                        className == null ? "null" : className,
                        className117 == null ? "null" : className117));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else return nmsClass.getCanonicalName();
    }
}

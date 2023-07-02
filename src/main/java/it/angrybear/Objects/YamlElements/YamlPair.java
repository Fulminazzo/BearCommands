package it.angrybear.Objects.YamlElements;

@SuppressWarnings("unchecked")
public class YamlPair<O> {
    private final Class<O> objectClass;
    private final Class<? extends YamlObject<O>> yamlClass;

    public YamlPair(Class<O> objectClass, Class<?> yamlClass) {
        this.objectClass = objectClass;
        this.yamlClass = (Class<? extends YamlObject<O>>) yamlClass;
    }

    public Class<O> getObjectClass() {
        return objectClass;
    }

    public Class<? extends YamlObject<O>> getYamlClass() {
        return yamlClass;
    }

    @Override
    public String toString() {
        return String.format("{%s -> %s}",
                objectClass == null ? "null" : objectClass.getSimpleName(),
                yamlClass == null ? "null" : yamlClass.getSimpleName());
    }
}
package it.angrybear.objects.configurations;

import it.angrybear.interfaces.IConfiguration;
import it.angrybear.objects.yamlelements.SerializableYamlParser;
import it.angrybear.objects.yamlelements.YamlParser;
import it.angrybear.utils.ClassUtils;
import it.angrybear.utils.FileUtils;
import it.fulminazzo.reflectionutils.objects.ReflObject;
import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Represents a YAML File configuration.
 */
public class FileConfiguration extends SimpleConfiguration {
    @Getter
    private final static LinkedList<YamlParser<?>> parsers = new LinkedList<>();
    private final File file;

    public FileConfiguration(String path) {
        this(new File(path));
    }

    public FileConfiguration(File file) {
        super("", null);
        this.file = file.getAbsoluteFile();
        Map<Object, Object> yaml;
        try {
            yaml = newYaml().load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.map.putAll(IConfiguration.generalToConfigMap(this, yaml));
        addParsers("it.angrybear.objects.yamlelements");
    }

    public FileConfiguration(InputStream inputStream) {
        this(null, inputStream);
    }

    public FileConfiguration(File file, InputStream inputStream) {
        super("", null);
        this.file = file == null ? null : file.getAbsoluteFile();
        Map<Object, Object> yaml = newYaml().load(inputStream);
        this.map.putAll(IConfiguration.generalToConfigMap(this, yaml));
        addParsers("it.angrybear.objects.yamlelements");
    }

    /**
     * Gets parent.
     *
     * @return the parent
     */
    @Override
    public IConfiguration getParent() {
        return null;
    }

    /**
     * Saves the configuration to the file.
     */
    public void save() {
        try {
            if (!file.exists()) FileUtils.createNewFile(file);
            FileWriter writer = new FileWriter(file);
            newYaml().dump(IConfiguration.configToGeneralMap(this), writer);
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the YAML with parameters.
     */
    public static Yaml newYaml() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer(dumperOptions);
        return new Yaml(representer);
    }

    /**
     * Add all the parsers present in a package.
     *
     * @param packageName the package name
     */
    public static void addParsers(String packageName) {
        addParsers(getParsersFromPackage(packageName).toArray(new YamlParser[0]));
    }

    /**
     * Add parsers.
     *
     * @param yamlParsers the YAML parsers
     */
    public static void addParsers(YamlParser<?>... yamlParsers) {
        for (YamlParser<?> yamlParser : yamlParsers)
            if (yamlParser != null && parsers.stream().noneMatch(p -> p.getOClass().equals(yamlParser.getOClass())))
                parsers.addLast(yamlParser);
    }

    /**
     * Remove parsers.
     *
     * @param packageName the package name
     */
    public static void removeParsers(String packageName) {
        removeParsers(getParsersFromPackage(packageName).toArray(new YamlParser[0]));
    }

    /**
     * Remove all the parsers present in a package.
     *
     * @param yamlParsers the YAML parsers
     */
    public static void removeParsers(YamlParser<?>... yamlParsers) {
        for (YamlParser<?> yamlParser : yamlParsers)
            if (yamlParser != null) parsers.removeIf(p -> p.getOClass().equals(yamlParser.getOClass()));
    }

    /**
     * Gets all the parsers from package.
     *
     * @param packageName the package name
     * @return the parsers from package
     */
    @SuppressWarnings("unchecked")
    public static List<YamlParser<?>> getParsersFromPackage(String packageName) {
        Set<Class<?>> classes = ClassUtils.findClassesInPackage(packageName);
        List<YamlParser<?>> yamlParsers = new ArrayList<>();
        for (Class<?> clazz : classes)
            if (YamlParser.class.isAssignableFrom(clazz))
                try {
                    clazz.getConstructor();
                    if (Modifier.isFinal(clazz.getModifiers()) || Modifier.isAbstract(clazz.getModifiers())) continue;
                    ReflObject<YamlParser<?>> parserReflObject = new ReflObject<YamlParser<?>>((Class<YamlParser<?>>) clazz);
                    YamlParser<?> parser = parserReflObject.getObject();
                    if (parser != null) yamlParsers.add(parser);
                } catch (NoSuchMethodException ignored) {}
        return yamlParsers;
    }

    /**
     * Gets the parser from the associated class.
     *
     * @param <O>    the type parameter
     * @param oClass the class
     * @return the parser
     */
    @SuppressWarnings("unchecked")
    public static <O> YamlParser<O> getParser(Class<O> oClass) {
        return oClass == null ? null : (YamlParser<O>) getParsers().stream()
                .filter(p -> p.getOClass().isAssignableFrom(oClass))
                .findFirst().orElse(null);
    }

    public static LinkedList<YamlParser<?>> getParsers() {
        parsers.removeIf(s -> s instanceof SerializableYamlParser);
        parsers.add(new SerializableYamlParser());
        return parsers;
    }

    @Override
    public String toString() {
        return String.format("{file: %s, non-null: %s}",
                file == null ? null : file.getAbsolutePath(), nonNull);
    }
}

package it.angrybear.utils;

import it.fulminazzo.reflectionutils.utils.ReflUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

public class ClassUtils {

    /**
     * Finds all the classes from the given package.
     *
     * @param packageName the package name
     * @return the set of classes
     */
    public static Set<Class<?>> findClassesInPackage(String packageName)  {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replaceAll("\\.", File.separator);
        try {
            FileInputStream fileInputStream = new FileInputStream(JarUtils.getCurrentJarName());
            JarInputStream inputStream = new JarInputStream(fileInputStream);
            JarEntry entry;
            while ((entry = inputStream.getNextJarEntry()) != null) {
                String className = entry.getName();
                if (!className.endsWith(".class")) continue;
                if (!className.startsWith(path)) continue;
                className = className.replace("/", ".");
                className = className.split(".class")[0];
                classes.add(ReflUtil.getClass(className));
            }
            inputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        classes = classes.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(c -> -ReflUtil.getClassAndSuperClasses(c).length))
                .collect(Collectors.toList());
        return new LinkedHashSet<>(classes);
    }
}

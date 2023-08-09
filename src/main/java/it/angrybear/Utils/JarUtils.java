package it.angrybear.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JarUtils {
    public static InputStream getJarFile(File file, String path) {
        JarFile jarFile = getJar(file);
        if (jarFile == null) return null;
        else return getJarFile(jarFile, path);
    }

    public static InputStream getJarFile(JarFile jar, String path) {
        try {
            ZipEntry jarEntry = jar.getEntry(path);
            return jar.getInputStream(jarEntry);
        } catch (Exception e) {
            return null;
        }
    }

    public static JarFile getJarFile(Class<?> jarClass) {
        return getJar(jarClass.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    public static JarFile getJar(String jarPath) {
        return getJar(new File(jarPath));
    }

    public static JarFile getJar(File jarFile) {
        try {
            if (jarFile != null && jarFile.isFile()) return new JarFile(jarFile);
            else return null;
        } catch (IOException e) {
            return null;
        }
    }
}
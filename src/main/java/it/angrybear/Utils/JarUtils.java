package it.angrybear.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JarUtils {

    /**
     * Returns the file at the specified path from the given File.
     * @param file: the File (should be a .jar);
     * @param path: the path.
     * @return an InputStream with the file contents (if found).
     */
    public static InputStream getJarFile(File file, String path) {
        JarFile jarFile = getJar(file);
        if (jarFile == null) return null;
        else return getJarFile(jarFile, path);
    }

    /**
     * Returns the file at the specified path from the given JarFile.
     * @param jar: the JarFile;
     * @param path: the path.
     * @return an InputStream with the file contents (if found).
     */
    public static InputStream getJarFile(JarFile jar, String path) {
        try {
            ZipEntry jarEntry = jar.getEntry(path);
            return jar.getInputStream(jarEntry);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns an instance of JarFile from the given Class.
     * @param jarClass: the Class.
     * @return the corresponding JarFile, if present.
     */
    public static JarFile getJarFile(Class<?> jarClass) {
        return getJar(jarClass.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    /**
     * Returns an instance of JarFile from the given path.
     * @param jarPath: the String path.
     * @return the corresponding JarFile, if present.
     */
    public static JarFile getJar(String jarPath) {
        return getJar(new File(jarPath));
    }

    /**
     * Returns an instance of JarFile from the given File.
     * @param jarFile: the File.
     * @return the corresponding JarFile, if present.
     */
    public static JarFile getJar(File jarFile) {
        try {
            if (jarFile != null && jarFile.isFile()) return new JarFile(jarFile);
            else return null;
        } catch (IOException e) {
            return null;
        }
    }
}
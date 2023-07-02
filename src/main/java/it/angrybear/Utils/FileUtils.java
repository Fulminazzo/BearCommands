package it.angrybear.Utils;

import it.angrybear.Enums.BearLoggingMessage;

import java.io.*;
import java.util.Scanner;

public class FileUtils {
    public static void writeToFile(File output, InputStream inputStream) throws IOException {
        if (!output.exists()) createNewFile(output);
        FileOutputStream outputStream = new FileOutputStream(output);
        if (inputStream != null) {
            while (inputStream.available() > 0) {
                byte[] tmp = new byte[Math.min(inputStream.available(), 8192)];
                if (inputStream.read(tmp) == -1) break;
                outputStream.write(tmp);
            }
            inputStream.close();
        }
        outputStream.close();
    }

    public static void createNewFile(File file) throws IOException {
        if (!file.getParentFile().exists()) createFolder(file.getParentFile());
        if (!file.createNewFile())
            throw new IOException(BearLoggingMessage.FILE_CREATE_ERROR.getMessage("%file%", file.getName()));
    }

    public static void createFolder(File folder) throws IOException {
        File parent = folder.getParentFile();
        if (parent != null && !parent.exists()) createFolder(parent);
        if (!folder.mkdir())
            throw new IOException(BearLoggingMessage.FOLDER_CREATE_ERROR.getMessage("%folder%", folder.getName()));
    }

    public static void copyFile(File file1, File file2) throws IOException {
        if (!file1.exists()) return;
        if (!file2.exists()) createNewFile(file2);
        FileInputStream inputStream = new FileInputStream(file1);
        FileOutputStream outputStream = new FileOutputStream(file2);
        while (inputStream.available() > 0) {
            byte[] tmp = new byte[Math.min(inputStream.available(), 8192)];
            if (inputStream.read(tmp) == -1) break;
            outputStream.write(tmp);
        }
        inputStream.close();
        outputStream.close();
    }

    public static void renameFile(File fileFrom, File fileTo) throws IOException {
        if (!fileFrom.renameTo(fileTo))
            throw new IOException(BearLoggingMessage.FILE_RENAME_ERROR.getMessage("%file%", fileFrom.getName()));
    }

    public static void deleteFile(File file) throws IOException {
        if (!file.delete())
            throw new IOException(BearLoggingMessage.FILE_DELETE_ERROR.getMessage("%file%", file.getName()));
    }

    public static void deleteFolder(File folder) throws IOException {
        recursiveDelete(folder);
    }

    private static void recursiveDelete(File folder) throws IOException {
        File[] allContents = folder.listFiles();
        if (allContents != null)
            for (File file : allContents)
                if (file.isDirectory()) recursiveDelete(file);
                else deleteFile(file);
        if (!folder.delete())
            throw new IOException(BearLoggingMessage.FOLDER_DELETE_ERROR.getMessage("%folder%", folder.getName()));
    }

    public static boolean compareTwoFiles(File file1, File file2) {
        try {
            Scanner fileScanner1 = new Scanner(file1);
            Scanner fileScanner2 = new Scanner(file2);
            while (fileScanner1.hasNextLine() && fileScanner2.hasNextLine())
                if (!fileScanner1.nextLine().equalsIgnoreCase(fileScanner2.nextLine()))
                    return false;
            return !fileScanner1.hasNextLine() && !fileScanner2.hasNextLine();
        } catch (FileNotFoundException e) {
            return false;
        }
    }
}
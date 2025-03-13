package net.vansen.fursconfig.file;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Basic file reader for FursConfig.
 */
public class FileTextReader {

    /**
     * Reads the file at the given path.
     *
     * @param path the path to the file
     * @return the contents of the file
     */
    public static String read(@NotNull Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the file.
     *
     * @param file the file to read
     * @return the contents of the file
     */
    public static String read(@NotNull File file) {
        return read(file.toPath());
    }

    /**
     * Reads the path file.
     *
     * @param path the path to the file
     * @return the contents of the file
     */
    public static String read(@NotNull String path) {
        return read(Paths.get(path));
    }
}

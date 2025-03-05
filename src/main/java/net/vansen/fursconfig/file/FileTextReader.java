package net.vansen.fursconfig.file;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileTextReader {

    public static String read(@NotNull Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String read(@NotNull File file) {
        return read(file.toPath());
    }

    public static String read(@NotNull String path) {
        return read(Paths.get(path));
    }
}

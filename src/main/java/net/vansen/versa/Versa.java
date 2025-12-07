package net.vansen.versa;

import net.vansen.fursconfig.file.FileTextReader;
import net.vansen.versa.node.Node;
import net.vansen.versa.parser.VersaParser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

@SuppressWarnings("unused")
public final class Versa {

    private Versa() {
    }

    /**
     * Parse configuration from a raw text string.
     */
    public static Node parseText(@NotNull String text) {
        return new VersaParser(text).parse();
    }

    /**
     * Read and parse configuration from a file path.
     */
    public static Node parse(@NotNull String file) {
        return parseText(FileTextReader.read(file));
    }

    /**
     * Read and parse configuration from a {@link File}.
     */
    public static Node parse(@NotNull File file) {
        return parseText(FileTextReader.read(file));
    }

    /**
     * Read and parse configuration from a {@link Path}.
     */
    public static Node parse(@NotNull Path path) {
        return parseText(FileTextReader.read(path));
    }
}
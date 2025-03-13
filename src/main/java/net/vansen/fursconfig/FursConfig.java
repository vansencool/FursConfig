package net.vansen.fursconfig;

import net.vansen.fursconfig.file.FileTextReader;
import net.vansen.fursconfig.lang.Lexer;
import net.vansen.fursconfig.lang.Node;
import net.vansen.fursconfig.lang.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Main class for FursConfig
 */
@SuppressWarnings({"unused", "unchecked"})
public class FursConfig {

    /**
     * The root, top-most node of the parsed configuration tree.
     */
    private Node root = new Node();

    /**
     * Creates a new instance of FursConfig.
     * This does not parse any input!
     *
     * @return a new FursConfig instance
     */
    public static FursConfig create() {
        return new FursConfig();
    }

    /**
     * Creates a new instance of FursConfig and parses the given input string.
     *
     * @param input the input string to parse
     * @return a new FursConfig instance with the parsed input
     */
    public static FursConfig createAndParse(@NotNull String input) {
        FursConfig config = new FursConfig();
        config.parse(input);
        return config;
    }

    /**
     * Creates a new instance of FursConfig and parses the contents of the given file.
     *
     * @param file the file to read and parse
     * @return a new FursConfig instance with the parsed file contents
     */
    public static FursConfig createAndParseFile(@NotNull File file) {
        return createAndParse(FileTextReader.read(file));
    }

    /**
     * Creates a new instance of FursConfig and parses the contents of the file at the given path.
     *
     * @param path the path to the file to read and parse
     * @return a new FursConfig instance with the parsed file contents
     */
    public static FursConfig createAndParseFile(@NotNull String path) {
        return createAndParse(FileTextReader.read(path));
    }

    /**
     * Creates a new instance of FursConfig and parses the contents of the file at the given path.
     *
     * @param path the path to the file to read and parse
     * @return a new FursConfig instance with the parsed file contents
     */
    public static FursConfig createAndParseFile(@NotNull Path path) {
        return createAndParse(FileTextReader.read(path));
    }

    /**
     * Creates a new instance of FursConfig from the given node.
     *
     * @param node the node to create the FursConfig from
     * @return a new FursConfig instance with the given node as the root
     */
    public static FursConfig from(@NotNull Node node) {
        FursConfig config = new FursConfig();
        config.root = node;
        return config;
    }

    /**
     * Retrieves the value at the given path.
     *
     * @param path the path to the value to retrieve
     * @return the value at the given path, or null if not found
     */
    public Object get(@NotNull String path) {
        return root.get(path);
    }

    /**
     * Retrieves the double value at the given path.
     *
     * @param path the path to the value to retrieve
     * @return the double value at the given path
     * @throws RuntimeException if the value cannot be cast to a double
     */
    public double getDouble(@NotNull String path) {
        return cast(path, Double.class);
    }

    /**
     * Retrieves the float value at the given path.
     *
     * @param path the path to the value to retrieve
     * @return the float value at the given path
     * @throws RuntimeException if the value cannot be cast to a float
     */
    public float getFloat(@NotNull String path) {
        return Float.parseFloat(String.valueOf(cast(path, Double.class)));
    }

    /**
     * Retrieves the int value at the given path.
     *
     * @param path the path to the value to retrieve
     * @return the int value at the given path
     * @throws RuntimeException if the value cannot be cast to an int
     */
    public int getInt(@NotNull String path) {
        return cast(path, Integer.class);
    }

    /**
     * Retrieves the long value at the given path.
     *
     * @param path the path to the value to retrieve
     * @return the long value at the given path
     * @throws RuntimeException if the value cannot be cast to a long
     */
    public long getLong(@NotNull String path) {
        return cast(path, Long.class);
    }

    /**
     * Retrieves the boolean value at the given path.
     *
     * @param path the path to the value to retrieve
     * @return the boolean value at the given path
     * @throws RuntimeException if the value cannot be cast to a boolean
     */
    public boolean getBoolean(@NotNull String path) {
        return cast(path, Boolean.class);
    }

    /**
     * Retrieves the string value at the given path.
     *
     * @param path the path to the value to retrieve
     * @return the string value at the given path
     * @throws RuntimeException if the value cannot be cast to a string
     */
    public String getString(@NotNull String path) {
        return cast(path, String.class);
    }

    /**
     * Retrieves the list value at the given path.
     *
     * @param path the path to the value to retrieve
     * @return the list value at the given path
     * @throws RuntimeException if the value cannot be cast to a list
     */
    public List<Object> getList(@NotNull String path) {
        return cast(path, List.class);
    }

    /**
     * Retrieves the list value at the given path.
     *
     * @param path  the path to the value to retrieve
     * @param clazz the class of the list elements
     * @param <T>   the type of the list elements
     * @return the list value at the given path
     * @throws RuntimeException if the value cannot be cast to a list
     */
    public <T> List<T> getList(@NotNull String path, @NotNull Class<T> clazz) {
        return (List<T>) cast(path, List.class);
    }

    /**
     * Retrieves the config (branch) at the given path.
     *
     * @param path the path to retrieve from
     * @return the config (branch) at the given path
     * @throws RuntimeException if the value cannot be cast to a node
     */
    public FursConfig getFursConfig(@NotNull String path) {
        Node node = cast(path, Node.class);
        FursConfig config = create();
        config.root = node;
        return config;
    }

    /**
     * Retrieves the value at the given path, or the default value if not found.
     *
     * @param path         the path to the value to retrieve
     * @param defaultValue the default value to return if not found
     * @return the value at the given path, or the default value if not found
     */
    public Object get(@NotNull String path, @Nullable Object defaultValue) {
        Object value = root.get(path);
        return value != null ? value : defaultValue;
    }

    /**
     * Retrieves the double value at the given path, or the default value if not found.
     *
     * @param path         the path to the value to retrieve
     * @param defaultValue the default value to return if not found
     * @return the double value at the given path, or the default value if not found
     */
    public double getDouble(@NotNull String path, double defaultValue) {
        return cast(path, Double.class, defaultValue);
    }

    /**
     * Retrieves the float value at the given path, or the default value if not found.
     *
     * @param path         the path to the value to retrieve
     * @param defaultValue the default value to return if not found
     * @return the float value at the given path, or the default value if not found
     */
    public float getFloat(@NotNull String path, float defaultValue) {
        // Couldn't figure out a way to do this in a single line
        try {
            return getFloat(path);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves the int value at the given path, or the default value if not found.
     *
     * @param path         the path to the value to retrieve
     * @param defaultValue the default value to return if not found
     * @return the int value at the given path, or the default value if not found
     */
    public int getInt(@NotNull String path, int defaultValue) {
        return cast(path, Integer.class, defaultValue);
    }

    /**
     * Retrieves the long value at the given path, or the default value if not found.
     *
     * @param path         the path to the value to retrieve
     * @param defaultValue the default value to return if not found
     * @return the long value at the given path, or the default value if not found
     */
    public long getLong(@NotNull String path, long defaultValue) {
        return cast(path, Long.class, defaultValue);
    }

    /**
     * Retrieves the boolean value at the given path, or the default value if not found.
     *
     * @param path         the path to the value to retrieve
     * @param defaultValue the default value to return if not found
     * @return the boolean value at the given path, or the default value if not found
     */
    public boolean getBoolean(@NotNull String path, boolean defaultValue) {
        return cast(path, Boolean.class, defaultValue);
    }

    /**
     * Retrieves the string value at the given path, or the default value if not found.
     *
     * @param path         the path to the value to retrieve
     * @param defaultValue the default value to return if not found
     * @return the string value at the given path, or the default value if not found
     */
    public String getString(@NotNull String path, @Nullable String defaultValue) {
        return cast(path, String.class, defaultValue);
    }

    /**
     * Retrieves the list value at the given path, or the default value if not found.
     *
     * @param path         the path to the value to retrieve
     * @param defaultValue the default value to return if not found
     * @return the list value at the given path, or the default value if not found
     */
    public List<Object> getList(@NotNull String path, @Nullable List<Object> defaultValue) {
        return cast(path, List.class, defaultValue);
    }

    /**
     * Retrieves the list value at the given path, or the default value if not found.
     *
     * @param path         the path to the value to retrieve
     * @param clazz        the class of the list elements
     * @param defaultValue the default value to return if not found
     * @param <T>          the type of the list elements
     * @return the list value at the given path, or the default value if not found
     */
    public <T> List<T> getList(@NotNull String path, @NotNull Class<T> clazz, @Nullable List<T> defaultValue) {
        return (List<T>) cast(path, List.class, defaultValue);
    }

    /**
     * Retrieves the config (branch) at the given path, or the default value if not found.
     *
     * @param path         the path to retrieve from
     * @param defaultValue the default value to return if not found
     * @return the config (branch) at the given path, or the default value if not found
     */
    public FursConfig getFursConfig(@NotNull String path, @NotNull FursConfig defaultValue) {
        Node node = cast(path, Node.class, defaultValue.root);
        FursConfig config = create();
        config.root = node;
        return config;
    }

    /**
     * Retrieves the config (branch) at the given path, or the default value if not found.
     *
     * @param path         the path to retrieve from
     * @param defaultValue the default value to return if not found
     * @return the config (branch) at the given path, or the default value if not found
     */
    public FursConfig getFursConfig(@NotNull String path, @NotNull Node defaultValue) {
        Node node = cast(path, Node.class, defaultValue);
        FursConfig config = create();
        config.root = node;
        return config;
    }

    /**
     * Checks if the given path exists.
     *
     * @param path the path to check
     * @return true if the path exists, false otherwise
     */
    public boolean hasPath(@NotNull String path) {
        return root.get(path) != null;
    }

    /**
     * Parses the given input string, which should be before doing any get methods.
     *
     * @param input the input string to parse
     */
    public void parse(@NotNull String input) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        root.children.clear();
        parser.parse(root);
    }

    private <T> T cast(@NotNull String path, @NotNull Class<T> clazz, @Nullable T defaultValue) {
        try {
            return clazz.cast(root.get(path));
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    private <T> T cast(@NotNull String path, @NotNull Class<T> clazz) {
        try {
            return clazz.cast(root.get(path));
        } catch (ClassCastException e) {
            throw new RuntimeException("Value at path '" + path + "' cannot be cast to " + clazz.getName(), e);
        }
    }
}
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
@Deprecated
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
     * @throws NumberFormatException if the value cannot be parsed to a double
     */
    public double getDouble(@NotNull String path) {
        return Double.parseDouble(root.get(path).toString());
    }

    /**
     * Retrieves the float value at the given path.
     *
     * @param path the path to the value to retrieve
     * @return the float value at the given path
     * @throws NumberFormatException if the value cannot be parsed to a float
     */
    public float getFloat(@NotNull String path) {
        return Float.parseFloat(String.valueOf(root.get(path)));
    }

    /**
     * Retrieves the int value at the given path.
     *
     * @param path the path to the value to retrieve
     * @return the int value at the given path
     * @throws NumberFormatException if the value cannot be parsed to an int
     */
    public int getInt(@NotNull String path) {
        return Integer.parseInt(root.get(path).toString());
    }

    /**
     * Retrieves the long value at the given path.
     *
     * @param path the path to the value to retrieve
     * @return the long value at the given path
     * @throws NumberFormatException if the value cannot be parsed to a long
     */
    public long getLong(@NotNull String path) {
        return Long.parseLong(root.get(path).toString());
    }

    /**
     * Retrieves the boolean value at the given path.
     *
     * @param path the path to the value to retrieve
     * @return the boolean value at the given path
     */
    public boolean getBoolean(@NotNull String path) {
        return Boolean.parseBoolean(root.get(path).toString());
    }

    /**
     * Retrieves the string value at the given path.
     *
     * @param path the path to the value to retrieve
     * @return the string value at the given path
     */
    public String getString(@NotNull String path) {
        return String.valueOf(root.get(path));
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
     * Retrieves the node at the given path.
     *
     * @param path the path to retrieve from
     * @return the node at the given path
     * @throws RuntimeException if the value cannot be cast to a node
     */
    public Node getNode(@NotNull String path) {
        return cast(path, Node.class);
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
        try {
            return getDouble(path);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves the float value at the given path, or the default value if not found.
     *
     * @param path         the path to the value to retrieve
     * @param defaultValue the default value to return if not found
     * @return the float value at the given path, or the default value if not found
     */
    public float getFloat(@NotNull String path, float defaultValue) {
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
        try {
            return getInt(path);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves the long value at the given path, or the default value if not found.
     *
     * @param path         the path to the value to retrieve
     * @param defaultValue the default value to return if not found
     * @return the long value at the given path, or the default value if not found
     */
    public long getLong(@NotNull String path, long defaultValue) {
        try {
            return getLong(path);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves the boolean value at the given path, or the default value if not found.
     *
     * @param path         the path to the value to retrieve
     * @param defaultValue the default value to return if not found
     * @return the boolean value at the given path, or the default value if not found
     */
    public boolean getBoolean(@NotNull String path, boolean defaultValue) {
        try {
            return getBoolean(path);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves the string value at the given path, or the default value if not found.
     *
     * @param path         the path to the value to retrieve
     * @param defaultValue the default value to return if not found
     * @return the string value at the given path, or the default value if not found
     */
    public String getString(@NotNull String path, @Nullable String defaultValue) {
        try {
            return getString(path);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves the list value at the given path, or the default value if not found.
     *
     * @param path         the path to the value to retrieve
     * @param defaultValue the default value to return if not found
     * @return the list value at the given path, or the default value if not found
     */
    public List<Object> getList(@NotNull String path, @Nullable List<Object> defaultValue) {
        try {
            return getList(path);
        }
        catch (Exception e) {
            return defaultValue;
        }
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
        try {
            return getList(path, clazz);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves the config (branch) at the given path, or the default value if not found.
     *
     * @param path         the path to retrieve from
     * @param defaultValue the default value to return if not found
     * @return the config (branch) at the given path, or the default value if not found
     */
    public FursConfig getFursConfig(@NotNull String path, @NotNull FursConfig defaultValue) {
        try {
            return getFursConfig(path);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves the config (branch) at the given path, or the default value if not found.
     *
     * @param path         the path to retrieve from
     * @param defaultValue the default value to return if not found
     * @return the config (branch) at the given path, or the default value if not found
     */
    public FursConfig getFursConfig(@NotNull String path, @NotNull Node defaultValue) {
        Node node;
        try {
            node = getNode(path);
        }
        catch (Exception e) {
            node = defaultValue;
        }
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
    public Node getNode(@NotNull String path, @NotNull Node defaultValue) {
        try {
            return getNode(path);
        }
        catch (Exception e) {
            return defaultValue;
        }
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

    private <T> T cast(@NotNull String path, @NotNull Class<T> clazz) {
        try {
            return clazz.cast(root.get(path));
        } catch (ClassCastException e) {
            throw new RuntimeException("Value at path '" + path + "' cannot be cast to " + clazz.getName(), e);
        }
    }
}
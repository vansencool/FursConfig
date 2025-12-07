package net.vansen.versa.annotations.adapter;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry holding all custom adapters used by the loader.
 * Required to support custom objects inside config files.
 *
 * <p>Register once (usually at startup):</p>
 * <pre>{@code
 * Adapters.register(World.class, new WorldAdapter());
 * }</pre>
 *
 * <p>Usage inside config fields:</p>
 * <pre>{@code
 * @ConfigPath("world") public static World world = new World("Earth", 1);
 * }</pre>
 */
public final class Adapters {

    private static final Map<Class<?>, ConfigAdapter<?>> map = new HashMap<>();

    /**
     * Register a converter for a custom type.
     */
    public static <T> void register(Class<T> type, @NotNull ConfigAdapter<T> adapter) {
        map.put(type, adapter);
    }

    /**
     * Retrieves a previously registered adapter for usage.
     */
    @SuppressWarnings("unchecked")
    public static <T> ConfigAdapter<T> get(@NotNull Class<T> type) {
        return (ConfigAdapter<T>) map.get(type);
    }
}
package net.vansen.versa.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Assigns a config key or nested path to a field/branch.
 * Supports nested form (`server.host`) or class-scoped usage.
 *
 * <pre>{@code
 * @ConfigFile("config.versa")
 * public class AppConfig {
 *
 *     @ConfigPath("server.host")
 *     public static String host = "127.0.0.1";
 *
 *     @ConfigPath("server")
 *     @Branch
 *     public static Server server = new Server();
 *
 *     public static class Server {
 *          @ConfigPath("port") public int port = 25565;
 *     }
 * }
 * }</pre>
 *
 * Output:
 * <pre>
 * server {
 *     host = "127.0.0.1"
 *     port = 25565
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ConfigPath {

    /**
     * The key/path used while reading and writing configuration.
     */
    @NotNull String value();
}
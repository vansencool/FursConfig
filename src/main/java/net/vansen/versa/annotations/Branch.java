package net.vansen.versa.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a configuration **branch**, meaning its type
 * represents a nested configuration section instead of a scalar value.
 * The field must be an object containing other config fields annotated with {@link ConfigPath} or {@link Branch}.
 * <p>
 * This is how nested config structures are generated:
 *
 * <pre>{@code
 * @ConfigFile("config.versa")
 * public class MyConfig {
 *
 *     @Branch
 *     @ConfigPath("server")
 *     public static Server server = new Server();
 *
 *     public static class Server {
 *         @ConfigPath("host") public String host = "127.0.0.1";
 *         @ConfigPath("port") public int port = 25565;
 *     }
 * }
 * }</pre>
 * <p>
 * Produces:
 * <pre>
 * server {
 *     host = "127.0.0.1"
 *     port = 25565
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Branch {
}
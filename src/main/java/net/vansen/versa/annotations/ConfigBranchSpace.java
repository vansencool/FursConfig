package net.vansen.versa.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Controls spacing (blank lines) around a branch block.
 * Can be applied on:
 * - The branch class itself
 * - The field containing the branch
 *
 * <pre>{@code
 * @Branch
 * @ConfigPath("server")
 * @ConfigBranchSpace(before = true, after = true)
 * public static Server server = new Server();
 *
 * public static class Server {
 *     @ConfigPath("port") public int port = 25565;
 * }
 * }</pre>
 * <p>
 * Output:
 * <pre>
 *
 * server {
 *     port = 25565
 * }
 *
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface ConfigBranchSpace {

    /**
     * Insert a blank line **before the branch block**.
     */
    boolean before() default false;

    /**
     * Insert a blank line **after the branch block**.
     */
    boolean after() default false;
}
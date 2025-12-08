package net.vansen.versa.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds block-level comments **inside** a branch.
 * - {@code start} appears directly after the opening brace `{`
 * - {@code end} appears just before the closing brace `}`
 *
 * <pre>{@code
 * @Branch
 * @ConfigPath("database")
 * @ConfigBranchComment(start="Database section", end="End of DB settings")
 * public static class Database {
 *     @ConfigPath("user") public String user = "root";
 *     @ConfigPath("pass") public String pass = "1234";
 * }
 * }</pre>
 *
 * Result:
 * <pre>
 * database {  # Database section
 *     user = "root"
 *     pass = "1234"
 * } # End of DB settings
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigBranchComment {

    /**
     * Comment placed **after opening brace** of the section.
     */
    @NotNull String start() default "";

    /**
     * Comment placed **after closing brace** of the section.
     */
    @NotNull String end() default "";
}
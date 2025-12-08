package net.vansen.versa.annotations;

import net.vansen.versa.annotations.loader.ConfigLoader;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a configuration container loaded by {@link ConfigLoader}.
 * File path is relative and will be generated automatically if missing.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigFile {
    /**
     * @return path to config file on disk
     */
    @NotNull String value();
}
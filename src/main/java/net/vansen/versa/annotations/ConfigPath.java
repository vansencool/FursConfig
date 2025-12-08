package net.vansen.versa.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a static field inside a @ConfigFile class as mapped to a config key.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigPath {
    /**
     * @return config key / node path used for reading & writing
     */
    @NotNull String value();
}
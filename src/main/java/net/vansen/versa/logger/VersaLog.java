package net.vansen.versa.logger;

import org.jetbrains.annotations.NotNull;

/**
 * Lightweight ANSI-colored console logger used internally by Versa.
 */
@SuppressWarnings("unused")
public final class VersaLog {

    private static final String R = "\u001B[0m";
    private static final String I = "\u001B[36m";
    private static final String W = "\u001B[33m";
    private static final String E = "\u001B[31m";
    private static boolean enabled = true;

    private VersaLog() {
    }

    /**
     * Prints an informational message in cyan.
     *
     * @param src module or class name label
     * @param msg message content
     */
    public static void info(@NotNull String src, @NotNull String msg) {
        out(I, src, msg);
    }

    /**
     * Prints a warning message in yellow.
     *
     * @param src module or class name label
     * @param msg message content
     */
    public static void warn(@NotNull String src, @NotNull String msg) {
        out(W, src, msg);
    }

    /**
     * Prints an error message in red.
     *
     * @param src module or class name label
     * @param msg message content
     */
    public static void error(@NotNull String src, @NotNull String msg) {
        out(E, src, msg);
    }

    /**
     * Globally enables or disables logging.
     *
     * @param b true to enable, false to silence all logs
     */
    public static void setEnabled(boolean b) {
        enabled = b;
    }

    private static void out(@NotNull String color, @NotNull String src, @NotNull String msg) {
        if (!enabled) return;
        String p = src.length() < 14 ? src + " ".repeat(14 - src.length()) : src;
        System.out.println(color + "VERSA :: " + p + " -> " + msg + R);
    }
}

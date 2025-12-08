package net.vansen.versa.comments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a comment stored inside a node or attached to values/branches.
 * Can be either using `//` or `#` depending on {@link #slash}.
 */
public class Comment {

    /** Comment category such as inline, branch start/end or standalone line. */
    public CommentType type;

    /** Raw comment text content without prefix symbols. */
    public String text;

    /** True = print using //, false = print using # */
    public boolean slash;

    /**
     * Creates a comment defaulting to // format.
     *
     * @param t   type of comment
     * @param s   comment text (nullable for empty/omitted)
     */
    public Comment(@NotNull CommentType t, @Nullable String s) {
        type = t;
        text = s;
        slash = true;
    }

    /**
     * Creates a comment with explicit prefix format.
     *
     * @param t   type of comment
     * @param s   comment text (nullable for empty/omitted)
     * @param sl  true = use // prefix, false = use #
     */
    public Comment(@NotNull CommentType t, @Nullable String s, boolean sl) {
        type = t;
        text = s;
        slash = sl;
    }

    /**
     * Converts this comment into formatted representation.
     *
     * @return formatted comment, or empty string when no text exists
     */
    public @NotNull String toString() {
        return (text == null || text.isEmpty()) ? "" : (slash ? " //" : " #") + text;
    }
}
package net.vansen.versa.comments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Comment {
    public CommentType type;
    public String text;
    public boolean slash;

    public Comment(@NotNull CommentType t, @Nullable String s) {
        type = t;
        text = s;
        slash = true;
    }

    public Comment(@NotNull CommentType t, @Nullable String s, boolean sl) {
        type = t;
        text = s;
        slash = sl;
    }

    public String toString() {
        return (text == null || text.isEmpty()) ? "" : (slash ? " //" : " #") + text;
    }
}
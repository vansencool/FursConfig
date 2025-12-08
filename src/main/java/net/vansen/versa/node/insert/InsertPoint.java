package net.vansen.versa.node.insert;

import net.vansen.versa.comments.Comment;
import net.vansen.versa.comments.CommentType;
import net.vansen.versa.node.Node;
import net.vansen.versa.node.entry.Entry;
import net.vansen.versa.node.entry.EntryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an insertion cursor inside a {@link Node}'s structure.
 * <p>
 * Used for advanced formatting and editing of configs without rebuilding nodes.
 * Allows placement of comments or blank lines <b>before or after</b> existing entries
 * using {@code node.before("key")}, {@code node.after("key")}, etc.
 * <p>
 * Each operation returns a new InsertPoint with an updated index so multiple
 * insert actions can be chained.
 *
 * <pre>
 * Node n = ...
 * n.before("port")
 *     .comment(" database port comment")
 *     .emptyLine()
 *     .commentHash(" another note");
 * </pre>
 */
@SuppressWarnings("unused")
public record InsertPoint(@NotNull Node node, int index) {

    /**
     * Inserts a `//` comment at this exact position.
     *
     * @param text comment text without prefix (leading space optional)
     * @return new insert point after the inserted comment
     */
    public @NotNull InsertPoint comment(@Nullable String text) {
        node.order.add(index, new Entry(EntryType.COMMENT, new Comment(CommentType.COMMENT_LINE, text)));
        return new InsertPoint(node, index + 1);
    }

    /**
     * Inserts a `#` comment at this exact position.
     *
     * @param text comment text without prefix
     * @return new insert point after the inserted comment
     */
    @SuppressWarnings("UnusedReturnValue")
    public @NotNull InsertPoint commentHash(@Nullable String text) {
        node.order.add(index, new Entry(EntryType.COMMENT, new Comment(CommentType.COMMENT_LINE, text, false)));
        return new InsertPoint(node, index + 1);
    }

    /**
     * Inserts a blank/empty line.
     *
     * @return new insert point after the blank line
     */
    public @NotNull InsertPoint emptyLine() {
        node.order.add(index, new Entry(EntryType.EMPTY_LINE, ""));
        return new InsertPoint(node, index + 1);
    }

    /**
     * @return true if this insert position is currently at the end of node.order.
     */
    public boolean isInsertingAtTheEnd() {
        return node.order.size() == index;
    }
}
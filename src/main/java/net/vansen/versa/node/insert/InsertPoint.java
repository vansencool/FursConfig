package net.vansen.versa.node.insert;

import net.vansen.versa.comments.Comment;
import net.vansen.versa.comments.CommentType;
import net.vansen.versa.node.Node;
import net.vansen.versa.node.entry.Entry;
import net.vansen.versa.node.entry.EntryType;

@SuppressWarnings("unused")
public record InsertPoint(Node node, int index) {

    /**
     * Inserts a line comment at this point.
     */
    public InsertPoint comment(String text) {
        node.order.add(index, new Entry(EntryType.COMMENT, new Comment(CommentType.COMMENT_LINE, text)));
        return new InsertPoint(node, index + 1);
    }

    /**
     * Inserts a line comment using # instead of //.
     */
    @SuppressWarnings("UnusedReturnValue")
    public InsertPoint commentHash(String text) {
        node.order.add(index, new Entry(EntryType.COMMENT, new Comment(CommentType.COMMENT_LINE, text, false)));
        return new InsertPoint(node, index + 1);
    }

    /**
     * Inserts a blank line.
     */
    public InsertPoint emptyLine() {
        node.order.add(index, new Entry(EntryType.EMPTY_LINE, ""));
        return new InsertPoint(node, index + 1);
    }
}
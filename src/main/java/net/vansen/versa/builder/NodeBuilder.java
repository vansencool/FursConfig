package net.vansen.versa.builder;

import net.vansen.versa.comments.Comment;
import net.vansen.versa.comments.CommentType;
import net.vansen.versa.node.Node;
import net.vansen.versa.node.Value;
import net.vansen.versa.node.entry.Entry;
import net.vansen.versa.node.entry.EntryType;

/**
 * Builds a {@link Node} (configuration branch).
 * Nodes store values, child nodes, comments, blank lines, and formatting order.
 * <p>
 * Example full config construction:
 *
 * <pre>
 * Node config = new NodeBuilder()
 *     .name("database")
 *     .comment("Connection settings")
 *     .add(new ValueBuilder().name("host").string("localhost"))
 *     .add(new ValueBuilder().name("port").intVal(3306))
 *     .emptyLine()
 *     .child(
 *         new NodeBuilder()
 *             .name("pool")
 *             .add(new ValueBuilder().name("size").intVal(10))
 *             .build()
 *     )
 *     .build();
 *
 * System.out.println(config);
 * </pre>
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class NodeBuilder {
    private final Node n = new Node();

    public static NodeBuilder builder() {
        return new NodeBuilder();
    }

    public NodeBuilder name(String name) {
        n.name = name;
        return this;
    }

    /**
     * Adds a built value to the node
     */
    public NodeBuilder add(Value v) {
        n.values.put(v.name, v);
        n.order.add(new Entry(EntryType.VALUE, v));
        return this;
    }

    /**
     * Shortcut for builder
     */
    public NodeBuilder add(ValueBuilder vb) {
        return add(vb.build());
    }

    /**
     * Adds a nested config branch
     */
    public NodeBuilder child(Node c) {
        n.children.add(c);
        n.order.add(new Entry(EntryType.BRANCH, c));
        return this;
    }

    /**
     * Adds a // comment as a full standalone line.
     * NOTE: If you want a space after the slashes, begin the text with a leading space.
     * <pre>
     * .comment(" This is a comment")  -> // This is a comment
     * .comment("NoSpace")            -> //NoSpace
     * </pre>
     */
    public NodeBuilder comment(String text) {
        Comment c = new Comment(CommentType.COMMENT_LINE, text, true);
        n.order.add(new Entry(EntryType.COMMENT, c));
        return this;
    }

    /**
     * Adds a # comment as a full standalone line.
     * NOTE: If you want a space after '#', begin the text with a leading space.
     * <pre>
     * .commentHash(" This uses hash") -> # This uses hash
     * .commentHash("NoSpace")         -> #NoSpace
     * </pre>
     */
    public NodeBuilder commentHash(String text) {
        Comment c = new Comment(CommentType.COMMENT_LINE, text, false);
        n.order.add(new Entry(EntryType.COMMENT, c));
        return this;
    }

    /**
     * Adds a start-branch inline comment after `{` using `//`.
     * Appears as:  section { // comment
     * If you want a space after //, begin with a space in text.
     * <pre>
     * .startComment(" Text") -> { // Text
     * .startComment("NoSpace")-> { //NoSpace
     * </pre>
     */
    public NodeBuilder startComment(String text) {
        n.inlineComments.add(new Comment(CommentType.START_BRANCH, text, true));
        return this;
    }

    /**
     * Adds a start-branch inline comment using `#`.
     * Appears as:  section { #comment
     * <pre>
     * .startCommentHash(" Text") -> { # Text
     * .startCommentHash("No")    -> { #No
     * </pre>
     */
    public NodeBuilder startCommentHash(String text) {
        n.inlineComments.add(new Comment(CommentType.START_BRANCH, text, false));
        return this;
    }

    /**
     * Adds an end-branch inline comment after `}` using `//`.
     * Printed as: } // comment
     * <pre>
     * .endComment(" Text") -> } // Text
     * .endComment("No")    -> } //No
     * </pre>
     */
    public NodeBuilder endComment(String text) {
        n.inlineComments.add(new Comment(CommentType.END_BRANCH, text, true));
        return this;
    }

    /**
     * Adds an end-branch inline comment using `#`.
     * Printed as: } #comment
     * <pre>
     * .endCommentHash(" Text") -> } # Text
     * .endCommentHash("No")    -> } #No
     * </pre>
     */
    public NodeBuilder endCommentHash(String text) {
        n.inlineComments.add(new Comment(CommentType.END_BRANCH, text, false));
        return this;
    }

    /**
     * Inserts a blank line
     */
    public NodeBuilder emptyLine() {
        n.order.add(new Entry(EntryType.EMPTY_LINE, ""));
        return this;
    }

    public Node build() {
        return n;
    }
}
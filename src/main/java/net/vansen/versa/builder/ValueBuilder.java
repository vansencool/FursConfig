package net.vansen.versa.builder;

import net.vansen.versa.comments.Comment;
import net.vansen.versa.comments.CommentType;
import net.vansen.versa.node.Node;
import net.vansen.versa.node.Value;
import net.vansen.versa.node.value.ValueType;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Builds a {@link Value} in a simple fluent format.
 * Set a name, assign a value type, optionally add inline comments,
 *
 * <pre>
 * Value v = ValueBuilder.builder()
 *     .name("enabled")
 *     .bool(true)
 *     .comment(CommentType.INLINE_VALUE, "Toggle system feature")
 *     .build();
 * </pre>
 */
@SuppressWarnings("unused")
public class ValueBuilder {
    private final Value val = new Value();

    public static ValueBuilder builder() {
        return new ValueBuilder();
    }

    public ValueBuilder name(String n) {
        val.name = n;
        return this;
    }

    public ValueBuilder bool(boolean b) {
        val.type = ValueType.BOOL;
        val.iv = b ? 1 : 0;
        return this;
    }

    public ValueBuilder intVal(long i) {
        val.type = ValueType.INT;
        val.iv = i;
        return this;
    }

    public ValueBuilder longVal(long l) {
        val.type = ValueType.LONG;
        val.iv = l;
        return this;
    }

    public ValueBuilder floatVal(double f) {
        val.type = ValueType.FLOAT;
        val.dv = f;
        return this;
    }

    public ValueBuilder doubleVal(double d) {
        val.type = ValueType.DOUBLE;
        val.dv = d;
        return this;
    }

    public ValueBuilder string(String s) {
        val.type = ValueType.STRING;
        val.sv = s;
        return this;
    }

    /**
     * Creates a list value using other values.
     *
     * <pre>
     * Value list = new ValueBuilder()
     *     .name("items")
     *     .list(
     *         new ValueBuilder().string("a").build(),
     *         new ValueBuilder().string("b").build()
     *     ).build();
     * </pre>
     */
    public ValueBuilder list(Value... vs) {
        val.type = ValueType.LIST;
        val.list = new ArrayList<>();
        Collections.addAll(val.list, vs);
        return this;
    }

    /**
     * Creates a list containing child branches instead of values.
     *
     * <pre>
     * Node worlds = new NodeBuilder()
     *     .name("worlds")
     *     .add(new ValueBuilder()
     *         .name("dimensions")
     *         .branches(
     *             new NodeBuilder().name("overworld").build(),
     *             new NodeBuilder().name("nether").build()
     *         )
     *     )
     *     .build();
     * </pre>
     */
    public ValueBuilder branches(Node... ns) {
        val.type = ValueType.LIST_OF_BRANCHES;
        val.branchList = new ArrayList<>();
        Collections.addAll(val.branchList, ns);
        return this;
    }

    /**
     * Adds a comment to the value.
     * Only INLINE_VALUE is currently used during printing.
     *
     * <pre>
     * .comment(CommentType.INLINE_VALUE, "explanation")
     * </pre>
     */
    public ValueBuilder comment(CommentType t, String text) {
        val.comments.add(new Comment(t, text));
        return this;
    }

    public Value build() {
        return val;
    }
}
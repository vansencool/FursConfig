package net.vansen.versa.node;

import net.vansen.versa.comments.Comment;
import net.vansen.versa.comments.CommentType;
import net.vansen.versa.node.entry.Entry;
import net.vansen.versa.node.entry.EntryType;
import net.vansen.versa.node.insert.InsertPoint;
import net.vansen.versa.node.value.ValueType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <h2>Node - A configuration section/block with values, child nodes and layout awareness</h2>
 *
 * <p>
 * A {@code Node} is one part of a hierarchical configuration tree in Versa configs.
 * It stores:
 * </p>
 *
 * <ul>
 *     <li><b>key/value pairs</b> — <code>host = "localhost"</code></li>
 *     <li><b>child nodes</b> — nested sections/tables</li>
 *     <li><b>inline comments + line comments</b></li>
 *     <li><b>empty lines for spacing</b></li>
 *     <li><b>print order</b> so layout is preserved on save</li>
 * </ul>
 *
 * <p>
 * Most config formats only parse → data → reformat everything,
 * losing comments and structure in the process.
 * <b>Node keeps the layout intact</b> so configs can be edited programmatically
 * without destroying human readability.
 * </p>
 *
 * <hr>
 * <h3>Quick Example — building + printing a config</h3>
 *
 * <pre><code>
 * Node root = new Node();
 * root.name = "database";
 *
 * root.addLineComment(" Connection settings")
 *     .setValue("host", "localhost")
 *     .setValue("port", 3306)
 *     .setValueComment("host", " Recommended: 127.0.0.1 or localhost")
 *     .emptyLine();
 *
 * Node pool = new Node();
 * pool.name = "pool";
 * pool.setValue("size", 10);
 *
 * root.addBranch(pool);
 *
 * root.before("port").comment(" Port of the database");
 * root.after("host").emptyLine();
 *
 * root.beforeBranch("pool").comment(" Database pool settings");
 *
 * pool.before("size").emptyLine();
 * pool.before("size").comment(" E 1");
 * pool.after("size").comment(" E 2");
 *
 * root.save(Path.of("config.versa"));
 * </code></pre>
 *
 * <p><b>Generated output:</b></p>
 *
 * <pre><code>
 * // Connection settings
 * host = "localhost" // Recommended: 127.0.0.1 or localhost
 *
 * // Port of the database
 * port = 3306
 *
 * // Database pool settings
 * pool {
 *
 *     // E 1
 *     size = 10
 *     // E 2
 * }
 * </code></pre>
 *
 * <hr>
 * <h3>Parsing from text using Versa</h3>
 * <p>
 * The {@code Versa} class is the entry-point for parsing config strings/files.
 * It reads Versa configuration format and produces a {@code Node} tree that preserves
 * layout as closely as possible (comments, blank lines, order, etc).
 * </p>
 *
 * <pre><code>
 * Node root = Versa.parse("config.versa"); // Parse to Node tree
 *
 * // Modify it
 * root.setValue("debug", true);
 *
 * // Write back (format remains similar to original)
 * root.save(Path.of("config.versa"));
 * </code></pre>
 *
 * <p>
 * Round-trip similarity is usually ~95–99%. The small difference comes mostly from
 * indentation normalization, since Versa intentionally reformats indentation rather
 * than preserving the user's original spacing style.
 * </p>
 *
 *
 * <hr>
 * <h3>Inline comments</h3>
 *
 * <pre><code>
 * root.setValue("max_players", 100);
 * root.setValueComment("max_players", " Soft limit");
 * </code></pre>
 *
 * <pre><code>
 * max_players = 100 // Soft limit
 * </code></pre>
 *
 *
 * <hr>
 * <h3>Ordering utilities</h3>
 * <p>These let you insert things relative to existing nodes/values.</p>
 *
 * <ul>
 *   <li><code>before("key")</code> → insert before value</li>
 *   <li><code>after("key")</code> → insert after value</li>
 *   <li><code>beforeBranch("name")</code> → insert before child node</li>
 *   <li><code>afterBranch("name")</code> → insert after child node</li>
 * </ul>
 *
 * <pre><code>
 * root.before("port").comment(" Port must stay above host");
 * root.after("host").emptyLine();
 * </code></pre>
 *
 *
 * <hr>
 * <h3>Tips</h3>
 * <ul>
 *     <li>Leading space after <code>//</code> makes comments nicer</li>
 *     <li><code>.emptyLine()</code> helps keep configs readable</li>
 *     <li>Root is always unnamed</li>
 * </ul>
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Node {
    /**
     * Name of this branch. May be empty.
     */
    public String name = "";

    /**
     * Map of values in this branch, keyed by name.
     */
    public Map<String, Value> values = new LinkedHashMap<>();

    /**
     * Child branches directly under this node.
     */
    public List<Node> children = new ArrayList<>();

    /**
     * Stores comments that are attached to this node rather than being standalone lines.
     * <p>
     * Examples:
     * <pre>
     * key = 10 // inline value comment
     *
     * section { // start-branch comment
     *     ...
     * } // end-branch comment
     * </pre>
     * <p>
     * Notes:<br>
     * • These comments are printed only when the node or value itself is printed.<br>
     * • Standalone comments belong in {@link #order} as ENTRY.COMMENT instead.<br>
     * • This list does not insert new lines, it attaches to existing elements.
     */
    public List<Comment> inlineComments = new ArrayList<>();

    /**
     * Ordered view of this node's contents for printing.
     */
    public List<Entry> order = new ArrayList<>();

    /**
     * Returns the first child branch with the given name, or {@code null} if none.
     */
    public Node getBranch(String n) {
        for (Node c : children) if (c.name.equals(n)) return c;
        return null;
    }

    /**
     * Searches this node <b>and all of its child nodes</b> recursively for a value
     * with the given key.
     * <pre><code>
     * root {
     *     version = "1.0"
     *     database {
     *         host = "localhost"
     *         pool {
     *             size = 10
     *         }
     *     }
     * }
     *
     * root.getValueDeep("host")  → "localhost"
     * root.getValueDeep("size")  → 10
     * root.getValueDeep("version") → "1.0"
     * root.getValueDeep("missing") → null
     * </code></pre>
     *
     * @param key name of the value to search for
     * @return the first matching {@code Value}, or {@code null} if not found anywhere
     */
    public Value getValueFromAnywhere(String key) {
        Value v = values.get(key);
        if (v != null) return v;
        for (Node c : children) {
            v = c.getValueFromAnywhere(key);
            if (v != null) return v;
        }
        return null;
    }

    /**
     * Resolves a value using a <b>dot-based path</b>, walking through child nodes.
     * Similar to <code>database.pool.size</code> lookup in config files.
     * <p>
     * Path rules:
     *
     * <ul>
     *     <li><b>"value"</b> → looks in this node only</li>
     *     <li><b>"child.value"</b> → search subtree one level deep</li>
     *     <li><b>"a.b.c.value"</b> → walk nodes until final key</li>
     * </ul>
     *
     * <pre><code>
     * database {
     *     host = "localhost"
     *     pool {
     *         size = 10
     *     }
     * }
     *
     * getValuePath("host")            → "localhost"
     * getValuePath("pool.size")       → 10
     * getValuePath("database.pool.size") → works if called on root
     * getValuePath("foo.bar")         → null (branch doesn't exist)
     * getValuePath("size")            → null unless on 'pool' node
     * </code></pre>
     *
     * @param path a dot-separated lookup like <code>"branch.sub.value"</code>
     * @return the {@code Value} if path resolves, otherwise {@code null}
     */
    public Value getValue(@NotNull String path) {
        String[] parts = path.split("\\.");
        Node n = this;
        for (int i = 0; i < parts.length; i++) {
            if (i == parts.length - 1) return n.values.get(parts[i]);
            Node next = null;
            for (Node c : n.children)
                if (c.name.equals(parts[i])) {
                    next = c;
                    break;
                }
            if (next == null) return null;
            n = next;
        }
        return null;
    }

    /**
     * Returns true if a dotted path resolves to a value within this node hierarchy.
     */
    public boolean hasPath(@NotNull String path) {
        return getValue(path) != null;
    }

    /**
     * Returns true if any value with this key exists anywhere in this node or childrens.
     */
    public boolean hasKey(@NotNull String key) {
        return getValueFromAnywhere(key) != null;
    }

    /**
     * Returns a string using a dot-path lookup.
     */
    public String getString(@NotNull String path) {
        Value v = getValue(path);
        return v == null ? null : v.asString();
    }

    /**
     * Returns an integer using a dot-path lookup
     */
    public Integer getInteger(@NotNull String path) {
        Value v = getValue(path);
        return v == null ? null : v.asInt();
    }

    /**
     * Returns a long using a dot-path lookup.
     */
    public Long getLong(@NotNull String path) {
        Value v = getValue(path);
        return v == null ? null : v.asLong();
    }

    /**
     * Returns a double using a dot-path lookup.
     */
    public Double getDouble(@NotNull String path) {
        Value v = getValue(path);
        return v == null ? null : v.asDouble();
    }

    /**
     * Returns a boolean using a dot-path lookup.
     */
    public Boolean getBool(@NotNull String path) {
        Value v = getValue(path);
        return v == null ? null : v.asBool();
    }

    /**
     * Returns a list of raw Value items from a dot-path lookup.
     */
    public List<Value> getList(@NotNull String path) {
        Value v = getValue(path);
        return v == null ? null : v.asList();
    }

    /**
     * Returns a list of Node branches from a dot-path lookup.
     */
    public List<Node> getBranchList(@NotNull String path) {
        Value v = getValue(path);
        return v == null ? null : v.asBranchList();
    }

    /**
     * Returns a string list from a dot-path lookup.
     */
    public List<String> getStringList(@NotNull String path) {
        List<Value> l = getList(path);
        if (l == null) return null;
        List<String> out = new ArrayList<>();
        for (Value v : l) out.add(v.asString());
        return out;
    }

    /**
     * Returns an integer list from a dot-path lookup.
     */
    public List<Integer> getIntegerList(@NotNull String path) {
        List<Value> l = getList(path);
        if (l == null) return null;
        List<Integer> out = new ArrayList<>();
        for (Value v : l) out.add(v.asInt());
        return out;
    }

    /**
     * Returns a string from path or the default if missing.
     */
    public String getString(@NotNull String path, String def) {
        Value v = getValue(path);
        return v == null ? def : v.asString();
    }

    /**
     * Returns an integer from path or a default.
     */
    public int getInteger(@NotNull String path, int def) {
        Value v = getValue(path);
        return v == null ? def : v.asInt();
    }

    /**
     * Returns a long from path or a default.
     */
    public long getLong(@NotNull String path, long def) {
        Value v = getValue(path);
        return v == null ? def : v.asLong();
    }

    /**
     * Returns a double from path or fallback.
     */
    public double getDouble(@NotNull String path, double def) {
        Value v = getValue(path);
        return v == null ? def : v.asDouble();
    }

    /**
     * Returns a boolean from path or fallback.
     */
    public Boolean getBool(@NotNull String path, boolean def) {
        Value v = getValue(path);
        return v == null ? def : v.asBool();
    }

    /**
     * Returns a list from path or default list.
     */
    public List<Value> getList(@NotNull String path, List<Value> def) {
        Value v = getValue(path);
        return v == null ? def : v.asList();
    }

    /**
     * Returns a branch list or default if missing.
     */
    public List<Node> getBranchList(@NotNull String path, List<Node> def) {
        Value v = getValue(path);
        return v == null ? def : v.asBranchList();
    }

    /**
     * Returns a string list or a fallback list.
     * Converts internal Value list into pure strings.
     */
    public List<String> getStringList(@NotNull String path, List<String> def) {
        List<Value> l = getList(path);
        if (l == null) return def;
        List<String> out = new ArrayList<>();
        for (Value v : l) out.add(v.asString());
        return out;
    }

    /**
     * Returns an integer list or fallback.
     * Converts Value list into plain integers.
     */
    public List<Integer> getIntegerList(@NotNull String path, List<Integer> def) {
        List<Value> l = getList(path);
        if (l == null) return def;
        List<Integer> out = new ArrayList<>();
        for (Value v : l) out.add(v.asInt());
        return out;
    }

    /**
     * Sets or replaces a typed comment entry on this node.
     * This is for meta comments, not directly printed unless wired into {@link #order}.
     */
    public Node setComment(CommentType t, String txt) {
        inlineComments.removeIf(c -> c.type == t);
        inlineComments.add(new Comment(t, txt));
        return this;
    }

    /**
     * Sets a simple value by name using a Java object.
     * Supported types: {@link Boolean}, {@link Integer}, {@link Long},
     * {@link Float}, {@link Double}, {@link String}.
     * The value is added to {@link #values} and appended into {@link #order}.
     */
    public Node setValue(String name, Object v) {
        Value val = new Value();
        val.name = name;
        if (v instanceof Boolean b) {
            val.type = ValueType.BOOL;
            val.iv = b ? 1 : 0;
        } else if (v instanceof Integer i) {
            val.type = ValueType.INT;
            val.iv = i;
        } else if (v instanceof Long l) {
            val.type = ValueType.LONG;
            val.iv = l;
        } else if (v instanceof Float f) {
            val.type = ValueType.FLOAT;
            val.dv = f;
        } else if (v instanceof Double d) {
            val.type = ValueType.DOUBLE;
            val.dv = d;
        } else if (v instanceof String s) {
            val.type = ValueType.STRING;
            val.sv = s;
        }
        values.put(name, val);
        order.add(new Entry(EntryType.VALUE, val));
        return this;
    }

    /**
     * Sets or replaces a comment on an existing value, the comment will always be a inlined comment ("example = 5 // comment").
     */
    public Node setValueComment(String key, String txt) {
        Value v = values.get(key);
        if (v != null) {
            v.comments.removeIf(c -> c.type == CommentType.INLINE_VALUE);
            v.comments.add(new Comment(CommentType.INLINE_VALUE, txt));
        }
        return this;
    }

    /**
     * Adds a printed line comment at the current position in {@link #order}.
     */
    public Node addLineComment(String text) {
        order.add(new Entry(EntryType.COMMENT, new Comment(CommentType.COMMENT_LINE, text)));
        return this;
    }

    /**
     * Adds an empty line at the current position in {@link #order}.
     */
    public Node emptyLine() {
        order.add(new Entry(EntryType.EMPTY_LINE, ""));
        return this;
    }

    /**
     * Attaches an inline comment after the opening `{` of this branch.
     * Uses // or # depending on the boolean flag (true = slash).
     */
    public Node addStartComment(String text, boolean slash) {
        inlineComments.add(new Comment(CommentType.START_BRANCH, text, slash));
        return this;
    }

    /**
     * Attaches an inline comment after the closing `}` of this branch.
     * Uses // or # depending on the boolean flag (true = slash).
     */
    public Node addEndComment(String text, boolean slash) {
        inlineComments.add(new Comment(CommentType.END_BRANCH, text, slash));
        return this;
    }

    /**
     * Same as {@link #addStartComment(String, boolean)} but defaults to //.
     */
    public Node addStartComment(String text) {
        return addStartComment(text, true);
    }

    /**
     * Same as {@link #addEndComment(String, boolean)} but defaults to //.
     */
    public Node addEndComment(String text) {
        return addEndComment(text, true);
    }

    /**
     * Adds an inline comment after the opening `{` of a child branch.
     * Does nothing if the branch does not exist.
     */
    public Node addStartCommentTo(String branch, String text, boolean slash) {
        for (Node n : children) {
            if (n.name.equals(branch)) {
                n.inlineComments.add(new Comment(CommentType.START_BRANCH, text, slash));
                return this;
            }
        }
        return this;
    }

    /**
     * Adds an inline comment after the closing `}` of a child branch.
     * Does nothing if the branch does not exist.
     */
    public Node addEndCommentTo(String branch, String text, boolean slash) {
        for (Node n : children) {
            if (n.name.equals(branch)) {
                n.inlineComments.add(new Comment(CommentType.END_BRANCH, text, slash));
                return this;
            }
        }
        return this;
    }

    /**
     * Short form of {@link #addStartCommentTo(String, String, boolean)}
     * using // as default comment marker.
     */
    public Node addStartCommentTo(String branch, String text) {
        return addStartCommentTo(branch, text, true);
    }

    /**
     * Short form of {@link #addEndCommentTo(String, String, boolean)}
     * using // as default comment marker.
     */
    public Node addEndCommentTo(String branch, String text) {
        return addEndCommentTo(branch, text, true);
    }

    /**
     * Adds a child branch and appends it to {@link #order}.
     */
    public Node addBranch(Node child) {
        children.add(child);
        order.add(new Entry(EntryType.BRANCH, child));
        return this;
    }

    /**
     * Find insert index before a value with this key.
     */
    public InsertPoint before(String key) {
        for (int i = 0; i < order.size(); i++) {
            Entry e = order.get(i);
            if (e.t == EntryType.VALUE && ((Value) e.o).name.equals(key))
                return new InsertPoint(this, i);
        }
        return new InsertPoint(this, order.size());
    }

    /**
     * Find insert index after a value with this key.
     */
    public InsertPoint after(String key) {
        for (int i = 0; i < order.size(); i++) {
            Entry e = order.get(i);
            if (e.t == EntryType.VALUE && ((Value) e.o).name.equals(key))
                return new InsertPoint(this, i + 1);
        }
        return new InsertPoint(this, order.size());
    }

    /**
     * Insert before a branch by name.
     */
    public InsertPoint beforeBranch(String name) {
        for (int i = 0; i < order.size(); i++) {
            Entry e = order.get(i);
            if (e.t == EntryType.BRANCH && ((Node) e.o).name.equals(name))
                return new InsertPoint(this, i);
        }
        return new InsertPoint(this, order.size());
    }

    /**
     * Insert after a branch by name.
     */
    public InsertPoint afterBranch(String name) {
        for (int i = 0; i < order.size(); i++) {
            Entry e = order.get(i);
            if (e.t == EntryType.BRANCH && ((Node) e.o).name.equals(name))
                return new InsertPoint(this, i + 1);
        }
        return new InsertPoint(this, order.size());
    }

    /**
     * Renders this node and its children to a configuration string,
     * using the given indentation depth.
     */
    public String toString(int depth) {
        String pad = "    ".repeat(depth);
        StringBuilder sb = new StringBuilder();

        for (Entry e : order) {

            if (e.t == EntryType.EMPTY_LINE) {
                sb.append("\n");
                continue;
            }

            if (e.t == EntryType.COMMENT) {
                Comment c = (Comment) e.o;
                sb.append(pad)
                        .append(c.slash ? "//" : "#")
                        .append(c.text)
                        .append("\n");
                continue;
            }

            if (e.t == EntryType.VALUE) {
                Value v = (Value) e.o;
                sb.append(pad)
                        .append(v.name)
                        .append(v.assign == ':' ? ": " : " = ")
                        .append(v);

                for (Comment c : v.comments)
                    if (c.type == CommentType.INLINE_VALUE)
                        sb.append(" ").append(c.slash ? "//" : "#").append(c.text);

                sb.append("\n");
                continue;
            }

            if (e.t == EntryType.BRANCH) {
                Node ch = (Node) e.o;

                sb.append(pad).append(ch.name).append(" {");

                for (Comment c : ch.inlineComments)
                    if (c.type == CommentType.START_BRANCH)
                        sb.append(" ").append(c.slash ? "//" : "#").append(c.text);

                sb.append("\n");
                sb.append(ch.toString(depth + 1));
                sb.append(pad).append("}");

                for (Comment c : ch.inlineComments)
                    if (c.type == CommentType.END_BRANCH)
                        sb.append(" ").append(c.slash ? "//" : "#").append(c.text);

                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Renders this node as a configuration string.
     */
    @Override
    public String toString() {
        return toString(0);
    }

    /**
     * Writes this node to a file using UTF-8 encoding.
     */
    public void save(File file) {
        try {
            Files.writeString(file.toPath(), toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Writes this node to a path using UTF-8 encoding.
     */
    public void save(Path path) {
        try {
            Files.writeString(path, toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
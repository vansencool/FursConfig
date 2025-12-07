package net.vansen.versa.utils;

import net.vansen.versa.comments.Comment;
import net.vansen.versa.node.Node;
import net.vansen.versa.node.Value;
import net.vansen.versa.node.entry.Entry;
import net.vansen.versa.node.entry.EntryType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("unused")
public class NodeMerge {

    /**
     * <p>Creates a new config structured like <b>fresh</b> (the latest template),
     * while inserting values from <b>user</b> where possible.</p>
     *
     * <p><b>Purpose:</b> Upgrading configs between versions.</p>
     *
     * <p>Rules:</p>
     * <ul>
     *   <li>User value exists → use user's</li>
     *   <li>User modified value → keep user's</li>
     *   <li>Key exists only in fresh → add it</li>
     *   <li>Branch exists in both → merge recursively</li>
     *   <li>Output format replicates fresh layout & ordering</li>
     * </ul>
     *
     * <p><b>Example:</b> (user old config) -> (fresh new config)</p>
     * <pre><code>
     * // user.conf (old)
     * database {
     *     host = "localhost"     ← user changed the value
     *     port = 2942            ← user changed the value
     * }
     *
     * // fresh.conf (new version)
     * database {
     *     host = "127.0.0.1"     ← default
     *     port = 3306            ← default
     *     timeout = 60000        ← new key
     *
     *     pool {                 ← new branch
     *         size = 10
     *     }
     * }
     *
     * // mergeNodes(user, fresh)
     *
     * database {
     *     host = "localhost"     ← USER preserved
     *     port = 2942            ← USER preserved
     *     timeout = 60000        ← NEW added
     *
     *     pool {                 ← NEW branch added
     *         size = 10
     *     }
     * }
     * </code></pre>
     */
    public static Node mergeNodes(@NotNull Node user, @NotNull Node fresh) {
        Node out = new Node();
        out.name = fresh.name;

        for (Entry e : fresh.order) {
            if (e.t == EntryType.EMPTY_LINE) {
                out.order.add(new Entry(EntryType.EMPTY_LINE, ""));
                continue;
            }

            if (e.t == EntryType.COMMENT) {
                out.order.add(new Entry(EntryType.COMMENT, e.o));
                continue;
            }

            if (e.t == EntryType.VALUE) {
                Value fv = (Value) e.o;
                Value uv = user.values.get(fv.name);
                Value chosen = uv != null ? uv : deepCopyValue(fv);
                out.values.put(chosen.name, chosen);
                out.order.add(new Entry(EntryType.VALUE, chosen));
                continue;
            }

            if (e.t == EntryType.BRANCH) {
                Node freshChild = (Node) e.o;
                Node userChild = user.getBranch(freshChild.name);
                Node merged = userChild != null ?
                        mergeNodes(userChild, freshChild) :
                        deepCopyNode(freshChild);

                out.children.add(merged);
                out.order.add(new Entry(EntryType.BRANCH, merged));
            }
        }
        return out;
    }

    /**
     * <p>Merges missing values & branches from <b>defaults</b> into <b>user</b>
     * without changing or reordering anything already present.</p>
     *
     * <p>Best used when loading configs at runtime to ensure missing
     * fields are appended, but user formatting and values stay untouched.</p>
     *
     * <p>Behavior:</p>
     * <ul>
     *     <li>If user has value → kept</li>
     *     <li>If user missing value → add from defaults</li>
     *     <li>No overrides, no defaults formatting</li>
     * </ul>
     */
    public static void merge(@NotNull Node user, @NotNull Node defaults) {
        for (Map.Entry<String, Value> e : defaults.values.entrySet()) {
            if (!user.values.containsKey(e.getKey())) {
                Value v = deepCopyValue(e.getValue());
                user.values.put(e.getKey(), v);
                user.order.add(new Entry(EntryType.VALUE, v));
            }
        }

        for (Node defChild : defaults.children) {
            Node userChild = user.getBranch(defChild.name);

            if (userChild == null) {
                Node copy = deepCopyNode(defChild);
                user.children.add(copy);
                user.order.add(new Entry(EntryType.BRANCH, copy));
            } else {
                merge(userChild, defChild);
            }
        }
    }

    /**
     * Deep-copies a {@link Value}. Lists, branchLists and comments are cloned
     * so the result shares no references with the original.
     */
    public static Value deepCopyValue(@NotNull Value v) {
        Value c = new Value();
        c.name = v.name;
        c.type = v.type;
        c.iv = v.iv;
        c.dv = v.dv;
        c.sv = v.sv;

        if (v.list != null) {
            c.list = new ArrayList<>();
            for (Value x : v.list) c.list.add(deepCopyValue(x));
        }

        if (v.branchList != null) {
            c.branchList = new ArrayList<>();
            for (Node n : v.branchList) c.branchList.add(deepCopyNode(n));
        }

        for (Comment com : v.comments)
            c.comments.add(new Comment(com.type, com.text));

        return c;
    }

    /**
     * Deep-copies a {@link Node}. Values, children and order are rebuilt to
     * reference new copies, producing a fully detached clone.
     */
    public static Node deepCopyNode(@NotNull Node n) {
        Node c = new Node();
        c.name = n.name;

        for (Value v : n.values.values()) {
            Value copy = deepCopyValue(v);
            c.values.put(copy.name, copy);
        }

        for (Node child : n.children) c.children.add(deepCopyNode(child));

        for (Entry e : n.order) {
            if (e.t == EntryType.VALUE) c.order.add(new Entry(EntryType.VALUE, c.values.get(((Value) e.o).name)));
            else if (e.t == EntryType.BRANCH)
                c.order.add(new Entry(EntryType.BRANCH, c.children.stream().filter(x -> x.name.equals(((Node) e.o).name)).findFirst().orElse(null)));
            else c.order.add(new Entry(e.t, e.o));
        }

        c.inlineComments.addAll(n.inlineComments);
        return c;
    }
}

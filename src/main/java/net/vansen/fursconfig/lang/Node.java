package net.vansen.fursconfig.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Node (Branch) Class
 * <p>
 * Represents a node in the parsed configuration tree.
 *
 * <p>A node can be either the root node or a branch node. The root node is the top-most node in the tree,
 * while branch nodes are nested within other nodes.</p>
 *
 * <p>Each node can have a set of key-value pairs, where the key is a string and the value is a boolean, integer, or string.</p>
 *
 * <p>Branch nodes can also have child nodes, allowing for a hierarchical structure.</p>
 *
 * <dl>
 *   <dt>Root Node</dt>
 *   <dd>
 *     <pre>
 * is_enabled = true
 * what_is_3_and_3 = 6
 *     </pre>
 *   </dd>
 *   <dt>Branch Node</dt>
 *   <dd>
 *     <pre>
 * some_branch { // This is inside of the root node
 *     is_enabled = true
 *     what_is_3_and_3 = 6
 * }
 *     </pre>
 *   </dd>
 *   <dt>Nested Branch Node</dt>
 *   <dd>
 *     <pre>
 * some_branch_1 {
 *     cool_to_meet_you = true
 *     other_branch {
 *         some_other_branchs_value = "..."
 *     }
 * }
 *     </pre>
 *   </dd>
 * </dl>
 */
public class Node {

    /**
     * Children of this node, will be either a value or another node
     */
    public final Map<String, Object> children = new ConcurrentHashMap<>();

    /**
     * Gets the value at the given path
     * <p>
     * Path should be in the format of "branch_1.branch_2.key", where branch_1 is a child of the root node and branch_2 is a child of branch_1 and key is a value of branch_2
     *
     * @param path the path to the value
     * @return the value at the given path
     */
    public Object get(@NotNull String path) {
        try {
            String[] parts = path.split("\\.");
            Node node = this;
            for (int i = 0; i < parts.length - 1; i++) {
                node = (Node) node.children.get(parts[i]);
                if (node == null) return null;
            }
            return node.children.get(parts[parts.length - 1]);
        } catch (Exception e) {
            throw new RuntimeException("Path: " + path + " either doesn't exist or there's a parsing error", e);
        }
    }
}


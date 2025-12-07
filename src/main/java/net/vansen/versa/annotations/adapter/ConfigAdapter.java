package net.vansen.versa.annotations.adapter;

import net.vansen.versa.builder.NodeBuilder;
import net.vansen.versa.node.Node;

/**
 * Converts objects to and from Versa node structures.
 *
 * <pre>{@code
 * public static class WorldAdapter implements ConfigAdapter<World> {
 *     public World fromNode(Node n) {
 *         return new World(n.getString("name"), n.getInteger("size"));
 *     }
 *
 *     public void toNode(World w, NodeBuilder b) {
 *         b.add(new ValueBuilder().name("name").string(w.name));
 *         b.emptyLine();
 *         b.add(new ValueBuilder().name("size").intVal(w.size));
 *     }
 * }
 * }</pre>
 */
public interface ConfigAdapter<T> {
    T fromNode(Node node);

    void toNode(T value, NodeBuilder builder);
}
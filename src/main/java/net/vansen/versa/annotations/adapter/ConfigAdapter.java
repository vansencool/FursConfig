package net.vansen.versa.annotations.adapter;

import net.vansen.versa.builder.NodeBuilder;
import net.vansen.versa.node.Node;
import org.jetbrains.annotations.NotNull;

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

    /**
     * Creates an object instance from a parsed node.
     *
     * @param node source node containing values
     * @return constructed object instance
     */
    @NotNull T fromNode(@NotNull Node node);

    /**
     * Writes an object into builder form for saving/serialization.
     *
     * @param value object to serialize into config form
     * @param builder node builder to write into
     */
    void toNode(@NotNull T value, @NotNull NodeBuilder builder);
}
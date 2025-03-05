package net.vansen.fursconfig.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Node {
    public final Map<String, Object> children = new ConcurrentHashMap<>();

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


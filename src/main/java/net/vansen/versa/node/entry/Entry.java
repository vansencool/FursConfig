package net.vansen.versa.node.entry;

import net.vansen.versa.node.Node;

/**
 * Represents a single ordered element inside a {@link Node}.
 * Used during printing to preserve formatting layout exactly as written.
 */
public class Entry {

    /**
     * Type of element (value, branch, comment or blank line)
     */
    public EntryType t;

    /**
     * The object associated with this entry — varies depending on {@link #t}
     */
    public Object o;

    /**
     * @param t the type of entry
     * @param o value/branch/comment or empty-line placeholder
     */
    public Entry(EntryType t, Object o) {
        this.t = t;
        this.o = o;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "t=" + t +
                ", o=" + o +
                '}';
    }
}
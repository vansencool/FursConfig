package net.vansen.versa.node.entry;

/**
 * Entry describing one item in the printed order of this node:
 * a value, a child branch, a comment line or an empty line.
 */
public class Entry {
    public EntryType t;
    public Object o;

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
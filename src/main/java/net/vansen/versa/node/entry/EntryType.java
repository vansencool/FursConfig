package net.vansen.versa.node.entry;

import net.vansen.versa.node.Node;

/**
 * Represents individual ordered elements inside a {@link Node}.
 * This enum helps preserve formatting when printing back to text.
 */
public enum EntryType {

    /**
     * A key-value entry (example: {@code host = "localhost"})
     */
    VALUE,

    /**
     * A nested config branch (example: {@code database { ... }})
     */
    BRANCH,

    /**
     * A blank visual separator, printed as an empty line
     */
    EMPTY_LINE,

    /**
     * A standalone line comment (`// comment` or `# comment`)
     */
    COMMENT
}
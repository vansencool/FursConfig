package net.vansen.versa.comments;

/**
 * Defines the type of a comment inside a config node.
 * <p>
 * Used by {@link Comment} and printing logic to determine
 * where a comment is placed when rendering configuration text.
 * <ul>
 *     <li>{@code INLINE_VALUE} -> printed next to a value on the same line</li>
 *     <li>{@code START_BRANCH} -> printed after '{' when a branch opens</li>
 *     <li>{@code END_BRANCH}   -> printed after '}' when a branch closes</li>
 *     <li>{@code COMMENT_LINE} -> full standalone comment line</li>
 * </ul>
 */
public enum CommentType {

    /**
     * Inline comment printed after a value
     */
    INLINE_VALUE,

    /**
     * Inline comment placed after an opening {
     */
    START_BRANCH,

    /**
     * Inline comment placed after a closing }
     */
    END_BRANCH,

    /**
     * A standalone comment line
     */
    COMMENT_LINE
}
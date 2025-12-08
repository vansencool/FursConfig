package net.vansen.versa.node.value;

import net.vansen.versa.node.Value;

/**
 * Describes the stored type inside a {@link Value}.
 */
public enum ValueType {

    /** 32-bit integer*/
    INT,

    /** 64-bit integer */
    LONG,

    /** Floating number stored internally as double but returned as float */
    FLOAT,

    /** Full double precision numeric value */
    DOUBLE,

    /** Quoted or raw text value (example: "hello") */
    STRING,

    /** Boolean value, represented internally as 1/0 */
    BOOL,

    /** Regular list containing {@link Value} elements */
    LIST,

    /** List containing embedded config branches ({ ... }) */
    LIST_OF_BRANCHES
}
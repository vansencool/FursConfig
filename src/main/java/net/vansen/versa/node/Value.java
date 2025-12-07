package net.vansen.versa.node;

import net.vansen.versa.comments.Comment;
import net.vansen.versa.node.value.ValueType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single parsed configuration value.
 * Stores data in typed form (number, string, boolean, list, or branch list)
 * and provides simple access methods for retrieving it.
 */
@SuppressWarnings("unused")
public class Value {
    public String name;
    public ValueType type;
    public long iv;
    public double dv;
    public String sv;
    public List<Value> list;
    public List<Node> branchList;
    public List<Comment> comments = new ArrayList<>();
    public char assign = '=';

    /**
     * Returns this value as an int.
     */
    public int asInt() {
        return (int) iv;
    }

    /**
     * Returns this value as a long.
     */
    public long asLong() {
        return iv;
    }

    /**
     * Returns this value as a float.
     */
    public float asFloat() {
        return (float) dv;
    }

    /**
     * Returns this value as a double.
     */
    public double asDouble() {
        return dv;
    }

    /**
     * Returns this value as a boolean (non-zero = true).
     */
    public boolean asBool() {
        return iv != 0;
    }

    /**
     * Returns this value as a string.
     */
    public String asString() {
        return sv;
    }

    /**
     * Returns this value as a list of values.
     */
    public List<Value> asList() {
        return list;
    }

    /**
     * Returns this value as a list of branch nodes.
     */
    public List<Node> asBranchList() {
        return branchList;
    }

    /**
     * Returns the underlying Java representation of this Value.
     * <p>
     * Numbers become Long/Double, strings return String, lists return List<Value>,
     * and LIST_OF_BRANCHES returns List<Node>.
     * Useful when you want the actual stored value without checking type.
     */
    public Object raw() {
        return switch (type) {
            case STRING -> sv;
            case INT, LONG, BOOL -> iv;
            case DOUBLE -> dv;
            case LIST -> list;
            case LIST_OF_BRANCHES -> branchList;
            default -> null;
        };
    }

    /**
     * True if stored as an int.
     */
    public boolean isInt() {
        return type == ValueType.INT;
    }

    /**
     * True if stored as a long.
     */
    public boolean isLong() {
        return type == ValueType.LONG;
    }

    /**
     * True if stored as a float.
     */
    public boolean isFloat() {
        return type == ValueType.FLOAT;
    }

    /**
     * True if stored as a double.
     */
    public boolean isDouble() {
        return type == ValueType.DOUBLE;
    }

    /**
     * True if stored as a string.
     */
    public boolean isString() {
        return type == ValueType.STRING;
    }

    /**
     * True if stored as a boolean.
     */
    public boolean isBool() {
        return type == ValueType.BOOL;
    }

    /**
     * True if this represents a simple value list.
     */
    public boolean isList() {
        return type == ValueType.LIST;
    }

    /**
     * True if this represents a list of branch objects.
     */
    public boolean isListOfBranches() {
        return type == ValueType.LIST_OF_BRANCHES;
    }

    /**
     * Converts this value to its config-text representation.
     */
    @Override
    public String toString() {
        if (isString()) return "\"" + sv + "\"";
        if (isBool()) return iv == 1 ? "true" : "false";
        if (isInt() || isLong()) return Long.toString(iv);
        if (isFloat() || isDouble()) return Double.toString(dv);
        if (isList()) {
            StringBuilder b = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) b.append(", ");
                b.append(list.get(i));
            }
            b.append("]");
            return b.toString();
        }
        if (isListOfBranches()) {
            StringBuilder b = new StringBuilder("[\n");
            for (int i = 0; i < branchList.size(); i++) {
                b.append("    {\n").append(branchList.get(i).toString(2)).append("    }");
                if (i < branchList.size() - 1) b.append(",\n");
            }
            b.append("\n]");
            return b.toString();
        }
        return "";
    }
}
package net.vansen.fursconfig.lang;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for FursConfig
 */
public class Parser {
    private final List<String> tokens;
    private int pos = 0;

    /**
     * Creates a new Parser
     *
     * @param lexer the lexer to use
     */
    public Parser(@NotNull Lexer lexer) {
        this.tokens = lexer.tokenize();
    }

    /**
     * Parses the given tokens into the given parent node
     *
     * @param parent the parent node to parse into
     */
    public void parse(@NotNull Node parent) {
        while (pos < tokens.size()) {
            String key = tokens.get(pos++);
            if (pos < tokens.size() && tokens.get(pos).equals("=")) {
                pos++;
                parent.children.put(key, parseValue());
            } else if (pos < tokens.size() && tokens.get(pos).equals("{")) {
                pos++;
                Node child = new Node();
                parent.children.put(key, child);
                parse(child);
            } else if (key.equals("}")) {
                return;
            }
        }
    }

    Object parseValue() {
        String token = tokens.get(pos++);
        if (token.equals("[")) {
            List<Object> list = new ArrayList<>();
            while (!tokens.get(pos).equals("]")) {
                if (tokens.get(pos).equals("{")) {
                    pos++;
                    Node obj = new Node();
                    parse(obj);
                    list.add(obj);
                } else {
                    list.add(tokenizeValue(tokens.get(pos++)));
                }
                if (tokens.get(pos).equals(",")) pos++;
            }
            pos++;
            return list;
        }
        return tokenizeValue(token);
    }

    Object tokenizeValue(@NotNull String token) {
        if (token.matches("\".*\"")) return token.substring(1, token.length() - 1);
        if (token.matches("true|false")) return Boolean.parseBoolean(token);
        if (token.contains(".")) return Double.parseDouble(token);
        if (token.matches("-?\\d+")) {
            try {
                return Integer.parseInt(token);
            } catch (NumberFormatException e) {
                return Long.parseLong(token);
            }
        }
        return token;
    }
}

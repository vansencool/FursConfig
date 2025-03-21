package net.vansen.fursconfig.lang;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lexer for FursConfig
 */
public class Lexer {

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "-?\\d+\\.\\d+|-?\\d+|\\w+|\"[^\"]*\"|=|\\{|}|\\[|]|,|\\s+");
    private final String input;

    /**
     * Initializes the lexer
     *
     * @param input the input string to tokenize
     */
    public Lexer(@NotNull String input) {
        this.input = removeComments(input);
    }

    /**
     * Tokenizes the input string into a list of tokens
     *
     * @return the list of tokens
     */
    public List<String> tokenize() {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(input);
        while (matcher.find()) {
            String token = matcher.group();
            if (!token.trim().isEmpty()) tokens.add(token);
        }
        return tokens;
    }

    @SuppressWarnings("StringConcatenationInLoop")
    private static String removeComments(@NotNull String input) {
        String result = "";
        String[] lines = input.split("\n");

        for (String line : lines) {
            int commentIndex = line.indexOf("//");
            result += (commentIndex != -1) ? line.substring(0, commentIndex) : line;
            result += "\n";
        }

        return result;
    }
}

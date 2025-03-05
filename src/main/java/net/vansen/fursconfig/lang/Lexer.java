package net.vansen.fursconfig.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "-?\\d+\\.\\d+|-?\\d+|\\w+|\"[^\"]*\"|=|\\{|}|\\[|]|,|\\s+");
    private final String input;

    public Lexer(String input) {
        this.input = input;
    }

    List<String> tokenize() {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(input);
        while (matcher.find()) {
            String token = matcher.group();
            if (!token.trim().isEmpty()) tokens.add(token);
        }
        return tokens;
    }
}

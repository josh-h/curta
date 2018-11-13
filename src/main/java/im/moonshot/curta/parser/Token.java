package im.moonshot.curta.parser;

import java.util.List;
import java.util.regex.Pattern;

/**
 *  A token is a value that represents a basic parse/lex unit.
 */
class Token {
    /**
     * Enumerates all kinds of tokens.
     */
    enum Kind {
        NUMBER("-?\\p{Digit}+"),
        LPAREN("\\("),
        ADD("add"),
        SUB("sub"),
        MULT("mult"),
        DIV("div"),
        LET ("let"),
        VARIABLE("[A-Za-z]+"),
        RPAREN("\\)"),
        COMMA(","),
        END("$");

        private final Pattern pattern;

        static List<Kind> functions() {
            return List.of(ADD, SUB, MULT, DIV, LET);
        }

        Kind(String aPattern) {
            // Prefix each pattern with the start of string, to avoid greedily consuming over tokens
            pattern = Pattern.compile("^" + aPattern);
        }

        Pattern pattern() {
            return pattern;
        }
    }


    private final String value;
    private final Kind kind;

    Token(Kind kind, String value) {
        this.kind = kind;
        this.value = value;
    }

    String value() {
        return value;
    }
    
    Kind kind() {
    	return kind;
    }

    @Override
    public String toString() {
        return "Token{" +
                "value='" + value + '\'' +
                ", kind=" + kind +
                '}';
    }
}

package im.moonshot.curta.parser;

import im.moonshot.curta.parser.Token.Kind;

import java.nio.CharBuffer;

/**
 * A lexical analyzer that maps an input stream of {@code CharSequence} to {@code Token}s.
 * <p>
 * Note that the analyzer discards any whitespace encountered.
 */
/*
    Implementation note:
    The intent for using CharSequence over String is to potentially support streaming from an
    InputStream instead of loading the entire source file into memory at once.
 */
class Lexer {
    private final CharSequence stream;
    private final CharBuffer buffer; // FIXME:
    private int position;
    private Token current;

    Lexer(CharSequence source) {
        buffer = CharBuffer.wrap(source); // FIXME: refactor to use this
        stream = source;
        position = 0;
    }

    /**
     * Return the position in the underlying {@code CharSequence}.
     *
     * @return the position of the current token
     */
    int position() {
        return position;
    }

    /**
     * Returns the token that the lexer has analyzed.
     *
     * @throws IllegalStateException if the Lexer has not begun analysis by a call to {@code nextToken}
     *
     * @return the current token.
     */
    Token current() {
        if (current == null) throw new IllegalStateException("Lexer has not begun");

        return current;
    }

    /**
     * Performs lexical analysis of the underlying {@code CharacterSequence} and returns the next token in the sequence.
     * <p>
     * If no matching tokens are found and tokens in the sequence remain then {@code null} is returned.
     *
     * @return the next {@code Token}.
     */
    Token nextToken() {
        skipWhitespace();

        // TODO: Consider refactoring to use an interator over the CharSequence
        var subsequence = seekToPosition();

        return findMatchingToken(subsequence);
    }

    private void skipWhitespace() {
        while(position < stream.length() && Character.isWhitespace(stream.charAt(position))) position++;
    }

    private CharSequence seekToPosition() {
        return stream.subSequence(position, stream.length());
    }

    private Token findMatchingToken(CharSequence sequence) {
        for (var tokenKind : Kind.values()) {
            var match = tokenKind.pattern().matcher(sequence);
            if (match.find()) {
                position += match.end();
                current = new Token(tokenKind, match.group());
                return current;
            }
        }
        return null;
    }
}

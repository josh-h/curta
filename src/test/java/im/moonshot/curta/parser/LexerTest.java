package im.moonshot.curta.parser;

import im.moonshot.curta.parser.Token.Kind;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LexerTest {
    @Test
    void shouldMatchTokens() {
        Lexer lexer = new Lexer("add(123, 456)");

        assertEquals(Kind.ADD, lexer.nextToken().kind());
        assertEquals(Kind.LPAREN, lexer.nextToken().kind());
        assertEquals(Kind.NUMBER, lexer.nextToken().kind());
        assertEquals(Kind.COMMA, lexer.nextToken().kind());
        assertEquals(Kind.NUMBER, lexer.nextToken().kind());
        assertEquals(Kind.RPAREN, lexer.nextToken().kind());
        assertEquals(Kind.END, lexer.nextToken().kind());
    }

    @Test
    void shouldReturnNullOnNoMatches() {
        var lexer = new Lexer("*");
        assertNull(lexer.nextToken());
    }

    @Test
    void shouldTrackPosition() {
        var lexer = new Lexer("123abc456");
        lexer.nextToken();
        assertEquals(3, lexer.position());
        lexer.nextToken();
        assertEquals(6, lexer.position());
        lexer.nextToken();
        assertEquals(9, lexer.position());
    }
    
    @Test
    void shouldTokenizeValue() {
    	var token = new Lexer("123").nextToken();
    	assertEquals("123", token.value());
    }

    @Test
    void shouldIgnoreWhitespace() {
        var token = new Lexer(" 123").nextToken();
        assertEquals("123", token.value());
    }

    @Test
    void shouldHandleNegativeIntegers() {
        var token = new Lexer("-123").nextToken();
        assertEquals("-123", token.value());
    }
}

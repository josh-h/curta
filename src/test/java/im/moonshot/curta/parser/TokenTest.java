package im.moonshot.curta.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TokenTest {
    @Test
    void matchesAtStart() {
        var m = Token.Kind.NUMBER.pattern().matcher("a123");
        assertFalse(m.find(), "Must match from start of string");
    }

    @Test
    void matchesToken() {
        var m = Token.Kind.COMMA.pattern().matcher(",");
        assertTrue(m.find());
    }
    
    @Test
    void tokenReturnsValue() {
    	var token = new Token(Token.Kind.NUMBER, "1");
    	assertEquals("1", token.value());
    	assertEquals(Token.Kind.NUMBER, token.kind());
    }

    @Test
    void shouldHandlePositiveAndNegativeIntegers() {
        var intPattern = Token.Kind.NUMBER.pattern();
        assertTrue(intPattern.matcher("100").find());
        assertTrue(intPattern.matcher("-1").find());

        assertTrue(intPattern.matcher("" + Integer.MAX_VALUE).find());
        assertTrue(intPattern.matcher("" + Integer.MIN_VALUE).find());
    }
}

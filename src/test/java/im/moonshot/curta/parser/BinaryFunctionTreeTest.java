package im.moonshot.curta.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BinaryFunctionTreeTest {

    @Test
    void returnsParamsAndValue() {
        var left = new NumberTree(new Token(Token.Kind.NUMBER, "1"));
        var right = new NumberTree(new Token(Token.Kind.NUMBER, "2"));
        var binOp = new BinaryFunctionTree(new Token(Token.Kind.ADD, "unused"), left, right);
        assertEquals(left, binOp.getFirst());
        assertEquals(right, binOp.getSecond());
        assertEquals(Token.Kind.ADD, binOp.getOperation().kind());
    }
}
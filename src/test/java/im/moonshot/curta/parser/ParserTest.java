package im.moonshot.curta.parser;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    @Test
    void shouldParseBinaryOperations() throws ParseException {
        var binOp = (BinaryFunctionTree) new Parser("add(1,2)").parse();

        var number = (NumberTree) binOp.getFirst();
        assertEquals(1, number.getValue());
    }

    @Test
    void shouldParseLetOperator() throws ParseException {
        var let = (LetFunctionTree) new Parser("let(a,1,add(a,2))").parse();
        assertEquals("a", ((VariableTree)let.getFirst()).getVariable());
        assertEquals(let.getOperation().kind(), Token.Kind.LET);
        assertTrue(let.getParam(1) instanceof VariableTree);
        assertTrue(let.getParam(2) instanceof NumberTree);
        assertTrue(let.getParam(3) instanceof FunctionTree);
    }

    @Test
    void shouldParseSemanticErrors() throws ParseException {
        var let = (LetFunctionTree) new Parser("let(a,1,add(b,2))").parse();

        assertEquals("a", ((VariableTree)let.getFirst()).getVariable());
        var variable = ((BinaryFunctionTree)let.getParam(3)).getFirst();
        assertEquals("b", ((VariableTree) variable).getVariable());
    }

    @Test
    void shouldIncludeExpectedTokensOnError() {
        var exception = assertThrows(ParseException.class, () -> {
            var parser = new Parser("let(1,add(1,2))");
            parser.parse();
        });
        assertEquals("Expected VARIABLE, but found '1'.", exception.getMessage());
        assertEquals(5, exception.getErrorOffset());
    }

    @Test
    void shouldFailOnEmptyExpression() {
        var exception = assertThrows(ParseException.class, () ->
            new Parser("").parse()
        );
        assertEquals("No tokens found.", exception.getMessage());
        assertEquals(0, exception.getErrorOffset());
    }

    @Test
    void shouldFailOnValidTokenButInvalidSyntax() {
        var exception = assertThrows(ParseException.class, () -> {
            var parser = new Parser("123");
            parser.parse();
        });
        assertEquals("Expected one of (ADD SUB MULT DIV LET), but found '123'.", exception.getMessage());
        assertEquals(1, exception.getErrorOffset());
    }

    @Test
    void shouldFailOnInvalidSyntax() {
        var exception = assertThrows(ParseException.class, () -> {
            var parser = new Parser("add(123)");
            parser.parse();
        });
        assertEquals("Expected COMMA, but found ')'.", exception.getMessage());
        assertEquals(8, exception.getErrorOffset());
    }

    @Test
    void shouldFailOnInvalidLetOperation() {
        var exception = assertThrows(ParseException.class, () ->
            new Parser("let(add(1,2),1,add(1,2))").parse()
        );
        assertEquals("Expected VARIABLE, but found 'add'.", exception.getMessage());
        assertEquals(5, exception.getErrorOffset());
    }

    @Test
    void shouldFailOnMissingRParen() {
        var exception = assertThrows(ParseException.class, () ->
            new Parser("let(a, 1, add(b, 1").parse()
        );
        assertEquals("Expected RPAREN, but found ''.", exception.getMessage());
        assertEquals(19, exception.getErrorOffset());
    }

    @Test
    void shouldHandleOnlyOneRootFunction() {
        var exception = assertThrows(ParseException.class, () ->
            new Parser("add(1,2)Unexpected").parse()
        );
    }
}

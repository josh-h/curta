package im.moonshot.curta.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.ParseException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatingInterpreterTest {

    private Object interpret(String text) throws ParseException {
        return new CalculatingInterpreter(new Parser(text)).interpret();
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    void shouldEvaluate(Tuple t) throws ParseException {
        assertEquals(t.result, interpret(t.expression));
    }

    static class Tuple {
        Tuple(String e, int r) { expression = e; result = r; }
        String expression;
        int result;
    }
    private static Stream<Tuple> dataProvider() {
        return Stream.of(
            new Tuple("add(sub(5,4),2)", 3),
            new Tuple("add(sub(-5,4),2)", -7),
            new Tuple("mult(5,add(1,2))", 15),
            new Tuple("mult(add(2, 2), div(9, 3))", 12),
            new Tuple("let(a, 5, add(a, a))", 10),
            new Tuple("let(a,5,let(b,mult(a,10),add(b,a)))", 55),
            new Tuple("let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b)))", 40)
        );
    }

    @Test
    void shouldHandleIntegerOverflow() {
        assertThrows(ArithmeticException.class, () ->
            interpret("add(2147483647, 1)")
        );
    }

    @Test
    void shouldHandleUndefinedVariable() {
        var exception = assertThrows(SemanticException.class, () ->
            interpret("let(a, 1, add(b, 1))")
        );
        assertEquals("Unknown variable 'b'.", exception.getMessage());
    }

    @Test
    void shouldFailOnInvalidVariableAssignment() {
        var exception = assertThrows(SemanticException.class, () ->
            interpret("let(a, add(a, 1), add(1,1))")
        );
        assertEquals("Unknown variable 'a'.", exception.getMessage());
    }
}

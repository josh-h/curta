package im.moonshot.curta.parser;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphvizInterpreterTest {
    @Test
    void shouldGenerateGraph() throws ParseException {
        var parser = new Parser("let(a,3,mult(a,add(add(1,2),sub(1,0))))");
        var interpretor = new GraphvizInterpreter(parser);
        assertEquals(
                "digraph BST {\n" +
                        "\tLET0 [ label = \"LET\" ];\n" +
                        "\tvar1 [ label = \"a\" ];\n" +
                        "\tint2 [ label = \"3\" ];\n" +
                        "\tMULT3 [ label = \"MULT\" ];\n" +
                        "\tvar4 [ label = \"a\" ];\n" +
                        "\tADD5 [ label = \"ADD\" ];\n" +
                        "\tADD6 [ label = \"ADD\" ];\n" +
                        "\tint7 [ label = \"1\" ];\n" +
                        "\tint8 [ label = \"2\" ];\n" +
                        "\tSUB9 [ label = \"SUB\" ];\n" +
                        "\tint10 [ label = \"1\" ];\n" +
                        "\tint11 [ label = \"0\" ];\n" +
                        "\tADD6 -> { int7 int8 };\n" +
                        "\tSUB9 -> { int10 int11 };\n" +
                        "\tADD5 -> { ADD6 SUB9 };\n" +
                        "\tMULT3 -> { var4 ADD5 };\n" +
                        "\tLET0 -> { var1 int2 MULT3 };\n" +
                        "}\n",
                interpretor.interpret()
                );
    }
}
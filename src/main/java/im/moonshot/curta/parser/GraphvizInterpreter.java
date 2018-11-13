package im.moonshot.curta.parser;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * An interpreter that outputs a graphviz visualization of the abstract syntax tree.
 */
public class GraphvizInterpreter implements TreeVisitor<String> {
    private static final Logger logger = Logger.getLogger(GraphvizInterpreter.class.getName());

    private final Parser parser;
    private final StringBuilder graph = new StringBuilder();
    private final List<String> labels = new LinkedList<>();
    private int id = 0;

    public GraphvizInterpreter(Parser parser) {
        this.parser = parser;
    }

    public String interpret() throws ParseException {
        var tree = parser.parse();
        assert tree != null;

        logger.fine("Starting graphviz interpretation");
        tree.accept(this);

        var result = "digraph BST {" +
            System.lineSeparator() +
            String.join(System.lineSeparator(), labels) +
            System.lineSeparator() +
            graph +
            "}" +
            System.lineSeparator();
        logger.fine("Finished graphviz interpretation");
        return result;
    }

    @Override
    public String visit(BinaryFunctionTree binaryFunction) {
        return visit((FunctionTree) binaryFunction);
    }

    @Override
    public String visit(LetFunctionTree operation) {
        return visit((FunctionTree)operation);
    }

    private String visit(FunctionTree operation) {
        var label = generateLabel(operation.getOperation().kind().toString());

        List<String> entries = operation.getParameters()
                                        .stream()
                                        .map(param -> param.accept(this))
                                        .collect(Collectors.toList());

        var labels = new String[entries.size()];
        graph.append(generateLeafNodes(label, entries.toArray(labels)));

        return label;
    }

    @Override
    public String visit(NumberTree number) {
        return generateLabel("int", "" + number.getValue());
    }

    @Override
    public String visit(VariableTree variable) {
        return generateLabel("var", variable.getVariable());
    }

    private String generateLabel(String label) { return generateLabel(label, null); }

    private String generateLabel(String label, String value) {
        var nodeName = "" + label + id++;
        labels.add("\t" +
            nodeName +
            " [ label = \"" +
            (value != null ? value : label) +
            "\" ];");

        return nodeName;
    }

    private String generateLeafNodes(String label, String ... leaves) {
        return "\t" +
               label +
               " -> { " +
               String.join(" ", leaves) +
               " };" +
               System.lineSeparator();
    }
}


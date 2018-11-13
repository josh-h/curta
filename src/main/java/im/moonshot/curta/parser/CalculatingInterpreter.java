package im.moonshot.curta.parser;

import java.text.ParseException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.IntSupplier;
import java.util.logging.Logger;

/**
 * Interprets the abstract syntax table and executes the calculations.
 * <p>
 * Any calculations that result in integer over/underflow raise {@code ArithmeticException}.
 */
public class CalculatingInterpreter implements TreeVisitor<Integer> {
    private final static Logger logger = Logger.getLogger(CalculatingInterpreter.class.getName());

    private final Parser parser;
    private final SymbolTable symbol;

    public CalculatingInterpreter(Parser parser) {
        this.parser = parser;
        symbol = new SymbolTable();
    }

    /**
     * Executes the syntax tree.
     *
     * @return the result
     * @throws ParseException if the parser encounters a syntax error
     * @throws ArithmeticException if integer overflow or underflow is detected
     */
    public Integer interpret() throws ParseException {
        var tree = parser.parse();
        assert tree != null;

        logger.fine("Starting interpretation");
        var result = tree.accept(this);
        logger.fine("Finished interpretation");

        return result;
    }

    /*
        Note: If this method gets any more complex consider creating new subtypes
        for each of the arithmetic functions.
     */
    @Override
    public Integer visit(BinaryFunctionTree binaryFunction) {
        logger.finer("ENTRY");

        int lparam = binaryFunction.getFirst().accept(this);
        int rparam = binaryFunction.getSecond().accept(this);
        int result;
        switch (binaryFunction.getOperation().kind()) {
            case ADD: result = Math.addExact(lparam, rparam); break;
            case SUB: result = Math.subtractExact(lparam, rparam); break;
            case DIV: result = lparam / rparam; break; // No over/under flow from division
            case MULT: result = Math.multiplyExact(lparam, rparam); break;
            default:
                // Our parser should keep us from encountering this.
                assert false;
                throw new SemanticException("Semantic error");
        }

        logger.exiting(getClass().getName(), "visit(BinaryFunctionTree)", result);
        return result;
    }

    @Override
    public Integer visit(LetFunctionTree letFunction) {
        logger.finer("ENTRY");

        VariableTree variable = (VariableTree) letFunction.getParam(1);
        int value = letFunction.getParam(2).accept(this);

        int result;
        result = symbol.scope(variable.getVariable(), value, () ->
            letFunction.getParam(3).accept(this)
        );

        logger.exiting(getClass().getName(), "visit(LetFunctionTree", result);
        return result;
    }

    @Override
    public Integer visit(NumberTree number) {
        return number.getValue();
    }

    @Override
    public Integer visit(VariableTree variable) {
        return symbol.get(variable.getVariable());
    }

    /**
     * A simple scoped symbol table.
     */
    private class SymbolTable {
        private class Symbol {
            Symbol(String variable, int value) {
                this.variable = variable;
                this.value = value;
            }
            String variable;
            int value;
        }

        private final Deque<Symbol> stack;

        SymbolTable() {
            stack = new LinkedList<>();
        }

        int get(String variable) {
            logger.finest(() -> "Checking symbol table for: " + variable);

            return stack.stream()
                    .filter(sym -> sym.variable.equals(variable))
                    .findFirst()
                    .orElseThrow(() -> new SemanticException("Unknown variable '" + variable + "'."))
                    .value;

        }

        /**
         * Adds the variable and its value to the symbol table's current scope. The scope is applied for the duration
         * of the lambda invocation.
         *
         * @param variable the variable to scope
         * @param value the value to assign to the variable
         * @param fn the lambda where the symbol's scope will exist
         * @return the result of fn
         */
        Integer scope(String variable, int value, IntSupplier fn) {
            stack.addFirst(new Symbol(variable, value));

            int result = fn.getAsInt();

            stack.removeFirst();
            return result;
        }
    }
}

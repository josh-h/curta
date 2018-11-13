package im.moonshot.curta.parser;

import im.moonshot.curta.parser.Token.Kind;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A recursive descent parser.
 *
 * See the package description for details on the supported grammar.
 */
public class Parser {
    private static final Logger logger = Logger.getLogger(Parser.class.getName());

    private final Lexer lexer;

    /**
     * Create a Parser from the stream.
     *
     * @param sequence the stream to parse
     */
    public Parser(CharSequence sequence) {
        lexer = new Lexer(sequence);
    }

    /**
     * Creates an abstract syntax tree by performing syntax parsing.
     *
     * @return an tree representation of the parsed input
     * @throws ParseException if the parser encountered a syntax error
     */
    public Tree parse() throws ParseException {
        logger.fine("Starting parser");

        lexer.nextToken(); // Start the lexer
        Tree tree = function();

        logger.fine("Finished parsing");
        return tree;
    }

    /* ["-"] #'[0-9]+' */
    private Tree number() throws ParseException {
        var integer = current();
        eat(Kind.NUMBER);
        return new NumberTree(integer);
    }

    /* #'[A-Za-z]+' */
    private Tree variable() throws ParseException {
        var variable = new VariableTree(current());
        eat(Kind.VARIABLE);
        return variable;
    }

    /*
        arithmetic_function | let_function
     */
    private Tree function() throws ParseException {
        logger.finer("ENTRY");

        Tree tree;
        if (isArithmeticFunction()) {
            tree = arithmeticFunction();
        } else if (current().kind() == Kind.LET) {
            tree = letFunction();
        } else {
            return error(Kind.functions());
        }

        logger.exiting(getClass().getName(), "function", tree);
        return tree;
    }

    /*
        ("add" | "sub" | "mult" | "div") "(" expression "," expression ")"
     */
    private Tree arithmeticFunction() throws ParseException {
        logger.finer("ENTRY");
        //
        // Since this is a private method we can guarantee that current()
        // is arithmetic. So, assert rather than force a runtime check.
        //
        assert isArithmeticFunction();

        Tree tree;
        Token function = current();

        eat(function);
        eat(Kind.LPAREN);
        var param1 = expression();
        eat(Kind.COMMA);
        var param2 = expression();
        eat(Kind.RPAREN);
        tree = new BinaryFunctionTree(function, param1, param2);

        logger.exiting(getClass().getName(), "arithmeticFunction", tree);
        return tree;

    }

    /*
        number | variable | function
     */
    private Tree expression() throws ParseException {
        logger.finer("ENTRY");

        Tree expression;
        if (current().kind() == Kind.NUMBER) {
            expression = number();
        } else if (current().kind() == Kind.VARIABLE) {
            expression = variable();
        } else if (isFunction()) {
            expression = function();
        } else {
            assert false;

            List<Kind> expected = List.copyOf(Kind.functions());
            expected.add(Kind.NUMBER);
            expected.add(Kind.VARIABLE);
            return error(expected);
        }

        logger.exiting(getClass().getName(), "expression", expression);
        return expression;
    }

    /*
        "let" "(" variable "," value_expression "," function ")"
     */
    private Tree letFunction() throws ParseException {
        logger.finer("ENTRY");

        assert current().kind() == Kind.LET;

        var operation = current();
        eat(Kind.LET);
        eat(Kind.LPAREN);
        var variable = variable();
        eat(Kind.COMMA);
        var expression = valueExpression();
        eat(Kind.COMMA);
        var function = function();
        eat(Kind.RPAREN);
        Tree tree = new LetFunctionTree(operation, variable, expression, function);

        logger.exiting(getClass().getName(), "letFunction", tree);
        return tree;
    }

    /*
        number | function
     */
    private Tree valueExpression() throws ParseException {
        logger.finer("ENTRY");

        Tree expression;
        if (current().kind() == Kind.NUMBER) {
            var integer = new NumberTree(current());
            eat(current());
            expression = integer;
        } else if (isFunction()) {
            expression = function();
        } else {
            var expected = new LinkedList<>(Kind.functions());
            expected.add(Kind.NUMBER);
            return error(expected);
        }

        logger.exiting(getClass().getName(), "valueExpression", expression);
        return expression;
    }

    private boolean isFunction() {
        return Kind.functions().contains(current().kind());
    }

    private boolean isArithmeticFunction() {
        return isFunction() && !(current().kind() == Kind.LET);
    }

    private void eat(Token expected) throws ParseException {
        eat(expected.kind());
    }

    private void eat(Token.Kind expected) throws ParseException {
        if (current().kind() == expected) {
            logger.finest("Eating: " + current().value());
            lexer.nextToken();
        } else {
            error(List.of(expected));
        }
    }

    private Token current() {
        return lexer.current();
    }

    /*
        TODO: Ideally we shouldn't stop on the first syntax error.
        Instead we could record the error in a list of errors, and skip ahead to the next recognized token.
        Then add public methods to check for any errors.
    */
    private Tree error(List<Kind> expectedTokens) throws ParseException {
        StringBuilder msg = new StringBuilder();
        int position;
        if (current().kind() == Kind.END && lexer.position() == 0) {
            msg.append("No tokens found.");
            position = lexer.position();
        } else {
            if(expectedTokens.size() > 1) {
                Iterable<String> names = expectedTokens.stream().map(Kind::name)::iterator;
                msg.append("Expected one of (")
                   .append(String.join(" ", names))
                   .append(")");
            } else {
                msg.append("Expected ")
                        .append(expectedTokens.get(0).name());
            }

            msg.append(", but found '")
                    .append(current().value())
                    .append("'.");
            var tokenLength = current().value();
            position = lexer.position() - tokenLength.length() + 1; // +1 because people don't work from 0 based indexes
        }

        throw new ParseException(msg.toString(), position);
    }
}

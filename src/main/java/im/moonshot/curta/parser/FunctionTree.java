package im.moonshot.curta.parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Abstract class for nodes representing a function in an abstract syntax tree.
 */
public abstract class FunctionTree implements Tree {
    private Token operation;
    private List<Tree> parameters;

    FunctionTree(Token token, Tree...parameters) {
        this.parameters = Arrays.asList(parameters);
        this.operation = token;
    }

    /**
     * Returns the function parameter at the specified position. Unlike Java arrays that are zero-based,
     * parameters are indexed starting at position 1. For example, {@code getParam(1)} returns the first function
     * parameter.
     *
     * @param index of the parameter
     * @throws IndexOutOfBoundsException if the parameter index doesn't exist
     * @return the specified parameter
     */
    Tree getParam(int index) {
        return parameters.get(index-1);
    }

    /* Convenience methods for getParam */
    Tree getFirst() { return parameters.get(0); }
    Tree getSecond() { return parameters.get(1); }

    /**
     * Return an unmodifiable view of the parameters.
     *
     * @return the parameters
     */
    List<Tree> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    /**
     * Returns the represented function.
     *
     * @return returns the function
     */
    Token getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "operation=" + operation +
                ", parameters=" + parameters +
                '}';
    }
}

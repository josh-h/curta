package im.moonshot.curta.parser;

/**
 * Indicates a semantic error occurred while visiting the abstract syntax tree.
 */
public class SemanticException extends RuntimeException {
    public SemanticException(String message) {
        super(message);
    }
}

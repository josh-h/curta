package im.moonshot.curta.parser;

/**
 * Common interface for all nodes in an abstract syntax tree.
 */
interface Tree {
    /**
     * Accept method used to implement the visitor pattern. Implementors use the methods
     * to provide semantic analysis and interpret the abstract syntax tax.
     *
     * @param <R> result type of this operation.
     * @param visitor the visitor to call
     * @return the result returned from calling the visitor
    */
    <R> R accept(TreeVisitor<R> visitor);
}

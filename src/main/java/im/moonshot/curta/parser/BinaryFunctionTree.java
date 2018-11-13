package im.moonshot.curta.parser;

/**
 * A tree node for a function that takes 2 parameters.
 */
class BinaryFunctionTree extends FunctionTree {
    BinaryFunctionTree(Token arithmeticOperation, Tree param1, Tree param2) {
        super(arithmeticOperation, param1, param2);
    }

    @Override
    public <T> T accept(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

package im.moonshot.curta.parser;

/**
 * A tree node for the let function taking 3 parameters.
 */
class LetFunctionTree extends FunctionTree {

    LetFunctionTree(Token let, Tree variable, Tree valueExpression, Tree function) {
        super(let, variable, valueExpression, function);
    }

    @Override
    public <T> T accept(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

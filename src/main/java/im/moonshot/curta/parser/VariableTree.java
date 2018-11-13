package im.moonshot.curta.parser;

/**
 * A tree node for a variable.
 */
class VariableTree implements Tree {
    private final String variable;

    VariableTree(Token variable) {
        this.variable = variable.value();
    }

    String getVariable() {
        return variable;
    }

    @Override
    public <R> R accept(TreeVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "VariableTree{" + variable + '}';
    }
}

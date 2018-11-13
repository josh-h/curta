package im.moonshot.curta.parser;

/**
 * A tree node for positive and negative integers.
 */
class NumberTree implements Tree {
    private final int literal;

    NumberTree(Token literal) {
        this.literal = Integer.valueOf(literal.value());
    }

    int getValue() {
        return literal;
    }

    @Override
    public <T> T accept(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "NumberTree{" + literal + '}';
    }
}

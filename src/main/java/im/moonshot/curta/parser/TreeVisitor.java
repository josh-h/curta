package im.moonshot.curta.parser;

/**
 * A visitor of {@code Tree}s.
 * <p>
 * When a visitor is passed to a tree's {@link Tree#accept accept} method,
 * the <code>visit<i>TreeType</i></code> method most applicable to the tree is invoked.
 * <p>
 * See the visitor design pattern for context.
 *
 * @param <R> the return type of this visitor's methods. Note using {@link
 *            Void} is valid for visitors that do not need to return results.
 */
interface TreeVisitor<R> {
    R visit(BinaryFunctionTree binaryFunction);
    R visit(NumberTree number);
    R visit(LetFunctionTree letFunction);
    R visit(VariableTree variable);
}

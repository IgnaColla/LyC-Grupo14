package lyc.compiler.icg_tree;

public class WriteExprNode extends TNode {

    private final TNode expression;

    public WriteExprNode(TNode expression) {
        this.expression = expression;
    }

    public TNode getExpression() {
        return expression;
    }

    @Override
    public String toCode() {
        return "(WRITE " + expression.toCode() + ")";
    }
}

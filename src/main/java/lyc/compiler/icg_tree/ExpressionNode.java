package lyc.compiler.icg_tree;

public class ExpressionNode extends TNode {

    private String operator;
    private TNode left;
    private TNode right;

    public ExpressionNode(String operator, TNode left, TNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public String getOperator() {
        return operator;
    }

    public TNode getLeft() {
        return left;
    }

    public TNode getRight() {
        return right;
    }

    @Override
    public String toCode() {
        return "(" + operator + " " + left.toCode() + " " + right.toCode() + ")";
    }

    @Override
    protected void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + "OP: " + operator);
        left.printTree(prefix + (isTail ? "    " : "│   "), false);
        right.printTree(prefix + (isTail ? "    " : "│   "), true);
    }
}

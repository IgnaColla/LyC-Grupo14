package lyc.compiler.icg_tree;

public class ConditionNode extends TNode {
    
    private String operator;
    private TNode left;
    private TNode right;

    public ConditionNode(String operator, TNode left, TNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public ConditionNode(String operator, TNode operand) {
        this.operator = operator;
        this.left = operand;
        this.right = null;
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
        if (right == null) {
            // Unary operator
            return "(" + operator + " " + left.toCode() + ")";
        }
        // Binary operator
        return "(" + operator + " " + left.toCode() + " " + right.toCode() + ")";
    }

    @Override
    protected void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + "CONDITION: " + operator);
        left.printTree(prefix + (isTail ? "    " : "│   "), right == null);
        if (right != null) {
            right.printTree(prefix + (isTail ? "    " : "│   "), true);
        }
    }
}

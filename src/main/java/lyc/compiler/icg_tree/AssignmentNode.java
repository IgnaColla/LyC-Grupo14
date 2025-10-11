package lyc.compiler.icg_tree;

public class AssignmentNode extends TNode {
    
    private String variable;
    private TNode expression;

    public AssignmentNode(String variable, TNode expression) {
        this.variable = variable;
        this.expression = expression;
    }

    public String getVariable() {
        return variable;
    }

    public TNode getExpression() {
        return expression;
    }

    @Override
    public String toCode() {
        return "(= " + variable + " " + expression.toCode() + ")";
    }

    @Override
    protected void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + "ASSIGN: " + variable);
        expression.printTree(prefix + (isTail ? "    " : "│   "), true);
    }
}

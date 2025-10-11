package lyc.compiler.icg_tree;

import java.util.List;
import java.util.ArrayList;

public class ProgramNode extends TNode {

    private List<TNode> statements;

    public ProgramNode(List<TNode> statements) {
        this.statements = statements != null ? statements : new ArrayList<>();
    }

    public ProgramNode() {
        this.statements = new ArrayList<>();
    }

    public List<TNode> getStatements() {
        return statements;
    }

    public void addStatement(TNode statement) {
        statements.add(statement);
    }

    @Override
    public String toCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < statements.size(); i++) {
            code.append(statements.get(i).toCode());
            if (i < statements.size() - 1) {
                code.append("\n");
            }
        }
        return code.toString();
    }

    @Override
    protected void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + "PROGRAM");
        for (int i = 0; i < statements.size(); i++) {
            boolean isLast = (i == statements.size() - 1);
            statements.get(i).printTree(prefix + "│   ", isLast);
        }
    }
}

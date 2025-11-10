package lyc.compiler.icg_tree;
import java.util.List;

public class IfNode extends TNode {
    
    private TNode condition;
    private List<TNode> thenBlock;
    private List<TNode> elseBlock;

    public IfNode(TNode condition, List<TNode> thenBlock, List<TNode> elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    public IfNode(TNode condition, List<TNode> thenBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = null;
    }

    public TNode getCondition() {
        return condition;
    }

    public List<TNode> getThenBlock() {
        return thenBlock;
    }

    public List<TNode> getElseBlock() {
        return elseBlock;
    }

    public boolean hasElse() {
        return elseBlock != null && !elseBlock.isEmpty();
    }

    @Override
    public String toCode() {
        StringBuilder code = new StringBuilder();
        code.append("(IF ").append(condition.toCode()).append(" THEN [");

        for (int i = 0; i < thenBlock.size(); i++) {
            code.append(thenBlock.get(i).toCode());
            if (i < thenBlock.size() - 1) code.append(" ");
        }
        code.append("]");

        if (hasElse()) {
            code.append(" ELSE [");
            for (int i = 0; i < elseBlock.size(); i++) {
                code.append(elseBlock.get(i).toCode());
                if (i < elseBlock.size() - 1) code.append(" ");
            }
            code.append("]");
        }

        code.append(")");
        return code.toString();
    }
}

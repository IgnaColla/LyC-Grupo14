package lyc.compiler.icg_tree;
import java.util.List;

public class WhileNode extends TNode {
    private TNode condition;
    private List<TNode> body;

    public WhileNode(TNode condition, List<TNode> body) {
        this.condition = condition;
        this.body = body;
    }

    public TNode getCondition() {
        return condition;
    }

    public List<TNode> getBody() {
        return body;
    }

    @Override
    public String toCode() {
        StringBuilder code = new StringBuilder();
        code.append("(WHILE ").append(condition.toCode()).append(" DO [");
        
        for (int i = 0; i < body.size(); i++) {
            code.append(body.get(i).toCode());
            if (i < body.size() - 1) code.append(" ");
        }
        
        code.append("])");
        return code.toString();
    }
}

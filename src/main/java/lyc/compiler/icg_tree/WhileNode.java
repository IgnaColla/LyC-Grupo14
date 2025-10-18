package lyc.compiler.icg_tree;

import java.util.List;

/**
 * Representa un bucle WHILE.
 * Ejemplo: while (x < 10) {...}
 */
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
    
    @Override
    protected void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + "WHILE");
        
        // Condición
        System.out.println(prefix + (isTail ? "    " : "│   ") + "├── CONDITION:");
        condition.printTree(prefix + (isTail ? "    " : "│   ") + "│   ", true);
        
        // Body
        System.out.println(prefix + (isTail ? "    " : "│   ") + "└── DO:");
        for (int i = 0; i < body.size(); i++) {
            boolean isLast = (i == body.size() - 1);
            body.get(i).printTree(prefix + (isTail ? "    " : "│   ") + "    ", isLast);
        }
    }
}

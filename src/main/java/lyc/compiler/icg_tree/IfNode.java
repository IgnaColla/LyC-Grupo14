package lyc.compiler.icg_tree;

import java.util.List;

/**
 * Representa una sentencia IF con opcional ELSE.
 * Ejemplos: if (x > 5) {...} else {...}
 */
public class IfNode extends TNode {
    
    private TNode condition;
    private List<TNode> thenBlock;
    private List<TNode> elseBlock;  // Puede ser null
    
    /**
     * Constructor para IF con ELSE.
     */
    public IfNode(TNode condition, List<TNode> thenBlock, List<TNode> elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }
    
    /**
     * Constructor para IF sin ELSE.
     */
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
    
    @Override
    protected void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + "IF");
        
        // Condición
        System.out.println(prefix + (isTail ? "    " : "│   ") + "├── CONDITION:");
        condition.printTree(prefix + (isTail ? "    " : "│   ") + "│   ", true);
        
        // THEN block
        System.out.println(prefix + (isTail ? "    " : "│   ") + "├── THEN:");
        for (int i = 0; i < thenBlock.size(); i++) {
            boolean isLastThen = (i == thenBlock.size() - 1) && !hasElse();
            thenBlock.get(i).printTree(prefix + (isTail ? "    " : "│   ") + "│   ", isLastThen);
        }
        
        // ELSE block (si existe)
        if (hasElse()) {
            System.out.println(prefix + (isTail ? "    " : "│   ") + "└── ELSE:");
            for (int i = 0; i < elseBlock.size(); i++) {
                boolean isLastElse = (i == elseBlock.size() - 1);
                elseBlock.get(i).printTree(prefix + (isTail ? "    " : "│   ") + "    ", isLastElse);
            }
        }
    }
}

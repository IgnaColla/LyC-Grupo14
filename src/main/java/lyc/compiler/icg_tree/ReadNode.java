package lyc.compiler.icg_tree;

/**
 * Representa la función READ para entrada de datos.
 * Ejemplo: read(variable)
 */
public class ReadNode extends TNode {
    
    private String parameter;
    
    public ReadNode(String parameter) {
        this.parameter = parameter;
    }
    
    public String getParameter() {
        return parameter;
    }
    
    @Override
    public String toCode() {
        return "(READ " + parameter + ")";
    }
    
    @Override
    protected void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + "READ: " + parameter);
    }
}

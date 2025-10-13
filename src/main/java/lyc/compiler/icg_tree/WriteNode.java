package lyc.compiler.icg_tree;

/**
 * Representa la función WRITE para salida de datos.
 * Ejemplo: write("mensaje") o write(variable)
 */
public class WriteNode extends TNode {
    
    private String parameter;
    
    public WriteNode(String parameter) {
        this.parameter = parameter;
    }
    
    public String getParameter() {
        return parameter;
    }
    
    @Override
    public String toCode() {
        return "(WRITE " + parameter + ")";
    }
    
    @Override
    protected void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + "WRITE: " + parameter);
    }
}

package lyc.compiler.icg_tree;

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
}

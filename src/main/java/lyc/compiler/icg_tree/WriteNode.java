package lyc.compiler.icg_tree;

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
}

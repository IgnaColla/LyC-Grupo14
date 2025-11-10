package lyc.compiler.icg_tree;

public class LiteralNode extends TNode {

    private String value;

    public LiteralNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toCode() {
        return value;
    }
}

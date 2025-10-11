package lyc.compiler.icg_tree;

public abstract class TNode {

    public abstract String toCode();

    public void printTree() {
        printTree("", true);
    }

    protected abstract void printTree(String prefix, boolean isTail);
}

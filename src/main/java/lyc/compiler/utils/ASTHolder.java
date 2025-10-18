package lyc.compiler.utils;

import lyc.compiler.icg_tree.ProgramNode;

public class ASTHolder {

    private static ProgramNode ast = null;

    public static void setAST(ProgramNode tree) {
        ast = tree;
    }

    public static ProgramNode getAST() {
        return ast;
    }

    public static void clear() {
        ast = null;
    }

    public static boolean hasAST() {
        return ast != null;
    }
}

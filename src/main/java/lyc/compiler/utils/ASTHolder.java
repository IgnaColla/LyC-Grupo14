package lyc.compiler.utils;

import lyc.compiler.icg_tree.ProgramNode;

/**
 * Almacena el árbol sintáctico generado durante el parsing
 */
public class ASTHolder {
    
    private static ProgramNode ast = null;
    
    /**
     * Guarda el AST
     */
    public static void setAST(ProgramNode tree) {
        ast = tree;
    }
    
    /**
     * Obtiene el AST
     */
    public static ProgramNode getAST() {
        return ast;
    }
    
    /**
     * Limpia para nueva compilación
     */
    public static void clear() {
        ast = null;
    }
    
    /**
     * Verifica si hay un AST
     */
    public static boolean hasAST() {
        return ast != null;
    }
}


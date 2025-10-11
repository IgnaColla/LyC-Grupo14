package lyc.compiler.semantic;

import java.util.HashMap;
import java.util.Map;

/**
 * Validador semántico simple
 * Verifica variables y tipos
 */
public class SemanticChecker {
    
    private static Map<String, String> variables = new HashMap<>();
    private static boolean hasErrors = false;
    
    /**
     * Declara una variable
     */
    public static void declare(String name, String type) {
        if (variables.containsKey(name)) {
            error("Variable '" + name + "' ya declarada");
        } else {
            variables.put(name, type);
        }
    }
    
    /**
     * Verifica si una variable existe
     */
    public static void checkExists(String name) {
        if (!variables.containsKey(name)) {
            error("Variable '" + name + "' no declarada");
        }
    }
    
    /**
     * Obtiene el tipo de una variable
     */
    public static String getType(String name) {
        return variables.get(name);
    }
    
    /**
     * Reporta error
     */
    private static void error(String msg) {
        System.err.println("[ERROR SEMANTICO] " + msg);
        hasErrors = true;
    }
    
    /**
     * Verifica si hay errores
     */
    public static boolean hasErrors() {
        return hasErrors;
    }
    
    /**
     * Limpia para nueva compilación
     */
    public static void clear() {
        variables.clear();
        hasErrors = false;
    }
}


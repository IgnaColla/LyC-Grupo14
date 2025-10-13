package lyc.compiler.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Validador semántico
 * Verifica variables y compatibilidad de tipos
 * Nota: Las validaciones de cotas y longitudes se realizan en el Lexer
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
     * Verifica compatibilidad de tipos en asignación
     */
    public static void checkTypeCompatibility(String varName, String exprType) {
        if (!variables.containsKey(varName)) {
            // Ya se reportó el error en checkExists
            return;
        }
        
        String varType = variables.get(varName);
        
        // Si alguno es desconocido, no validar
        if (varType == null || exprType == null || varType.equals("-") || exprType.equals("-")) {
            return;
        }
        
        // Reglas de compatibilidad
        if (!areTypesCompatible(varType, exprType)) {
            error("Incompatibilidad de tipos: no se puede asignar " + exprType + " a variable de tipo " + varType);
        }
    }
    
    /**
     * Verifica si dos tipos son compatibles
     */
    private static boolean areTypesCompatible(String targetType, String sourceType) {
        // Mismo tipo es compatible
        if (targetType.equals(sourceType)) {
            return true;
        }
        
        // Int puede asignarse a Float
        if (targetType.equals("Float") && sourceType.equals("Int")) {
            return true;
        }
        
        // Otras combinaciones no son compatibles
        return false;
    }
    
    /**
     * Infiere el tipo de una expresión
     * Retorna "Int", "Float", "String" o "-" si no puede determinar
     */
    public static String inferExpressionType(String expr) {
        if (expr == null) return "-";
        
        // Si es una variable, devolver su tipo
        if (variables.containsKey(expr)) {
            return variables.get(expr);
        }
        
        // Si es un literal
        if (expr.startsWith("\"")) {
            return "String";
        }
        
        // Si contiene punto decimal, es Float
        if (expr.contains(".")) {
            return "Float";
        }
        
        // Intentar parsear como entero
        try {
            Integer.parseInt(expr);
            return "Int";
        } catch (NumberFormatException e) {
            // No es un literal simple
        }
        
        return "-";
    }
    
    /**
     * Reporta error
     */
    private static void error(String msg) {
        System.err.println("[ERROR SEMANTICO] " + msg);
        hasErrors = true;
    }
    
    /**
     * Reporta advertencia (no detiene compilación)
     */
    public static void warning(String msg) {
        System.err.println("[WARNING] " + msg);
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


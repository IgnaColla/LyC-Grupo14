package lyc.compiler.utils;

import java.util.HashMap;
import java.util.Map;

public class SemanticChecker {
    
    private static Map<String, String> variables = new HashMap<>();
    private static boolean hasErrors = false;

    public static void declare(String name, String type) {
        if (variables.containsKey(name)) {
            error("Variable '" + name + "' already declared");
        } else {
            variables.put(name, type);
        }
    }

    public static void checkExists(String name) {
        if (!variables.containsKey(name)) {
            error("Variable '" + name + "' not declared");
        }
    }

    public static String getType(String name) {
        return variables.get(name);
    }

    public static void checkTypeCompatibility(String varName, String exprType) {
        if (!variables.containsKey(varName)) {
            return;
        }
        
        String varType = variables.get(varName);
        
        if (varType == null || exprType == null || varType.equals("-") || exprType.equals("-")) {
            return;
        }

        if (!areTypesCompatible(varType, exprType)) {
            error("Type incompatibility: cannot assign type " + exprType + " to a variable of type " + varType);
        }
    }

    private static boolean areTypesCompatible(String targetType, String sourceType) {
        if (targetType.equals(sourceType)) {
            return true;
        }

        if (targetType.equals("Float") && sourceType.equals("Int")) {
            return true;
        }

        return false;
    }

    public static String inferExpressionType(String expr) {
        if (expr == null) return "-";

        if (variables.containsKey(expr)) {
            return variables.get(expr);
        }

        if (expr.startsWith("\"")) {
            return "String";
        }

        if (expr.contains(".")) {
            return "Float";
        }

        try {
            Integer.parseInt(expr);
            return "Int";
        } catch (NumberFormatException e) {
        }
        
        return "-";
    }

    private static void error(String msg) {
        System.err.println("[SEMANTIC ERROR] " + msg);
        hasErrors = true;
    }

    public static void warning(String msg) {
        System.err.println("[WARNING] " + msg);
    }

    public static boolean hasErrors() {
        return hasErrors;
    }

    public static void clear() {
        variables.clear();
        hasErrors = false;
    }
}


package lyc.compiler.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lyc.compiler.icg_tree.AggregateNode;
import lyc.compiler.icg_tree.ExpressionNode;
import lyc.compiler.icg_tree.LiteralNode;
import lyc.compiler.icg_tree.TNode;

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
        if (expr == null) {
            return "-";
        }

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

    public static String inferType(TNode n) {
        if (n instanceof LiteralNode) {
            String v = ((LiteralNode) n).getValue();
            if (v.startsWith("\"")) {
                return "String";
            }
            if (variables.containsKey(v)) {
                return variables.get(v);
            }
            if (Character.isLetter(v.charAt(0))) {
                return "-";
            }
            return v.contains(".") ? "Float" : "Int";
        }
        if (n instanceof ExpressionNode) {
            String l = inferType(((ExpressionNode) n).getLeft()), r = inferType(((ExpressionNode) n).getRight());
            if (l.equals("String") || r.equals("String")) {
                return "String";
            }
            return (l.equals("Float") || r.equals("Float")) ? "Float" : "Int";
        }
        if (n instanceof AggregateNode) {
            return ((AggregateNode) n).getResultType();
        }
        return "-";
    }

    public static String aggregateResultType(String op, List<TNode> items) {
        for (TNode it : items) {
            if ("String".equals(inferType(it))) {
                error("agg(" + op + "): no se admiten elementos string");
            }
        }
        if (op.equals("COUNT")) {
            return "Int";
        }
        if (op.equals("AVG")) {
            return "Float";
        }
        for (TNode it : items) {
            if ("Float".equals(inferType(it))) {
                return "Float";
            }
        }
        return "Int";
    }

    public static void checkAggregateAssignment(String var, TNode expr) {
        if (containsAggregate(expr)) {
            checkTypeCompatibility(var, inferType(expr));
        }
    }

    private static boolean containsAggregate(TNode n) {
        if (n instanceof AggregateNode) {
            return true;
        }
        if (n instanceof ExpressionNode) {
            ExpressionNode e = (ExpressionNode) n;
            return containsAggregate(e.getLeft()) || containsAggregate(e.getRight());
        }
        return false;
    }
}

package lyc.compiler.codegen;

import java.util.ArrayList;
import java.util.List;

/**
 * Generador de código intermedio simple
 * Guarda el código mientras se parsea
 */
public class CodeGenerator {
    
    private static List<String> code = new ArrayList<>();
    private static int tempCount = 0;
    private static int labelCount = 0;
    
    /**
     * Agrega una línea de código
     */
    public static void add(String line) {
        code.add(line);
    }
    
    /**
     * Genera una nueva temporal
     */
    public static String newTemp() {
        return "_t" + (tempCount++);
    }
    
    /**
     * Genera una nueva etiqueta
     */
    public static String newLabel() {
        return "L" + (labelCount++);
    }
    
    /**
     * Obtiene todo el código
     */
    public static List<String> getCode() {
        return code;
    }
    
    /**
     * Limpia para nueva compilación
     */
    public static void clear() {
        code.clear();
        tempCount = 0;
        labelCount = 0;
    }
}


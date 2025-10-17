package lyc.compiler.utils;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {
    
    private static List<String> code = new ArrayList<>();
    private static int tempCount = 0;
    private static int labelCount = 0;

    public static void add(String line) {
        code.add(line);
    }

    public static String newTemp() {
        return "_t" + (tempCount++);
    }

    public static String newLabel() {
        return "L" + (labelCount++);
    }

    public static List<String> getCode() {
        return code;
    }

    public static void clear() {
        code.clear();
        tempCount = 0;
        labelCount = 0;
    }
}

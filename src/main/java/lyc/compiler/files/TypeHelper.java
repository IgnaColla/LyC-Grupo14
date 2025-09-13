package lyc.compiler.files;

public class TypeHelper {
    private static String currentType = "";
    
    public static void setCurrentType(String type) {
        currentType = type;
    }
    
    public static String getCurrentType() {
        return currentType;
    }
    
    public static void clear() {
        currentType = "";
    }
}

package lyc.compiler.files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private static SymbolTable instance;
    private Map<String, Symbol> symbols;
    
    private SymbolTable() {
        symbols = new HashMap<>();
    }
    
    public static SymbolTable getInstance() {
        if (instance == null) {
            instance = new SymbolTable();
        }
        return instance;
    }
    
    public void addVariable(String name, String type) {
        if (!symbols.containsKey(name)) {
            symbols.put(name, new Symbol(name, "VARIABLE", "-", null));
            System.out.println("Variable added to table: " + name + " (" + type + ")");
        }
    }
    
    public void addConstant(String value, String type) {
        String key = value + "_" + type;
        if (!symbols.containsKey(key)) {
            String symbolType = convertToSymbolType(type);

            Integer length = null;
            if ("CTE_STRING".equals(symbolType)) {
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    length = value.length() - 2;
                } else {
                    length = value.length();
                }
            }

            symbols.put(key, new Symbol(value, "CONSTANT", symbolType, value, length));
            System.out.println("Constant added to table: " + value + " (" + symbolType + ")");
        }
    }

    private String convertToSymbolType(String type) {
        return switch (type) {
            case "Int" -> "CTE_INTEGER";
            case "Float" -> "CTE_FLOAT";
            case "String" -> "CTE_STRING";
            default -> type;
        };
    }

    public boolean exists(String name) {
        return symbols.containsKey(name);
    }
    
    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }

    public void generateSymbolTableFile() {
        System.out.println("Generating symbols table... Total symbols: " + symbols.size());

        try {
            File outputFile = new File("target/output/symbols-table.txt");
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(outputFile)) {
                // Header
                writer.write("\n" + "=".repeat(110) + "\n");
                writer.write(centerText("SYMBOLS TABLE", 110) + "\n");
                writer.write("=".repeat(110) + "\n");

                // Column headers
                String format = "| %-35s | %-13s | %-35s | %-8s |\n";
                writer.write(String.format(format, "NAME", "TYPE", "VALUE", "LENGTH"));
                writer.write("-".repeat(110) + "\n");

                // Symbol entries
                for (Symbol symbol : symbols.values()) {
                    String name = symbol.getName();
                    String type = symbol.getType() != null ? symbol.getType() : "-";
                    String value = symbol.getValue() != null ? symbol.getValue() : "-";
                    String length = symbol.getLength() != null ? 
                                   String.valueOf(symbol.getLength()) : "-";
                    
                    writer.write(String.format(format, name, type, value, length));
                }

                // Footer
                writer.write("=".repeat(110) + "\n");
                writer.write("Total symbols: " + symbols.size() + "\n");
                writer.write("=".repeat(110) + "\n\n");
            }
            // System.out.println("Symbols table generated successfully.");
        } catch (IOException e) {
            System.err.println("Error generating symbols-table.txt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void clear() {
        symbols.clear();
    }

    public void updateVariablesWithType(String type) {
        // Update vars that have "-" with the wright type
        for (Symbol symbol : symbols.values()) {
            if (symbol.getCategory().equals("VARIABLE") && symbol.getType().equals("-")) {
                symbol.setType(type);
            }
        }
    }

    public void updateVariableType(String name, String type) {
        // Update the type of a specific variable
        Symbol symbol = symbols.get(name);
        if (symbol != null && symbol.getCategory().equals("VARIABLE")) {
            symbol.setType(type);
        }
    }

    public Collection<Symbol> getSymbols() {
        return symbols.values();
    }

    public int getSymbolCount() {
        return symbols.size();
    }

    public static class Symbol {
        private String name;
        private String category; // VARIABLE or CONSTANT
        private String type;     // CTE_INTEGER, CTE_FLOAT, CTE_STRING, or "-" for variables
        private String value;    // Only for constants
        private Integer length;  // Only for CTE_STRING

        public Symbol(String name, String category, String type, String value) {
            this(name, category, type, value, null);
        }

        public Symbol(String name, String category, String type, String value, Integer length) {
            this.name = name;
            this.category = category;
            this.type = type;
            this.value = value;
            this.length = length;
        }

        public void setType(String type) { 
            this.type = type; 
        }

        public boolean isConstant() { 
            return "CONSTANT".equals(this.category); 
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getType() { return type; }
        public String getValue() { return value; }
        public Integer getLength() { return length; }
    }
}

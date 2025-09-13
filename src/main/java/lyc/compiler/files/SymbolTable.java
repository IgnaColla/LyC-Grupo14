package lyc.compiler.files;

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
            symbols.put(name, new Symbol(name, "VARIABLE", type, null));
            System.out.println("Variable added to table: " + name + " (" + type + ")");
        }
    }
    
    public void addConstant(String value, String type) {
        String key = value + "_" + type;
        if (!symbols.containsKey(key)) {
            symbols.put(key, new Symbol(value, "CONSTANT", type, value));
            System.out.println("Constant added to table: " + value + " (" + type + ")");
        }
    }
    
    public boolean exists(String name) {
        return symbols.containsKey(name);
    }
    
    public void generateSymbolTableFile() {
        System.out.println("Generating symbols table... Total symbols: " + symbols.size());
        
        try (FileWriter writer = new FileWriter("./symbols-table.txt")) {
            writer.write("SYMBOLS TABLE\n");
            writer.write("=================\n\n");
            writer.write(String.format("%-20s %-15s %-20s %-15s \n", 
                "NAME", "TYPE", "VALUE", "LENGTH"  ));
            writer.write("----------------------------------------------------------------\n");
            
            for (Symbol symbol : symbols.values()) {
                writer.write(String.format("%-20s %-15s %-20s %-15s\n",
                    symbol.getName(),
                    symbol.getType(),
                    symbol.getValue() != null ? symbol.getValue() : "-",
                    symbol.getValue() != null ? symbol.getValue().length() : "0"));
            }
            
            writer.write("\nTotal symbols: " + symbols.size() + "\n");
            System.out.println("Symbols table generated successfully.");
            
        } catch (IOException e) {
            System.err.println("Error generating symbols-table.txt: " + e.getMessage());
        }
    }
    
    public void clear() {
        symbols.clear();
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
        private String type;     // Int, Float, String
        private String value;    // Only for constants
        
        public Symbol(String name, String category, String type, String value) {
            this.name = name;
            this.category = category;
            this.type = type;
            this.value = value;
        }
        
        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getType() { return type; }
        public String getValue() { return value; }
    }
}

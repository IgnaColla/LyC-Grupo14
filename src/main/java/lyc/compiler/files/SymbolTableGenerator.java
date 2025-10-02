package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;

public class SymbolTableGenerator implements FileGenerator {

    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        SymbolTable symbolTable = SymbolTable.getInstance();

        // Header
        fileWriter.write("\n" + "=".repeat(110) + "\n");
        fileWriter.write(centerText("SYMBOLS TABLE", 110) + "\n");
        fileWriter.write("=".repeat(110) + "\n");

        // Column headers
        String format = "| %-35s | %-13s | %-35s | %-8s |\n";
        fileWriter.write(String.format(format, "NAME", "TYPE", "VALUE", "LENGTH"));
        fileWriter.write("-".repeat(110) + "\n");

        // Symbol entries
        for (Object sym : symbolTable.getSymbols()) {
            SymbolTable.Symbol s = (SymbolTable.Symbol) sym;
            String name = s.getName();
            String type = s.getType() != null ? s.getType() : "-";
            String value = s.getValue() != null ? s.getValue() : "-";
            String length = s.getValue() != null ? 
                           String.valueOf(s.getValue().length()) : "0";
            
            fileWriter.write(String.format(format, name, type, value, length));
        }

        // Footer
        fileWriter.write("=".repeat(110) + "\n");
        fileWriter.write("Total symbols: " + symbolTable.getSymbolCount() + "\n");
        fileWriter.write("=".repeat(110) + "\n\n");
        
        fileWriter.flush();
    }
}

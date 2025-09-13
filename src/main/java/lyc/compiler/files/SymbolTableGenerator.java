package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;

public class SymbolTableGenerator implements FileGenerator {

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        SymbolTable symbolTable = SymbolTable.getInstance();

        fileWriter.write("SYMBOLS TABLE\n");
        fileWriter.write("=================\n\n");
        fileWriter.write(String.format("%-20s %-15s %-20s %-15s \n",
                "NAME", "TYPE", "VALUE", "LENGTH"));
        fileWriter.write("----------------------------------------------------------------\n");

        for (Object sym : symbolTable.getSymbols()) {
            SymbolTable.Symbol s = (SymbolTable.Symbol) sym;
            fileWriter.write(String.format("%-20s %-15s %-20s %-15s\n",
                    s.getName(),
                    s.getType(),
                    s.getValue() != null ? s.getValue() : "-",
                    s.getValue() != null ? s.getValue().length() : "0"));
        }

        fileWriter.write("\nTotal symbols: " + symbolTable.getSymbolCount() + "\n");
        fileWriter.flush();
    }
}

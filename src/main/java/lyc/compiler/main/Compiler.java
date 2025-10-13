package lyc.compiler.main;

import lyc.compiler.Parser;
import lyc.compiler.factories.FileFactory;
import lyc.compiler.factories.ParserFactory;
import lyc.compiler.files.FileOutputWriter;
import lyc.compiler.files.SymbolTableGenerator;
import lyc.compiler.utils.CodeGenerator;
import lyc.compiler.utils.SemanticChecker;
import lyc.compiler.files.IntermediateCodeGenerator;

import java.io.IOException;
import java.io.Reader;

public final class Compiler {

    private Compiler(){}

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Filename must be provided as argument.");
            System.exit(0);
        }

        try (Reader reader = FileFactory.create(args[0])) {
            // Limpiar para nueva compilación
            CodeGenerator.clear();
            SemanticChecker.clear();
            lyc.compiler.utils.ASTHolder.clear();
            
            // Parsear
            Parser parser = ParserFactory.create(reader);
            parser.parse();
            
            // Verificar errores semánticos
            if (SemanticChecker.hasErrors()) {
                System.err.println("\n[ERROR] Compilación detenida por errores semánticos");
                System.exit(1);
            }
            
            // Generar archivos
            // FileOutputWriter.writeOutput("symbols-table.txt", new SymbolTableGenerator());
            FileOutputWriter.writeOutput("intermediate-code.txt", new IntermediateCodeGenerator());
            FileOutputWriter.writeOutput("final.asm", new SymbolTableGenerator());
            
            System.out.println("\n[OK] Compilación exitosa");
            System.out.println("Archivos generados:");
            System.out.println("  - symbol-table.txt");
            System.out.println("  - intermediate-code.txt");
            
        } catch (IOException e) {
            System.err.println("Error al leer archivo: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error de compilación: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

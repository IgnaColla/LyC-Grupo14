package lyc.compiler.main;

import lyc.compiler.Parser;
import lyc.compiler.factories.FileFactory;
import lyc.compiler.factories.ParserFactory;
import lyc.compiler.files.FileOutputWriter;
import lyc.compiler.files.GraphvizGenerator;
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
            CodeGenerator.clear();
            SemanticChecker.clear();
            lyc.compiler.utils.ASTHolder.clear();

            Parser parser = ParserFactory.create(reader);
            parser.parse();
            
            if (SemanticChecker.hasErrors()) {
                System.err.println("\n[ERROR] Compilation failed due to semantic errors");
                System.exit(1);
            }

            FileOutputWriter.writeOutput("intermediate-code.txt", new IntermediateCodeGenerator());
            FileOutputWriter.writeOutput("ast-tree.dot", new GraphvizGenerator());
            FileOutputWriter.writeOutput("final.asm", new SymbolTableGenerator());

            System.out.println("\n[OK] Successful compilation");
            System.out.println("Generated files:");
            System.out.println("  - symbol-table.txt");
            System.out.println("  - intermediate-code.txt");

        } catch (IOException e) {
            System.err.println("Error while reading file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Compilation error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

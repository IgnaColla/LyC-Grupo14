package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;

import lyc.compiler.icg_tree.ProgramNode;
import lyc.compiler.utils.ASTHolder;

/**
 * Escribe el código intermedio al archivo desde el AST
 */
public class IntermediateCodeGenerator implements FileGenerator {

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write("=============================================\n");
        fileWriter.write("    CODIGO INTERMEDIO (Desde AST)\n");
        fileWriter.write("=============================================\n\n");
        
        // Obtener el AST
        ProgramNode ast = ASTHolder.getAST();
        
        if (ast != null) {
            // Generar código desde el AST
            String code = ast.toCode();
            fileWriter.write(code + "\n");
        } else {
            fileWriter.write("// No se generó AST\n");
        }
        
        fileWriter.write("\n=============================================\n");
    }
}

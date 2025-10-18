package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;

import lyc.compiler.icg_tree.ProgramNode;
import lyc.compiler.utils.ASTHolder;

public class IntermediateCodeGenerator implements FileGenerator {

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write("=============================================\n");
        fileWriter.write("    INTERMEDIATE CODE (For Tree generation)\n");
        fileWriter.write("=============================================\n\n");

        // Obtain AST
        ProgramNode ast = ASTHolder.getAST();

        if (ast != null) {
            String code = ast.toCode();
            fileWriter.write(code + "\n");
        } else {
            fileWriter.write("// Tree not generated\n");
        }

        fileWriter.write("\n=============================================\n");
    }
}

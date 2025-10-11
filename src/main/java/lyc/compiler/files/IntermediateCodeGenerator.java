package lyc.compiler.files;

import lyc.compiler.codegen.CodeGenerator;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Escribe el código intermedio al archivo
 */
public class IntermediateCodeGenerator implements FileGenerator {

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write("=============================================\n");
        fileWriter.write("    CODIGO INTERMEDIO\n");
        fileWriter.write("=============================================\n\n");
        
        List<String> code = CodeGenerator.getCode();
        
        for (String line : code) {
            fileWriter.write(line + "\n");
        }
        
        fileWriter.write("\n=============================================\n");
    }
}

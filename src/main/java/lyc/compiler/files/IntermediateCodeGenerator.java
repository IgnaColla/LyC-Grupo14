package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;

import lyc.compiler.icg_tree.*;
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
            String code = processProgram(ast);
            fileWriter.write(code);
        } else {
            fileWriter.write("// Tree not generated\n");
        }

        fileWriter.write("\n+ Dot file for Graphviz visualization generated in 'target/output/ast-tree.dot'");
        fileWriter.write("\n=============================================\n");
    }

    private String processProgram(ProgramNode program) {
        StringBuilder code = new StringBuilder();
        if (program.getStatements() != null) {
            for (TNode stmt : program.getStatements()) {
                code.append(processNode(stmt)).append("\n");
            }
        }
        return code.toString();
    }

    private String processNode(TNode node) {
        if (node instanceof TriangleAreaNode) {
            TriangleAreaNode triangleNode = (TriangleAreaNode) node;
            if (triangleNode.getComparisonTree() != null) {
                return triangleNode.getComparisonTree().toCode();
            }
            return triangleNode.toCode();
        } 
        else if (node instanceof ConvDateNode) {
            ConvDateNode dateNode = (ConvDateNode) node;
            if (dateNode.getConversionTree() != null) {
                return dateNode.getConversionTree().toCode();
            }
            return dateNode.toCode();
        }
        else if (node instanceof AssignmentNode) {
            AssignmentNode assign = (AssignmentNode) node;
            String exprCode = processNode(assign.getExpression());
            return "(= " + assign.getVariable() + " " + exprCode + ")";
        }
        else if (node instanceof ProgramNode) {
            return processProgram((ProgramNode) node);
        }
        else {
            return node.toCode();
        }
    }
}

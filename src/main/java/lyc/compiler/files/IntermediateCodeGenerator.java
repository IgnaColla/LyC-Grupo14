package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;

import lyc.compiler.icg_tree.AggregateNode;
import lyc.compiler.icg_tree.AssignmentNode;
import lyc.compiler.icg_tree.ConditionNode;
import lyc.compiler.icg_tree.ConvDateNode;
import lyc.compiler.icg_tree.ExpressionNode;
import lyc.compiler.icg_tree.IfNode;
import lyc.compiler.icg_tree.ProgramNode;
import lyc.compiler.icg_tree.TNode;
import lyc.compiler.icg_tree.TriangleAreaNode;
import lyc.compiler.icg_tree.WhileNode;
import lyc.compiler.icg_tree.WriteExprNode;
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
                appendAggregateCode(stmt, code);          // expansión previa
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
        } else if (node instanceof ConvDateNode) {
            ConvDateNode dateNode = (ConvDateNode) node;
            if (dateNode.getConversionTree() != null) {
                return dateNode.getConversionTree().toCode();
            }
            return dateNode.toCode();
        } else if (node instanceof AssignmentNode) {
            AssignmentNode assign = (AssignmentNode) node;
            String exprCode = processNode(assign.getExpression());
            return "(= " + assign.getVariable() + " " + exprCode + ")";
        } else if (node instanceof ProgramNode) {
            return processProgram((ProgramNode) node);
        } else {
            return node.toCode();
        }
    }

    private void appendAggregateCode(TNode n, StringBuilder out) {   // post-orden: los anidados primero
        if (n == null) {
            return;
        }
        if (n instanceof AggregateNode) {
            AggregateNode a = (AggregateNode) n;
            for (TNode it : a.getItems()) {
                appendAggregateCode(it, out);
            }
            for (TNode step : a.expand()) {
                out.append(step.toCode()).append("\n");
            }
        } else if (n instanceof AssignmentNode) {
            appendAggregateCode(((AssignmentNode) n).getExpression(), out);
        } else if (n instanceof WriteExprNode) {
            appendAggregateCode(((WriteExprNode) n).getExpression(), out);
        } else if (n instanceof ExpressionNode) {
            appendAggregateCode(((ExpressionNode) n).getLeft(), out);
            appendAggregateCode(((ExpressionNode) n).getRight(), out);
        } else if (n instanceof ConditionNode) {
            appendAggregateCode(((ConditionNode) n).getLeft(), out);
            appendAggregateCode(((ConditionNode) n).getRight(), out);
        } else if (n instanceof IfNode) {
            IfNode i = (IfNode) n;
            appendAggregateCode(i.getCondition(), out);
            for (TNode t : i.getThenBlock()) {
                appendAggregateCode(t, out);
            }
            if (i.getElseBlock() != null) {
                for (TNode t : i.getElseBlock()) {
                    appendAggregateCode(t, out);
                }
            }
        } else if (n instanceof WhileNode) {
            WhileNode w = (WhileNode) n;
            out.append("// la condición/cuerpo con agg se re-evalúa en cada iteración\n");
            appendAggregateCode(w.getCondition(), out);
            for (TNode t : w.getBody()) {
                appendAggregateCode(t, out);
            }
        }
    }
}

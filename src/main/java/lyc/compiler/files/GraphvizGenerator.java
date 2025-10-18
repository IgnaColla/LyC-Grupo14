package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import lyc.compiler.icg_tree.*;
import lyc.compiler.utils.ASTHolder;
import java.util.List;

public class GraphvizGenerator implements FileGenerator {
    private int nodeCounter = 0;
    private StringBuilder dotContent;

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        nodeCounter = 0;
        dotContent = new StringBuilder();

        dotContent.append("digraph AST {\n");
        dotContent.append("    node [shape=box, style=rounded, fontname=\"Arial\"];\n");
        dotContent.append("    edge [fontname=\"Arial\"];\n\n");

        ProgramNode ast = ASTHolder.getAST();
        if (ast != null) {
            generateNode(ast, -1);
        }

        dotContent.append("}\n");
        fileWriter.write(dotContent.toString());
    }

    private String generateNode(TNode node, int parentId) {
        if (node == null) return null;

        int currentId = nodeCounter++;
        String nodeName = "node" + currentId;

        if (node instanceof ProgramNode) {
            ProgramNode pn = (ProgramNode) node;
            List<TNode> statements = pn.getStatements();

            dotContent.append(String.format("    %s [label=\"PROGRAM\"];\n", nodeName));

            if (!statements.isEmpty()) {
                String stmtTreeRoot = buildStatementTree(statements, 0);
                dotContent.append(String.format("    %s -> %s;\n", nodeName, stmtTreeRoot));
            }
        } 
        else if (node instanceof AssignmentNode) {
            AssignmentNode an = (AssignmentNode) node;
            dotContent.append(String.format("    %s [label=\"ASSIGN\"];\n", nodeName));

            int varId = nodeCounter++;
            String varName = "node" + varId;
            dotContent.append(String.format("    %s [label=\"%s\", shape=ellipse, style=filled, fillcolor=lightgreen];\n", 
                varName, an.getVariable()));
            dotContent.append(String.format("    %s -> %s;\n", nodeName, varName));

            String exprName = generateNode(an.getExpression(), currentId);
            dotContent.append(String.format("    %s -> %s;\n", nodeName, exprName));
        }
        else if (node instanceof ExpressionNode) {
            ExpressionNode en = (ExpressionNode) node;
            dotContent.append(String.format("    %s [label=\"%s\"];\n", nodeName, en.getOperator()));

            String leftName = generateNode(en.getLeft(), currentId);
            String rightName = generateNode(en.getRight(), currentId);
            dotContent.append(String.format("    %s -> %s;\n", nodeName, leftName));
            dotContent.append(String.format("    %s -> %s;\n", nodeName, rightName));
        }
        else if (node instanceof LiteralNode) {
            LiteralNode ln = (LiteralNode) node;
            String value = ln.getValue().replace("\\", "\\\\").replace("\"", "\\\"");
            dotContent.append(String.format("    %s [label=\"%s\", shape=ellipse, style=filled, fillcolor=lightblue];\n", 
                nodeName, value));
        }
        else if (node instanceof IfNode) {
            IfNode ifn = (IfNode) node;
            dotContent.append(String.format("    %s [label=\"IF\"];\n", nodeName));

            String condName = generateNode(ifn.getCondition(), currentId);
            dotContent.append(String.format("    %s -> %s;\n", nodeName, condName));

            boolean hasThen = ifn.getThenBlock() != null && !ifn.getThenBlock().isEmpty();
            boolean hasElse = ifn.hasElse() && ifn.getElseBlock() != null && !ifn.getElseBlock().isEmpty();

            if (hasThen && !hasElse && ifn.getThenBlock().size() == 1) {
                String thenStmt = generateNode(ifn.getThenBlock().get(0), currentId);
                dotContent.append(String.format("    %s -> %s;\n", nodeName, thenStmt));
            } else if (hasThen || hasElse) {
                int branchId = nodeCounter++;
                String branchName = "node" + branchId;
                dotContent.append(String.format("    %s [label=\":\", shape=circle];\n", branchName));
                dotContent.append(String.format("    %s -> %s;\n", nodeName, branchName));

                if (hasThen) {
                    String thenTree = buildStatementTree(ifn.getThenBlock(), 0);
                    dotContent.append(String.format("    %s -> %s;\n", branchName, thenTree));
                }

                if (hasElse) {
                    String elseTree = buildStatementTree(ifn.getElseBlock(), 0);
                    dotContent.append(String.format("    %s -> %s;\n", branchName, elseTree));
                }
            }
        }
        else if (node instanceof WhileNode) {
            WhileNode wn = (WhileNode) node;
            dotContent.append(String.format("    %s [label=\"WHILE\"];\n", nodeName));

            String condName = generateNode(wn.getCondition(), currentId);
            dotContent.append(String.format("    %s -> %s;\n", nodeName, condName));

            if (wn.getBody() != null && !wn.getBody().isEmpty()) {
                String bodyTree = buildStatementTree(wn.getBody(), 0);
                dotContent.append(String.format("    %s -> %s;\n", nodeName, bodyTree));
            }
        }
        else if (node instanceof ConditionNode) {
            ConditionNode cn = (ConditionNode) node;
            dotContent.append(String.format("    %s [label=\"%s\"];\n", nodeName, cn.getOperator()));

            String leftName = generateNode(cn.getLeft(), currentId);
            String rightName = generateNode(cn.getRight(), currentId);
            dotContent.append(String.format("    %s -> %s;\n", nodeName, leftName));
            dotContent.append(String.format("    %s -> %s;\n", nodeName, rightName));
        }
        else if (node instanceof ReadNode) {
            ReadNode rn = (ReadNode) node;
            dotContent.append(String.format("    %s [label=\"READ\"];\n", nodeName));

            int paramId = nodeCounter++;
            String paramName = "node" + paramId;
            dotContent.append(String.format("    %s [label=\"%s\", shape=ellipse, style=filled, fillcolor=lightgreen];\n", 
                paramName, rn.getParameter()));
            dotContent.append(String.format("    %s -> %s;\n", nodeName, paramName));
        }
        else if (node instanceof WriteNode) {
            WriteNode wn = (WriteNode) node;
            dotContent.append(String.format("    %s [label=\"WRITE\"];\n", nodeName));

            int paramId = nodeCounter++;
            String paramName = "node" + paramId;
            String param = wn.getParameter().replace("\\", "\\\\").replace("\"", "\\\"");
            dotContent.append(String.format("    %s [label=\"%s\", shape=ellipse, style=filled, fillcolor=lightyellow];\n", 
                paramName, param));
            dotContent.append(String.format("    %s -> %s;\n", nodeName, paramName));
        }
        else if (node instanceof TriangleAreaNode) {
            TriangleAreaNode tan = (TriangleAreaNode) node;
            dotContent.append(String.format("    %s [label=\"TRIANGLE_AREA\"];\n", nodeName));

            String tri1Name = buildTriangleTree(tan.getTriangle1(), "T1");
            dotContent.append(String.format("    %s -> %s;\n", nodeName, tri1Name));

            String tri2Name = buildTriangleTree(tan.getTriangle2(), "T2");
            dotContent.append(String.format("    %s -> %s;\n", nodeName, tri2Name));
        }
        else if (node instanceof ConvDateNode) {
            ConvDateNode cdn = (ConvDateNode) node;
            dotContent.append(String.format("    %s [label=\"CONV_DATE\"];\n", nodeName));

            int dateId = nodeCounter++;
            String dateName = "node" + dateId;
            String dateValue = cdn.getDay() + "-" + cdn.getMonth() + "-" + cdn.getYear();
            dotContent.append(String.format("    %s [label=\"%s\", shape=ellipse, style=filled, fillcolor=lightyellow];\n", 
                dateName, dateValue));
            dotContent.append(String.format("    %s -> %s;\n", nodeName, dateName));
        }

        return nodeName;
    }

    private String buildTriangleTree(List<TriangleAreaNode.Coordinate> coords, String label) {
        int triId = nodeCounter++;
        String triName = "node" + triId;
        dotContent.append(String.format("    %s [label=\"%s\", shape=circle];\n", triName, label));

        String coordTreeRoot = buildCoordinateTree(coords, 0);
        if (coordTreeRoot != null) {
            dotContent.append(String.format("    %s -> %s;\n", triName, coordTreeRoot));
        }
        
        return triName;
    }

    private String buildCoordinateTree(List<TriangleAreaNode.Coordinate> coords, int index) {
        if (coords == null || index >= coords.size()) {
            return null;
        }
        
        if (index == coords.size() - 1) {
            return createCoordinateNode(coords.get(index));
        }

        int seqId = nodeCounter++;
        String seqName = "node" + seqId;
        dotContent.append(String.format("    %s [label=\";\", shape=circle];\n", seqName));

        String leftChild = createCoordinateNode(coords.get(index));
        dotContent.append(String.format("    %s -> %s;\n", seqName, leftChild));

        String rightChild = buildCoordinateTree(coords, index + 1);
        if (rightChild != null) {
            dotContent.append(String.format("    %s -> %s;\n", seqName, rightChild));
        }

        return seqName;
    }

    private String createCoordinateNode(TriangleAreaNode.Coordinate coord) {
        int coordId = nodeCounter++;
        String coordName = "node" + coordId;
        dotContent.append(String.format("    %s [label=\",\"];\n", coordName));

        int xId = nodeCounter++;
        String xName = "node" + xId;
        dotContent.append(String.format("    %s [label=\"%s\", shape=ellipse, style=filled, fillcolor=lightblue];\n", 
            xName, coord.getX()));
        dotContent.append(String.format("    %s -> %s;\n", coordName, xName));

        int yId = nodeCounter++;
        String yName = "node" + yId;
        dotContent.append(String.format("    %s [label=\"%s\", shape=ellipse, style=filled, fillcolor=lightblue];\n", 
            yName, coord.getY()));
        dotContent.append(String.format("    %s -> %s;\n", coordName, yName));

        return coordName;
    }

    private String buildStatementTree(List<TNode> statements, int index) {
        if (statements == null || index >= statements.size()) {
            return null;
        }

        if (index == statements.size() - 1) {
            return generateNode(statements.get(index), -1);
        }

        int seqId = nodeCounter++;
        String seqName = "node" + seqId;
        dotContent.append(String.format("    %s [label=\";\", shape=circle];\n", seqName));

        String leftChild = generateNode(statements.get(index), seqId);
        dotContent.append(String.format("    %s -> %s;\n", seqName, leftChild));

        String rightChild = buildStatementTree(statements, index + 1);
        if (rightChild != null) {
            dotContent.append(String.format("    %s -> %s;\n", seqName, rightChild));
        }

        return seqName;
    }
}

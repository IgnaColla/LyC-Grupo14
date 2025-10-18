package lyc.compiler;

import org.junit.jupiter.api.Test;

import lyc.compiler.icg_tree.AssignmentNode;
import lyc.compiler.icg_tree.ConditionNode;
import lyc.compiler.icg_tree.ExpressionNode;
import lyc.compiler.icg_tree.LiteralNode;
import lyc.compiler.icg_tree.ProgramNode;
import lyc.compiler.icg_tree.TNode;
import lyc.compiler.icg_tree.ReadNode;
import lyc.compiler.icg_tree.WriteNode;
import lyc.compiler.icg_tree.ConvDateNode;
import lyc.compiler.icg_tree.TriangleAreaNode;
import lyc.compiler.icg_tree.WhileNode;
import lyc.compiler.icg_tree.IfNode;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests simples para los nodos del árbol de código intermedio.
 */
public class TNodeTest {

    @Test
    public void testLiteralNode() {
        LiteralNode node = new LiteralNode("5");
        assertEquals("5", node.toCode());
    }

    @Test
    public void testExpressionNode() {
        LiteralNode left = new LiteralNode("5");
        LiteralNode right = new LiteralNode("3");
        ExpressionNode expr = new ExpressionNode("+", left, right);
        assertEquals("(+ 5 3)", expr.toCode());
    }

    @Test
    public void testAssignmentNode() {
        LiteralNode value = new LiteralNode("10");
        AssignmentNode assign = new AssignmentNode("x", value);
        assertEquals("(= x 10)", assign.toCode());
    }

    @Test
    public void testConditionNode() {
        LiteralNode left = new LiteralNode("x");
        LiteralNode right = new LiteralNode("5");
        ConditionNode cond = new ConditionNode(">", left, right);
        assertEquals("(> x 5)", cond.toCode());
    }

    @Test
    public void testConditionNodeNOT() {
        LiteralNode operand = new LiteralNode("flag");
        ConditionNode cond = new ConditionNode("NOT", operand);
        assertEquals("(NOT flag)", cond.toCode());
    }

    @Test
    public void testProgramNode() {
        LiteralNode value = new LiteralNode("42");
        AssignmentNode assign = new AssignmentNode("answer", value);
        
        List<TNode> statements = new ArrayList<>();
        statements.add(assign);
        
        ProgramNode program = new ProgramNode(statements);
        assertEquals("(= answer 42)", program.toCode());
    }

    @Test
    public void testCompleteProgram() {
        // x = 5 + 3
        LiteralNode five = new LiteralNode("5");
        LiteralNode three = new LiteralNode("3");
        ExpressionNode sum = new ExpressionNode("+", five, three);
        AssignmentNode assign = new AssignmentNode("x", sum);
        
        List<TNode> statements = new ArrayList<>();
        statements.add(assign);
        
        ProgramNode program = new ProgramNode(statements);
        assertEquals("(= x (+ 5 3))", program.toCode());
    }

    @Test
    public void testIfNode() {
        LiteralNode x = new LiteralNode("x");
        LiteralNode five = new LiteralNode("5");
        ConditionNode cond = new ConditionNode(">", x, five);
        
        LiteralNode one = new LiteralNode("1");
        AssignmentNode assign = new AssignmentNode("result", one);
        
        List<TNode> thenBlock = new ArrayList<>();
        thenBlock.add(assign);
        
        IfNode ifNode = new IfNode(cond, thenBlock);
        String expected = "(IF (> x 5) THEN [(= result 1)])";
        assertEquals(expected, ifNode.toCode());
    }

    @Test
    public void testIfNodeWithElse() {
        LiteralNode x = new LiteralNode("x");
        LiteralNode five = new LiteralNode("5");
        ConditionNode cond = new ConditionNode(">", x, five);
        
        LiteralNode one = new LiteralNode("1");
        AssignmentNode thenAssign = new AssignmentNode("result", one);
        
        LiteralNode zero = new LiteralNode("0");
        AssignmentNode elseAssign = new AssignmentNode("result", zero);
        
        List<TNode> thenBlock = new ArrayList<>();
        thenBlock.add(thenAssign);
        
        List<TNode> elseBlock = new ArrayList<>();
        elseBlock.add(elseAssign);
        
        IfNode ifNode = new IfNode(cond, thenBlock, elseBlock);
        String expected = "(IF (> x 5) THEN [(= result 1)] ELSE [(= result 0)])";
        assertEquals(expected, ifNode.toCode());
    }

    @Test
    public void testWhileNode() {
        LiteralNode x = new LiteralNode("x");
        LiteralNode ten = new LiteralNode("10");
        ConditionNode cond = new ConditionNode("<", x, ten);
        
        LiteralNode one = new LiteralNode("1");
        ExpressionNode increment = new ExpressionNode("+", x, one);
        AssignmentNode assign = new AssignmentNode("x", increment);
        
        List<TNode> body = new ArrayList<>();
        body.add(assign);
        
        WhileNode whileNode = new WhileNode(cond, body);
        String expected = "(WHILE (< x 10) DO [(= x (+ x 1))])";
        assertEquals(expected, whileNode.toCode());
    }

    @Test
    public void testReadNode() {
        ReadNode readNode = new ReadNode("input");
        assertEquals("(READ input)", readNode.toCode());
    }

    @Test
    public void testWriteNode() {
        WriteNode writeNode = new WriteNode("\"mensaje\"");
        assertEquals("(WRITE \"mensaje\")", writeNode.toCode());
    }

    @Test
    public void testConvDateNode() {
        ConvDateNode dateNode = new ConvDateNode("25", "12", "2023");
        assertEquals("(CONVDATE 25-12-2023)", dateNode.toCode());
    }

    @Test
    public void testTriangleAreaNode() {
        List<TriangleAreaNode.Coordinate> t1 = new ArrayList<>();
        t1.add(new TriangleAreaNode.Coordinate("1", "2"));
        t1.add(new TriangleAreaNode.Coordinate("3", "4"));
        t1.add(new TriangleAreaNode.Coordinate("5", "6"));
        
        List<TriangleAreaNode.Coordinate> t2 = new ArrayList<>();
        t2.add(new TriangleAreaNode.Coordinate("7", "8"));
        t2.add(new TriangleAreaNode.Coordinate("9", "10"));
        t2.add(new TriangleAreaNode.Coordinate("11", "12"));
        
        TriangleAreaNode triangleNode = new TriangleAreaNode(t1, t2);
        String expected = "(TRIANGLE_AREA_MAXIMUM [(1,2);(3,4);(5,6)] [(7,8);(9,10);(11,12)])";
        assertEquals(expected, triangleNode.toCode());
    }
}

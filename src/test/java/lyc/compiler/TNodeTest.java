package lyc.compiler;

import org.junit.jupiter.api.Test;

import lyc.compiler.icg_tree.AssignmentNode;
import lyc.compiler.icg_tree.ConditionNode;
import lyc.compiler.icg_tree.ExpressionNode;
import lyc.compiler.icg_tree.LiteralNode;
import lyc.compiler.icg_tree.ProgramNode;
import lyc.compiler.icg_tree.TNode;

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
}

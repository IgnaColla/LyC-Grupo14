package lyc.compiler.icg_tree;

import java.util.ArrayList;
import java.util.List;
import lyc.compiler.utils.SemanticChecker;

public class AggregateNode extends TNode {

    private static int counter = 0;

    public static void resetCounter() {
        counter = 0;
    }
    private final int id;
    private final String op;
    private final List<TNode> items;
    private final String resultType;

    public AggregateNode(String op, List<TNode> items) {
        this.id = counter++;
        this.op = op;
        this.items = items;
        this.resultType = SemanticChecker.aggregateResultType(op, items);
    }

    public List<TNode> expand() {
        List<TNode> out = new ArrayList<>();
        int n = items.size();
        TNode res = new LiteralNode(getResultVar());
        if (op.equals("COUNT")) { // constante en compilación
            out.add(new AssignmentNode(getResultVar(), new LiteralNode(String.valueOf(n))));
        } else if (op.equals("SUM") || op.equals("AVG")) {   // acumulación
            out.add(new AssignmentNode(getResultVar(), new LiteralNode("0")));
            for (int i = 0; i < n; i++) {
                out.add(new AssignmentNode(getItemVar(i), items.get(i)));
                out.add(new AssignmentNode(getResultVar(),
                        new ExpressionNode("+", res, new LiteralNode(getItemVar(i)))));
            }
            if (op.equals("AVG")) // división final
            {
                out.add(new AssignmentNode(getResultVar(),
                        new ExpressionNode("/", res, new LiteralNode(String.valueOf(n)))));
            }
        } else { // MIN / MAX: comparación y reemplazo
            String cmp = op.equals("MIN") ? "<" : ">";
            out.add(new AssignmentNode(getItemVar(0), items.get(0)));
            out.add(new AssignmentNode(getResultVar(), new LiteralNode(getItemVar(0))));
            for (int i = 1; i < n; i++) {
                out.add(new AssignmentNode(getItemVar(i), items.get(i)));
                List<TNode> then = new ArrayList<>();
                then.add(new AssignmentNode(getResultVar(), new LiteralNode(getItemVar(i))));
                out.add(new IfNode(new ConditionNode(cmp, new LiteralNode(getItemVar(i)), res), then));
            }
        }
        return out;
    }

    public String getOp() {
        return op;
    }

    public List<TNode> getItems() {
        return items;
    }

    public String getResultType() {
        return resultType;
    }

    public boolean isFloat() {
        return "Float".equals(resultType);
    }

    public String getResultVar() {
        return "agg" + id + "_res";
    }

    public String getItemVar(int i) {
        return "agg" + id + "_e" + (i + 1);
    }

    @Override
    public String toCode() {
        return getResultVar();
    }
}

package lyc.compiler.icg_tree;
import java.util.List;

public class TriangleAreaNode extends TNode {
    private List<Coordinate> triangle1;
    private List<Coordinate> triangle2;
    private double area1;
    private double area2;
    private double maxArea;

    private TNode formulaTree1;
    private TNode formulaTree2;
    private TNode comparisonTree;

    public TriangleAreaNode(List<Coordinate> triangle1, List<Coordinate> triangle2) {        
        this.triangle1 = triangle1;
        this.triangle2 = triangle2;

        this.area1 = calculateArea(triangle1);
        this.area2 = calculateArea(triangle2);
        this.maxArea = Math.max(area1, area2);

        this.formulaTree1 = buildExpressionTree(triangle1);
        this.formulaTree2 = buildExpressionTree(triangle2);

        this.comparisonTree = new ExpressionNode(">",
            formulaTree1,
            formulaTree2);

        // System.out.println("Triangle 1 - Area: " + String.format("%.2f", area1));
        // System.out.println("Triangle 2 - Area: " + String.format("%.2f", area2));
        // System.out.println("Maximum Area: " + String.format("%.2f", maxArea));
    }

    // |x1(y2-y3) + x2(y3-y1) + x3(y1-y2)|/2
    private TNode buildExpressionTree(List<Coordinate> coords) {
        if (coords.size() != 3) return null;
        
        try {
            Coordinate c1 = coords.get(0);
            Coordinate c2 = coords.get(1);
            Coordinate c3 = coords.get(2);

            TNode sub1 = new ExpressionNode("-", 
                new LiteralNode(c2.getY()), 
                new LiteralNode(c3.getY()));

            TNode sub2 = new ExpressionNode("-", 
                new LiteralNode(c3.getY()), 
                new LiteralNode(c1.getY()));

            TNode sub3 = new ExpressionNode("-", 
                new LiteralNode(c1.getY()), 
                new LiteralNode(c2.getY()));

            TNode mul1 = new ExpressionNode("*", 
                new LiteralNode(c1.getX()), 
                sub1);

            TNode mul2 = new ExpressionNode("*", 
                new LiteralNode(c2.getX()), 
                sub2);

            TNode mul3 = new ExpressionNode("*", 
                new LiteralNode(c3.getX()), 
                sub3);

            TNode add1 = new ExpressionNode("+", mul1, mul2);
            TNode addTotal = new ExpressionNode("+", add1, mul3);
            TNode division = new ExpressionNode("/", 
                addTotal, 
                new LiteralNode("2"));
            return division;
        } catch (Exception e) {
            System.err.println("Error building expression tree: " + e.getMessage());
            return null;
        }
    }

    private double calculateArea(List<Coordinate> coordinates) {
        if (coordinates.size() != 3) {
            System.err.println("Error: the triangle must have exactly 3 coordinates");
            return 0.0;
        }

        try {
            Coordinate c1 = coordinates.get(0);
            Coordinate c2 = coordinates.get(1);
            Coordinate c3 = coordinates.get(2);

            double x1 = parseValue(c1.getX());
            double y1 = parseValue(c1.getY());
            double x2 = parseValue(c2.getX());
            double y2 = parseValue(c2.getY());
            double x3 = parseValue(c3.getX());
            double y3 = parseValue(c3.getY());
            double area = Math.abs(x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2.0;
            return area;
        } catch (Exception e) {
            System.err.println("Error calculating area: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }

    private double parseValue(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public TNode getFormulaTree1() {
        return formulaTree1;
    }

    public TNode getFormulaTree2() {
        return formulaTree2;
    }

    public TNode getComparisonTree() {
        return comparisonTree;
    }

    public List<Coordinate> getTriangle1() {
        return triangle1;
    }

    public List<Coordinate> getTriangle2() {
        return triangle2;
    }

    public double getArea1() {
        return area1;
    }

    public double getArea2() {
        return area2;
    }

    public double getMaxArea() {
        return maxArea;
    }

    @Override
    public String toCode() {
        StringBuilder code = new StringBuilder();
        code.append("(TRIANGLE_AREA_MAXIMUM [");
        for (int i = 0; i < triangle1.size(); i++) {
            code.append(triangle1.get(i).toString());
            if (i < triangle1.size() - 1) code.append(";");
        }
        code.append("] [");
        for (int i = 0; i < triangle2.size(); i++) {
            code.append(triangle2.get(i).toString());
            if (i < triangle2.size() - 1) code.append(";");
        }
        code.append("])");
        return code.toString();
    }

    public static class Coordinate {
        private String x;
        private String y;

        public Coordinate(String x, String y) {
            this.x = x;
            this.y = y;
        }

        public String getX() {
            return x;
        }

        public String getY() {
            return y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }
}

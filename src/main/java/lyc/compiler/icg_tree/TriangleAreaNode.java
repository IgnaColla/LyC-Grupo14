package lyc.compiler.icg_tree;

import java.util.List;

/**
 * Representa la función especial triangleAreaMaximum.
 * Ejemplo: triangleAreaMaximum([1,2;3,4;5,6], [7,8;9,10;11,12])
 */
public class TriangleAreaNode extends TNode {
    
    private List<Coordinate> triangle1;
    private List<Coordinate> triangle2;
    
    public TriangleAreaNode(List<Coordinate> triangle1, List<Coordinate> triangle2) {
        this.triangle1 = triangle1;
        this.triangle2 = triangle2;
    }
    
    public List<Coordinate> getTriangle1() {
        return triangle1;
    }
    
    public List<Coordinate> getTriangle2() {
        return triangle2;
    }
    
    @Override
    public String toCode() {
        StringBuilder code = new StringBuilder();
        code.append("(TRIANGLE_AREA_MAXIMUM [");
        
        // Triangle 1
        for (int i = 0; i < triangle1.size(); i++) {
            code.append(triangle1.get(i).toString());
            if (i < triangle1.size() - 1) code.append(";");
        }
        code.append("] [");
        
        // Triangle 2
        for (int i = 0; i < triangle2.size(); i++) {
            code.append(triangle2.get(i).toString());
            if (i < triangle2.size() - 1) code.append(";");
        }
        code.append("])");
        
        return code.toString();
    }
    
    @Override
    protected void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + "TRIANGLE_AREA_MAXIMUM");
        
        // Triangle 1
        System.out.println(prefix + (isTail ? "    " : "│   ") + "├── Triangle 1:");
        for (int i = 0; i < triangle1.size(); i++) {
            boolean isLast = (i == triangle1.size() - 1);
            System.out.println(prefix + (isTail ? "    " : "│   ") + "│   " 
                + (isLast ? "└── " : "├── ") + triangle1.get(i));
        }
        
        // Triangle 2
        System.out.println(prefix + (isTail ? "    " : "│   ") + "└── Triangle 2:");
        for (int i = 0; i < triangle2.size(); i++) {
            boolean isLast = (i == triangle2.size() - 1);
            System.out.println(prefix + (isTail ? "    " : "│   ") + "    " 
                + (isLast ? "└── " : "├── ") + triangle2.get(i));
        }
    }
    
    /**
     * Clase interna para representar una coordenada (x, y).
     */
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

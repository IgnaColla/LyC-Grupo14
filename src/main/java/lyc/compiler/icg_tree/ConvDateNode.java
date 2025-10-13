package lyc.compiler.icg_tree;

/**
 * Representa la función especial convDate.
 * Ejemplo: convDate(25-12-2023)
 */
public class ConvDateNode extends TNode {
    
    private String day;
    private String month;
    private String year;
    
    public ConvDateNode(String day, String month, String year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }
    
    public String getDay() {
        return day;
    }
    
    public String getMonth() {
        return month;
    }
    
    public String getYear() {
        return year;
    }
    
    @Override
    public String toCode() {
        return "(CONVDATE " + day + "-" + month + "-" + year + ")";
    }
    
    @Override
    protected void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + "CONVDATE: " 
            + day + "-" + month + "-" + year);
    }
}

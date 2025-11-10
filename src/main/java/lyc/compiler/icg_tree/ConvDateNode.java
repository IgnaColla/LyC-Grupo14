package lyc.compiler.icg_tree;

public class ConvDateNode extends TNode {
    private String day;
    private String month;
    private String year;
    private int converted;
    private TNode conversionTree;

    public ConvDateNode(String day, String month, String year, int converted) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.converted = converted;

        TNode yearPart = new ExpressionNode("*",
            new LiteralNode(year),
            new LiteralNode("10000"));

        TNode monthPart = new ExpressionNode("*",
            new LiteralNode(month),
            new LiteralNode("100"));

        TNode yearPlusMonth = new ExpressionNode("+",
            yearPart,
            monthPart);

        this.conversionTree = new ExpressionNode("+",
            yearPlusMonth,
            new LiteralNode(day));
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

    public int getConverted() {
        return converted;
    }

    public String getConvertedString() {
        return String.valueOf(converted);
    }

    public TNode getConversionTree() {
        return conversionTree;
    }

    @Override
    public String toCode() {
        return "(CONVDATE " + day + "-" + month + "-" + year + ")";
    }
}

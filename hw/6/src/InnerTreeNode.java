import java.util.List;

public class InnerTreeNode implements Tree {
    private List<Pair<Range, Tree>> kids;
    private String colName;

    public InnerTreeNode(List<Pair<Range, Tree>> kids, String colName) {
        this.kids = kids;
        this.colName = colName;
    }

    @Override
    public void print(Prefix prefix) {
        for (Pair<Range, Tree> pr: kids) {
            System.out.print("\n" + prefix + colName + " = " + pr.getLeft());
            pr.getRight().print(prefix.next());
        }
    }
}

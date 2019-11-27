import java.util.List;
import java.util.stream.Collectors;

public class InnerTreeNode implements Tree {
    private List<Pair<Range, Tree>> kids;
    private String colName;
    private int size;

    public InnerTreeNode(List<Pair<Range, Tree>> kids, String colName) {
        this.kids = kids;
        this.colName = colName;
        this.size = kids.stream().map(Pair::getRight).map(Tree::size).mapToInt(i -> i).sum();
    }

    @Override
    public void print(Prefix prefix) {
        for (Pair<Range, Tree> pr: kids) {
            System.out.print("\n" + prefix + colName + " = " + pr.getLeft());
            pr.getRight().print(prefix.next());
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<Cluster> getClusters() {
        return kids.stream().flatMap(kid -> kid.getRight().getClusters().stream()).collect(Collectors.toList());
    }
}

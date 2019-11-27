import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BinaryInnerTreeNode implements Tree {
    private Tree left;
    private Tree right;

    public BinaryInnerTreeNode(Tree left, Tree right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void print(Prefix prefix) {
        System.out.println(prefix.toString() + size());
        left.print(prefix.next());
        right.print(prefix.next());
    }

    @Override
    public int size() {
        return left.size() + right.size();
    }

    @Override
    public List<Cluster> getClusters() {
        return Stream.concat(left.getClusters().stream(), right.getClusters().stream()).collect(Collectors.toList());
    }
}

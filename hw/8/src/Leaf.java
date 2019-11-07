import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Leaf implements Tree {
    private Col col;
    private Cluster cluster;

    public Leaf(Col col, List<Row> rows, List<Col> allCols) {
        this.col = col;
        this.cluster = new Cluster(rows, allCols.stream().map(Col::getSupplier).collect(Collectors.toList()));
    }

    @Override
    public void print(Prefix prefix) {
        System.out.print(" : " + col.getMiddle() + "(" + col.getCount() + ")");
    }

    @Override
    public int size() {
        return col.getCount();
    }

    @Override
    public List<Cluster> getClusters() {
        return List.of(cluster);
    }
}

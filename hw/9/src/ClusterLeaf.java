import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClusterLeaf implements Tree {
    private List<Row> rows;
    private List<Col> goals;
    private Cluster cluster;
    private int minSize;

    public ClusterLeaf(List<Row> rows, List<Col> goals, List<Col> cols, int minSize) {
        this.rows = rows;
        this.goals = goals.stream().map(col -> col.getSupplier().get()).collect(Collectors.toList());
        for (Row row : rows) {
            updateGoals(row);
        }
        this.cluster = new Cluster(rows, cols.stream().map(Col::getSupplier).collect(Collectors.toList()));
        this.minSize = minSize;
    }

    @Override
    public void print(Prefix prefix) {
        System.out.println(prefix.toString() + rows.size());
        String output = goals.stream().map(Col::getSummary).collect(Collectors.joining(", "));
        System.out.println(prefix + output);
        String indexes = rows.stream().map(Row::getPos).sorted().map(pos -> Integer.toString(pos))
                .collect(Collectors.joining(",", "(", ")"));
        System.out.println(prefix + indexes);
    }

    @Override
    public void print(Prefix prefix, PrintWriter writer) {
        writer.println(prefix.toString() + rows.size());
        String output = goals.stream().map(Col::getSummary).collect(Collectors.joining(", "));
        writer.println(prefix + output);
        String indexes = rows.stream().map(Row::getPos).sorted().map(pos -> Integer.toString(pos))
                .collect(Collectors.joining(",", "(", ")"));
        writer.println(prefix + indexes);
    }

    @Override
    public int size() {
        return rows.size();
    }

    @Override
    public List<Cluster> getClusters() {
        return List.of(cluster);
    }

    @Override
    public Tree update(Row row, double alpha) {
        int newSize = rows.size() + 1;
        updateGoals(row);
        rows.add(row);
        cluster.getRows().add(row);
        if (newSize >= minSize) {
            Table table = cluster.getTable();
            return table.cluster(table.getRows(), minSize);
        } else {
            return this;
        }
    }

    private void updateGoals(Row row) {
        for (Col col : this.goals) {
            Cell cell = row.getCells().get(col.getPos() - 1);
            cell.addTo(col);
        }
    }
}

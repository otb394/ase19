import java.util.List;
import java.util.stream.Collectors;

public class ClusterLeaf implements Tree {
    private List<Row> rows;
    private List<Col> goals;

    public ClusterLeaf(List<Row> rows, List<Col> goals) {
        this.rows = rows;
        this.goals = goals.stream().map(col -> col.getSupplier().get()).collect(Collectors.toList());
        for (Row row : rows) {
            for (Col col : this.goals) {
                Cell cell = row.getCells().get(col.getPos() - 1);
                cell.addTo(col);
            }
        }
    }

    @Override
    public void print(Prefix prefix) {
        System.out.println(prefix.toString() + rows.size());
        String output = goals.stream().map(Col::getSummary).collect(Collectors.joining(", "));
        System.out.println(prefix + output);
    }

    @Override
    public int size() {
        return rows.size();
    }
}

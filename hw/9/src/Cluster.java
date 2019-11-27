import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Cluster {
    private List<Row> rows;
    private List<Supplier<Col>> colSuppliers;

    public Cluster(List<Row> rows, List<Supplier<Col>> colSuppliers) {
        this.rows = rows;
        this.colSuppliers = colSuppliers;
    }

    public boolean contains(Row row) {
        return rows.contains(row);
    }

    public List<Row> getRows() {
        return rows;
    }

    public Row getCentroid() {
        List<Col> cols = colSuppliers.stream().map(Supplier::get).collect(Collectors.toList());
        for(Row row : rows) {
            List<Cell> rowCells = row.getCells();
            int n = rowCells.size();
            for (int i = 0; i < n; i++) {
                rowCells.get(i).addTo(cols.get(i));
            }
        }
        List<Cell> cells = cols.stream().map(Col::getMiddle).collect(Collectors.toList());
        return new Row(0, cells, false);
    }
}

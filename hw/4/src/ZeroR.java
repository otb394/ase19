import java.util.List;

public class ZeroR {
    private Table table;
    private Abcd abcd;

    public ZeroR(Table table) {
        this.table = table;
        this.abcd = new Abcd();
    }

    public void run() {
        List<Row> rows = table.getRows();
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            Row row = rows.get(rowIndex);
            if (rowIndex >= 2) {
                classify(row);
            }
            train(row);
        }
    }

    private void train(Row row) {
        table.processRow(row);
    }

    private void classify(Row row) {
        Sym classColumn = table.getClassColumn().orElseThrow(() -> new RuntimeException("No class column"));
        String prediction = classColumn.getMode();
        String actual = row.getCells().get(classColumn.getPos() - 1).toString();
        abcd.abcd1(prediction, actual);
    }

    public void report() {
        System.out.println();
        System.out.println(table.getTitle());
        abcd.report();
    }
}

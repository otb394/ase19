import java.util.List;

public abstract class AbstractBaseClassifier {
    protected Abcd abcd;
    protected Table sourceTable;
    protected int wait;

    public AbstractBaseClassifier(Table table, int wait) {
        this.abcd = new Abcd();
        this.wait = wait;
        this.sourceTable = table;
    }

    public void run() {
        List<Row> rows = sourceTable.getRows();
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            Row row = rows.get(rowIndex);
            if (rowIndex >= wait) {
                classify(row);
            }
            train(row);
        }
    }

    public void report() {
        System.out.println();
        System.out.println(sourceTable.getTitle());
        abcd.report();
    }

    protected abstract void classify(Row row);
    protected abstract void train(Row row);
}

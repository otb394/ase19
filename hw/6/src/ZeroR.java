
public class ZeroR extends AbstractBaseClassifier {
    public ZeroR(Table table, int wait) {
        super(table, wait);
    }

    @Override
    protected void train(Row row) {
        sourceTable.process(row);
    }

    @Override
    protected void classify(Row row) {
        Sym classColumn = sourceTable.getClassColumn().orElseThrow(() -> new RuntimeException("No class column"));
        String prediction = classColumn.getMode();
        String actual = row.getCells().get(classColumn.getPos() - 1).toString();
        abcd.abcd1(prediction, actual);
    }
}

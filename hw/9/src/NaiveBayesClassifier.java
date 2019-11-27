import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NaiveBayesClassifier extends AbstractBaseClassifier {
    private Map<String, Table> classTables;
    private Table overallStatsTable;
    private static final int K = 1;
    private static final int M = 2;

    public NaiveBayesClassifier(Table sourceTable, int wait) {
        super(sourceTable, wait);
        this.sourceTable = sourceTable;
        this.classTables = new HashMap<>();
        this.overallStatsTable = new Table("Overall", sourceTable.getCols());
        this.abcd = new Abcd();
        this.wait = wait;
    }

    protected void classify(Row row) {
        Optional<String> prediction = classTables.entrySet().stream()
                .map(entry -> Pair.of(entry.getKey(), like(entry.getValue(), row, entry.getKey())))
                .max(Comparator.comparingDouble(Pair::getRight))
                .map(Pair::getLeft);
        prediction.ifPresent(pred -> abcd.abcd1(pred, getClazz(row)));
    }

    private double like(Table table, Row row, String classValue) {
        double classPrior = overallStatsTable.getClassColumn()
                .orElseThrow(() -> new RuntimeException("No class column"))
                .like(classValue, K);
        double logLike = Math.log(classPrior);
        List<Cell> cells = row.getCells();
        List<Sym> xsyms = table.getSymbolXs();
        for (int i = 0; i < xsyms.size(); i++) {
            Sym col = xsyms.get(i);
            Cell cell = cells.get(col.getPos() - 1);
            double priorOfSymbol = overallStatsTable.getSymbolXs().get(i).like(cell.toString(), K);
            logLike += Math.log(cell.likedBy(col, priorOfSymbol, M));
        }

        List<Num> xnums = table.getNumXs();

        for (Num num : xnums) {
            Cell cell = cells.get(num.getPos() - 1);
            logLike += Math.log(cell.likedBy(num));
        }
        return logLike;
    }

    protected void train(Row row) {
        String actualClass = getClazz(row);
        Table classTable = Optional.ofNullable(classTables.get(actualClass))
                .orElseGet(() -> new Table(actualClass, sourceTable.getCols()));
        overallStatsTable.process(row);
        classTable.process(row);
        classTables.put(actualClass, classTable);
    }

    private String getClazz(Row row) {
        Sym classColumn = sourceTable.getClassColumn().orElseThrow(() -> new RuntimeException("No class column"));
        return row.getCells().get(classColumn.getPos() - 1).toString();
    }
}

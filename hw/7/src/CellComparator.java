import java.util.Map;

public class CellComparator {
    private static final Map<Class<? extends Cell>, Integer> rankMap =
            Map.of(NumberCell.class, 0, SymbolCell.class, 1, QuestionMark.class, 2);

    public static int compare(Cell cell, Cell t1) {
        return Integer.compare(rankMap.get(cell.getClass()), rankMap.get(t1.getClass()));
    }
}

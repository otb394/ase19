import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table extends TblObject {
    private String title;
    private List<Row> rows;
    private List<Col> cols;
    private My my;

    private static class TreeConstants {
        private static final int MIN_OBS = 4;
    }

    public class My {
        private List<Col> goals;
        private List<Col> xs;
        private List<Num> nums;
        private List<Sym> syms;
        private List<Num> xnums;
        private List<Sym> xsyms;
        private List<Col> w;
        private Integer classPos;
        private Sym classColumn;

        My() {
            goals = new ArrayList<>();
            xs = new ArrayList<>();
            nums = new ArrayList<>();
            syms = new ArrayList<>();
            xnums = new ArrayList<>();
            xsyms = new ArrayList<>();
            w = new ArrayList<>();
        }

        public void dump(Prefix prefix) {
            Optional.ofNullable(classPos).ifPresent(clas -> System.out.println(prefix + "class: " + clas));
            System.out.println(prefix + "goals");
            dumpArray(prefix.next(), goals.stream().map(Col::getPos).collect(Collectors.toList()));
            System.out.println(prefix + "nums");
            dumpArray(prefix.next(), nums.stream().map(Col::getPos).collect(Collectors.toList()));
            System.out.println(prefix + "syms");
            dumpArray(prefix.next(), syms.stream().map(Col::getPos).collect(Collectors.toList()));
            System.out.println(prefix + "w");
            dumpArray(prefix.next(), w.stream().map(col -> new String(col.getPos() + ": -1"))
                    .collect(Collectors.toList()));
            System.out.println(prefix + "xnums");
            dumpArray(prefix.next(), xnums.stream().map(Col::getPos).collect(Collectors.toList()));
            System.out.println(prefix + "xs");
            dumpArray(prefix.next(), xs.stream().map(Col::getPos).collect(Collectors.toList()));
            System.out.println(prefix + "xsyms");
            dumpArray(prefix.next(), xsyms.stream().map(Col::getPos).collect(Collectors.toList()));
        }

        private <T> void dumpArray(Prefix prefix, Collection<T> collection) {
            collection.forEach(element -> System.out.println(prefix.toString() + element));
        }
    }

    Table(String title) {
        super();
        this.title = title;
        rows = new ArrayList<>();
        cols = new ArrayList<>();
        my = new My();
    }

    Table(String title, List<Col> cols) {
        super();
        this.title = title;
        this.rows = new ArrayList<>();
        this.cols = new ArrayList<>();
        this.my = new My();
        createColumns(cols.stream().map(Col::getName).collect(Collectors.joining(",")));
    }

    public void read(String fileName) {
        try (Stream<String> lineStream = Files.lines(Path.of(fileName))) {
            List<String> lines = lineStream.collect(Collectors.toList());
            createColumns(lines.get(0));
            lines.stream().skip(1).forEachOrdered(this::addRow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Row> getRows() {
        return rows.stream().filter(row -> !row.isSkipped()).collect(Collectors.toList());
    }

    public void process() {
        rows.stream().filter(row -> !row.isSkipped()).forEachOrdered(this::process);
    }

    public void process(Row row) {
        int noOfCols = cols.size();
        List<Cell> cells = row.getCells();
        for (int columnIndex = 0; columnIndex < noOfCols; columnIndex++) {
            if (!cols.get(columnIndex).isSkipped()) {
                cells.get(columnIndex).addTo(cols.get(columnIndex));
            }
        }
    }

    public Optional<Sym> getClassColumn() {
        return Optional.ofNullable(my.classColumn);
    }

    public List<Sym> getSymbolXs() {
        return my.xsyms;
    }

    public List<Num> getNumXs() {
        return my.xnums;
    }

    public void print() {
        printColumnNames();
        rows.forEach(Row::print);
    }

    public void dump() {
        Prefix prefix = new Prefix(0);
        System.out.println(prefix + "t.cols");
        dumpCols(prefix.next());
        System.out.println(prefix + "t.my");
        my.dump(prefix.next());
    }

    public String getTitle() {
        return title;
    }

    public List<Col> getCols() {
        return cols;
    }

    private void createColumns(String columnNames) {
        String stripped = replaceWhiteSpaceAndComments(columnNames.strip());
        String[] rawCells = stripped.split(",");
        for (int columnIndex = 0; columnIndex < rawCells.length; columnIndex++) {
            String columnName = rawCells[columnIndex].strip();
            ColumnType type = ColumnType.of(columnName);
            Col col;
            switch (type) {
                case IGNORE:
                    col = new Sym(columnIndex+1, columnName);
                    col.skip();
                    break;
                case LESS:
                    Num num = new Num(columnIndex+1, columnName);
                    col = num;
                    my.nums.add(num);
                    my.goals.add(col);
                    my.w.add(col);
                    break;
                case MORE:
                    num = new Num(columnIndex+1, columnName);
                    col = num;
                    my.nums.add(num);
                    my.goals.add(col);
                    break;
                case DOLLAR:
                    num = new Num(columnIndex+1, columnName);
                    col = num;
                    my.nums.add(num);
                    my.xs.add(col);
                    my.xnums.add(num);
                    break;
                case EXCLAMATION:
                    Sym sym = new Sym(columnIndex+1, columnName);
                    col = sym;
                    my.syms.add(sym);
                    my.goals.add(col);
                    my.classPos = sym.getPos();
                    my.classColumn = sym;
                    break;
                case OTHER:
                default:
                    sym = new Sym(columnIndex+1, columnName);
                    col = sym;
                    my.syms.add(sym);
                    my.xs.add(col);
                    my.xsyms.add(sym);
            }
            cols.add(col);
        }
    }

    private void addRow(String rawRow) {
        String strippedRow = replaceWhiteSpaceAndComments(rawRow.strip());
        if (strippedRow.isBlank()) {
            addRow(new Row(rows.size() + 1, new ArrayList<>(), true));
        } else {
            String[] rawCells = strippedRow.split(",");
            List<Cell> cells = new ArrayList<>();
            for (String rawCell : rawCells) {
                Cell cell = Cell.of(rawCell);
                cells.add(cell);
            }
            addRow(new Row(rows.size() + 1, cells, false));
        }
    }

    private void addRow(Row row) {
        rows.add(row);
        if (row.getCells().size() != cols.size()) {
            row.skip();
        }
    }

    private String replaceWhiteSpaceAndComments(String s) {
        return s.replaceAll("[\\s] | #.*", " ");
    }

    private void printColumnNames() {
        System.out.print("[");
        System.out.print(cols.stream().filter(col -> !col.isSkipped()).map(Col::getName)
                .collect(Collectors.joining(", ")));
        System.out.println("]");
    }

    private void dumpRows(Prefix prefix) {
        List<Row> relevantRows = rows.stream().filter(row -> !row.isSkipped()).collect(Collectors.toList());
        int size = relevantRows.size();
        for (int rowIndex = 0; rowIndex < size; rowIndex++) {
            System.out.println(prefix.toString() + (rowIndex+1));
            relevantRows.get(rowIndex).dump(prefix.next());
        }
    }

    private void dumpCols(Prefix prefix) {
        List<Col> relevantCols = cols.stream().filter(col -> !col.isSkipped()).collect(Collectors.toList());
        for (int colIndex = 0; colIndex < relevantCols.size(); colIndex++) {
            System.out.println(prefix.toString() + (colIndex + 1));
            relevantCols.get(colIndex).dump(prefix.next());
        }
    }

    public Tree decisionTree() {
        return tree(rows.stream().filter(row -> !row.isSkipped()).collect(Collectors.toList()),
                row -> row.getCells().get(my.classPos - 1), () -> new Sym(my.classPos, my.classColumn.getName()), 0);
    }

    public Tree regressionTree() {
        Col goalCol = cols.get(cols.size() - 1); // Need to handle !$mpg properly
        return tree(rows.stream().filter(row -> !row.isSkipped()).collect(Collectors.toList()),
                row -> row.getCells().get(cols.size() - 1), () -> new Num(goalCol.getPos(), goalCol.getName()), 0);
    }

    private Tree tree(List<Row> rows, Function<Row, Cell> y, Supplier<Col> ySupplier, int lvl) {
        if (rows.size() >= (TreeConstants.MIN_OBS * 2)) {
            double lo = -1.0;
            List<Pair<Col, Col>> cut = null;
            Num col = null;
            for (Num num: my.xnums) {
                if (num.getPos() == cols.size()) { // Hack
                    continue;
                }
                List<Pair<NumberCell, Cell>> data = rows.stream()
                        .map(row -> getNumCell(row, num.getPos() - 1)
                                .map(numCell -> Pair.of(numCell, y.apply(row))))
                        .flatMap(Optional::stream)
                        .collect(Collectors.toList());
                Div div = new Div(data, () -> new Num(num.getPos(), num.getName()), ySupplier);
                List<Pair<Col, Col>> cut1 = div.getRanges();
                if (cut1.size() > 1) {
                    double lo1 = div.getGain();
                    if (lo == -1.0 || lo1 < lo) {
                        lo = lo1;
                        cut = cut1;
                        col = num;
                    }
                }
            }
            if (cut != null) {
                String colName = col.getName();
                final int index = col.getPos() - 1;
                List<Row> relevantRows = rows.stream().filter(row -> getNumCell(row, index).isPresent())
                        .collect(Collectors.toList());
                List<Pair<Range, Tree>> kids =
                        split(relevantRows,
                                row -> row.getCells().get(index),
                                cut.stream().map(Pair::getLeft).collect(Collectors.toList()))
                                .stream()
                                .map(pr -> Pair.of(pr.getLeft(), tree(pr.getRight(), y, ySupplier, lvl + 1)))
                                .collect(Collectors.toList());
                return new InnerTreeNode(kids, colName);
            }
        }
        return new Leaf(getCol(rows, y, ySupplier));
    }

    private List<Pair<Range, List<Row>>> split(List<Row> rows, Function<Row, Cell> x, List<Col> xRanges) {
        List<Range> ranges = xRanges.stream().map(Col::toRange).sorted().collect(Collectors.toList());
        rows.sort(Comparator.comparing(x));
        int i = 0;
        List<Pair<Range, List<Row>>> result = new ArrayList<>();
        int n = rows.size();
        int m = xRanges.size();
        for (int j = 0; j < m; j++) {
            List<Row> rangeRows = new ArrayList<>();
            Range range = ranges.get(j);
            while(i<n && range.contains(x.apply(rows.get(i)))) {
                rangeRows.add(rows.get(i));
                i++;
            }
            result.add(Pair.of(range, rangeRows));
        }
        return result;
    }

    private <T> Col getCol(List<T> list, Function<? super T, ? extends Cell> function, Supplier<Col> supplier) {
        Col col = supplier.get();
        for (T element: list) {
            Cell cell = function.apply(element);
            cell.addTo(col);
        }
        return col;
    }

    private Optional<NumberCell> getNumCell(Row row, int index) {
        Cell cell = row.getCells().get(index);
        if (cell instanceof QuestionMark || cell instanceof SymbolCell) return Optional.empty();
        else return Optional.ofNullable((NumberCell) cell);
    }
}

import org.w3c.dom.ls.LSOutput;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table extends TblObject {
    private String title;
    private List<Row> rows;
    private List<Col> cols;
    private My my;

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

    public void read(String fileName) {
        try (Stream<String> lineStream = Files.lines(Path.of(fileName))) {
            List<String> lines = lineStream.collect(Collectors.toList());
            createColumns(lines.get(0));
            lines.stream().skip(1).forEachOrdered(this::addRow);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public List<Row> getRows() {
        return rows.stream().filter(row -> !row.isSkipped()).collect(Collectors.toList());
    }

    public void process() {
        rows.stream().filter(row -> !row.isSkipped()).forEachOrdered(this::processRow);
    }

    public void processRow(Row row) {
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

    private String replaceWhiteSpaceAndComments(String s) {
        return s.replaceAll("[\\s] | #.*", " ");
    }

    public void print() {
        printColumnNames();
        rows.forEach(Row::print);
    }

    private void printColumnNames() {
        System.out.print("[");
        System.out.print(cols.stream().filter(col -> !col.isSkipped()).map(Col::getName)
                .collect(Collectors.joining(", ")));
        System.out.println("]");
    }

    public void dump() {
        Prefix prefix = new Prefix(0);
        System.out.println(prefix + "t.cols");
        dumpCols(prefix.next());
        System.out.println(prefix + "t.my");
        my.dump(prefix.next());
    }

    private void dumpRows(Prefix prefix) {
        List<Row> relevantRows = rows.stream().filter(row -> !row.isSkipped()).collect(Collectors.toList());
        int size = relevantRows.size();
        for (int rowIndex = 0; rowIndex < size; rowIndex++) {
            System.out.println(prefix.toString() + (rowIndex+1));
            relevantRows.get(rowIndex).dump(prefix.next());
        }
    }

    public String getTitle() {
        return title;
    }

    private void dumpCols(Prefix prefix) {
        List<Col> relevantCols = cols.stream().filter(col -> !col.isSkipped()).collect(Collectors.toList());
        for (int colIndex = 0; colIndex < relevantCols.size(); colIndex++) {
            System.out.println(prefix.toString() + (colIndex + 1));
            relevantCols.get(colIndex).dump(prefix.next());
        }
    }
}

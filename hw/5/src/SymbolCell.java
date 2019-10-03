import java.util.Objects;

public class SymbolCell implements Cell {
    private String value;

    public SymbolCell(String value) {
        this.value = value;
    }

    @Override
    public void print() {
        System.out.print(value);
    }

    @Override
    public double likedBy(Sym sym, double prior, double pseudocountM) {
        return sym.like(value, prior, pseudocountM);
    }

    @Override
    public void addTo(Col col) {
        col.add(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int compareTo(Cell cell) {
        if (cell instanceof SymbolCell) {
            return value.compareTo(cell.toString());
        } else {
            return CellComparator.compare(this, cell);
        }
    }

    @Override
    public void removeFrom(Col col) {
        col.remove(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolCell that = (SymbolCell) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

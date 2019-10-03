import java.util.Objects;

public class NumberCell implements Cell {
    private double value;

    NumberCell(double value) {
        this.value = value;
    }

    @Override
    public double likedBy(Num num) {
        return num.like(value);
    }

    @Override
    public void print() {
        System.out.print(value);
    }

    @Override
    public void addTo(Col col) {
        col.add(value);
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    public double getValue() {
        return value;
    }

    @Override
    public int compareTo(Cell cell) {
        if (cell instanceof NumberCell) {
            NumberCell other = (NumberCell) cell;
            return Double.compare(value, other.getValue());
        } else {
            return CellComparator.compare(this, cell);
        }
    }

    public static NumberCell of(String string) {
        String stripped = string.strip();
        try {
            return new NumberCell(Double.parseDouble(stripped));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
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
        NumberCell that = (NumberCell) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

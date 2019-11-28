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
        System.out.printf("%.2f", value);
    }

    @Override
    public void addTo(Col col) {
        col.add(value);
    }

    @Override
    public String toString() {
        return String.format("%.2f", value);
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

    @Override
    public boolean within(NumericalRange numericalRange) {
        boolean lowBound = numericalRange.getLow().map(lo -> value >= lo).orElse(true);
        boolean highBound = numericalRange.getHigh().map(hi -> value <= hi).orElse(true);
        return lowBound && highBound;
    }

    @Override
    public double diff(Cell cell) {
        if (cell instanceof NumberCell) {
            NumberCell other = (NumberCell) cell;
            return value - other.getValue();
        } else {
            return Double.MAX_VALUE;
        }
    }

    @Override
    public boolean within(SymbolicRange symbolicRange) {
        return symbolicRange.getSymbols().contains(Double.toString(value));
    }
}

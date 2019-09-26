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
}

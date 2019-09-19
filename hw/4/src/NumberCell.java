public class NumberCell implements Cell {
    private int value;

    NumberCell(int value) {
        this.value = value;
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
        return Integer.toString(value);
    }
}

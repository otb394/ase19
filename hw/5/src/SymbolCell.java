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
}

import java.util.function.Supplier;

public abstract class Col extends TblObject {
    protected int pos;
    protected String name;
    protected boolean skipped;
    protected int weight;

    public Col(int pos, String name, int weight) {
        super();
        this.pos = pos;
        this.name = name;
        this.skipped = false;
        this.weight = weight;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPos() {
        return this.pos;
    }

    public void skip() {
        this.skipped = true;
    }

    public String getName() {
        return this.name;
    }

    public boolean isSkipped() {
        return skipped;
    }

    public abstract void dump(Prefix prefix);

    public void add(double v) {
        add(Double.toString(v));
    }

    public void add(String v) {}

    public void remove(double v) {}

    public void remove(String v) {}

    public abstract double getVariety();

    public abstract int getCount();

    public abstract String getSummary();

    public abstract Cell getMiddle();

    public double diffMiddle(Col other) {
        return getMiddle().diff(other.getMiddle());
    }

    public double dis(Cell a, Cell b) {
        return a.diff(b);
    }

    public abstract Supplier<Col> getSupplier();

    public double xpect(Col col) {
        int n = getCount() + col.getCount();
        return (getCount() * getVariety() + col.getCount() * col.getVariety()) / ((double) n);
    }

    public abstract Range toRange();

    public int getWeight() {
        return weight;
    }

    public double norm(Cell cell) {
        return 1.0;
    }

    public boolean same(Col col) {
        return false;
    }
}

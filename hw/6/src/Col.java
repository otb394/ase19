public abstract class Col extends TblObject {
    protected int pos;
    protected String name;
    protected boolean skipped;

    public Col(int pos, String name) {
        super();
        this.pos = pos;
        this.name = name;
        this.skipped = false;
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

    public void add(double v) {}

    public void add(String v) {}

    public void remove(double v) {}

    public void remove(String v) {}

    public abstract double getVariety();

    public abstract int getCount();

    public abstract String getSummary();

    public double xpect(Col col) {
        int n = getCount() + col.getCount();
        return (getCount() * getVariety() + col.getCount() * col.getVariety()) / ((double) n);
    }
}

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
}

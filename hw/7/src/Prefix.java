public class Prefix {
    private int level;

    public Prefix(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "|  ".repeat(level);
    }

    public Prefix next() {
        return new Prefix(level + 1);
    }

    public Prefix previous() {
        return new Prefix(level - 1);
    }
}

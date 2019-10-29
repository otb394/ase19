public class Leaf implements Tree {
    private Col col;

    public Leaf(Col col) {
        this.col = col;
    }

    @Override
    public void print(Prefix prefix) {
        System.out.print(" : " + col.getMiddle() + "(" + col.getCount() + ")");
    }
}

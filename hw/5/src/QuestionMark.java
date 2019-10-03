public class QuestionMark implements Cell {

    @Override
    public void print() {
        System.out.print("?");
    }

    @Override
    public boolean isSkipped() {
        return true;
    }

    @Override
    public double likedBy(Num num) {
        return 1.0;
    }

    @Override
    public double likedBy(Sym sym, double prior, double pseudocountM) {
        return 1.0;
    }

    @Override
    public void addTo(Col col) { }

    @Override
    public String toString() {
        return "?";
    }
}

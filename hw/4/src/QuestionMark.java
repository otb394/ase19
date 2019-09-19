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
    public void addTo(Col col) { }

    @Override
    public String toString() {
        return "?";
    }
}

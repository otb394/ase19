import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DecisionTreeLearner {
    private static final double COHEN = 0.3;
    private static final double TRIVIAL = 1.025;
    private List<Pair<NumberCell, Cell>> data;
    private Supplier<Num> xSupplier;
    private Supplier<Col> ySupplier;
    private int steps;
    private NumberCell start;
    private NumberCell stop;
    private double epsilon;
    private double gain;
    private List<Pair<Col, Col>> ranges;

    public DecisionTreeLearner(List<Pair<NumberCell, Cell>> data,
                               Supplier<Num> xSupplier,
                               Supplier<Col> ySupplier) {
        this.data = data.stream().map(Pair.ComparablePair::of).sorted().collect(Collectors.toList());
        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;
        int count = data.size();
        this.steps = (int) Math.floor(Math.sqrt(count));
        this.start = this.data.get(0).getLeft();
        this.stop = this.data.get(count-1).getLeft();
        Col bf4 = getCol(this.data, Pair::getLeft, xSupplier::get);
        this.epsilon = bf4.getVariety() * COHEN;
        this.gain = 0;
        this.ranges = new ArrayList<>();
        divide(0, count, bf4, getCol(this.data, Pair::getRight, ySupplier));
    }

    public List<Pair<Col, Col>> getRanges() {
        return ranges;
    }

    private void divide(int low, int high, Col xCol, Col yCol) {
        Col l = ySupplier.get();
        Col r = ySupplier.get();
        Num lx = xSupplier.get();
        Num rx = xSupplier.get();
        for (Pair<NumberCell, Cell> pr: this.data.subList(low, high)) {
            pr.getLeft().addTo(rx);
            pr.getRight().addTo(r);
        }
        double best = yCol.getVariety();
        int cut = -1;
        for (int i = low; i < high-1; i++) {
            Pair<NumberCell, Cell> pr = this.data.get(i);
            pr.getRight().addTo(l);
            pr.getRight().removeFrom(r);
            pr.getLeft().addTo(lx);
            pr.getLeft().removeFrom(rx);
            if (l.getCount() >= this.steps && r.getCount() >= this.steps) {
                Pair<NumberCell, Cell> afterPr = this.data.get(i+1);
                double now = pr.getLeft().getValue();
                double after = afterPr.getLeft().getValue();
                if (now == after) {
                    continue;
                }
                if (Math.abs(rx.getMean() - lx.getMean()) >= epsilon) {
                    if ((afterPr.getLeft().getValue() - this.start.getValue()) >= epsilon) {
                        if ((this.stop.getValue() - now) >= epsilon) {
                            double xpect = l.xpect(r);
                            if (xpect * TRIVIAL < best) {
                                best = xpect;
                                cut = i+1;
                            }
                        }
                    }
                }
            }
        }

        if (cut != -1) {
            divide(low, cut, getCol(this.data.subList(low, cut), Pair::getLeft, xSupplier::get),
                    getCol(this.data.subList(low, cut), Pair::getRight, ySupplier));
            divide(cut, high, getCol(this.data.subList(cut, high), Pair::getLeft, xSupplier::get),
                    getCol(this.data.subList(cut, high), Pair::getRight, ySupplier));
        } else {
            gain += yCol.getCount() * yCol.getVariety();
            ranges.add(Pair.of(xCol, yCol));
        }
    }

    private <T> Col getCol(List<T> list, Function<? super T, ? extends Cell> function, Supplier<Col> supplier) {
        Col col = supplier.get();
        for (T element: list) {
            Cell cell = function.apply(element);
            cell.addTo(col);
        }
        return col;
    }
}

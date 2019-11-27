import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Div {
    private static final double COHEN = 0.3;
    private static final double TRIVIAL = 1.025;
    private List<Pair<Cell, Cell>> data;
    private Supplier<Col> xSupplier;
    private Supplier<Col> ySupplier;
    private int steps;
    private Cell start;
    private Cell stop;
    private double epsilon;
    private double gain;
    private List<Pair<Col, Col>> ranges;

    public Div(List<Pair<Cell, Cell>> data,
               Supplier<Col> xSupplier,
               Supplier<Col> ySupplier, int steps) {
        this.data = data.stream().map(Pair.ComparablePair::of).sorted().collect(Collectors.toList());
        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;
        int count = data.size();
        this.steps = steps;
        this.start = this.data.get(0).getLeft();
        this.stop = this.data.get(count-1).getLeft();
        Col bf4 = getCol(this.data, Pair::getLeft, xSupplier);
        this.epsilon = bf4.getVariety() * COHEN;
        this.gain = 0;
        this.ranges = new ArrayList<>();
        if (xSupplier.get() instanceof Sym) {
            divideSym(xSupplier, ySupplier);
        } else {
            divide(0, count, bf4, getCol(this.data, Pair::getRight, ySupplier));
        }
    }

    private void divideSym(Supplier<Col> xSupplier, Supplier<Col> ySupplier) {
        int i = 0;
        int size = data.size();
        while (i < size) {
            Col symbol = xSupplier.get();
            Cell symbolCell = data.get(i).getLeft();
            symbolCell.addTo(symbol);
            Col yCol = ySupplier.get();
            while(i < size && data.get(i).getLeft().equals(symbolCell)) {
                data.get(i).getRight().addTo(yCol);
                i++;
            }
            gain += yCol.getCount() * yCol.getVariety();
            ranges.add(Pair.of(symbol, yCol));
        }
    }

    public List<Pair<Col, Col>> getRanges() {
        return ranges;
    }

    public double getGain() {
        return gain / data.size();
    }

    private void divide(int low, int high, Col xCol, Col yCol) {
        Col l = ySupplier.get();
        Col r = ySupplier.get();
        Col lx = xSupplier.get();
        Col rx = xSupplier.get();
        for (Pair<Cell, Cell> pr: this.data.subList(low, high)) {
            pr.getLeft().addTo(rx);
            pr.getRight().addTo(r);
        }
        double best = yCol.getVariety();
        int cut = -1;
        for (int i = low; i < high-1; i++) {
            Pair<Cell, Cell> pr = this.data.get(i);
            pr.getRight().addTo(l);
            pr.getRight().removeFrom(r);
            pr.getLeft().addTo(lx);
            pr.getLeft().removeFrom(rx);
            if (l.getCount() >= this.steps && r.getCount() >= this.steps) {
                Pair<Cell, Cell> afterPr = this.data.get(i+1);
                Cell nowCell = pr.getLeft();
                Cell afterCell = afterPr.getLeft();
                if (nowCell.equals(afterCell)) {
                    continue;
                }

                if (Math.abs(rx.diffMiddle(lx)) >= epsilon) {
                    if ((afterCell.diff(this.start)) >= epsilon) {
                        if ((this.stop.diff(nowCell)) >= epsilon) {
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
            divide(low, cut, getCol(this.data.subList(low, cut), Pair::getLeft, xSupplier),
                    getCol(this.data.subList(low, cut), Pair::getRight, ySupplier));
            divide(cut, high, getCol(this.data.subList(cut, high), Pair::getLeft, xSupplier),
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

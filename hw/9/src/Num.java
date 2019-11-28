import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Num extends Col {
    private int count;
    private double mean;
    private double m2;
    private double hi;
    private double lo;
    private static final Double EPSILON = 1e-64;
    private static final Double EPSILON2 = 1e-4;

    Num(int pos, String name) {
        super(pos, name, 1);
        this.count = 0;
        this.mean = 0.0;
        this.m2 = 0.0;
        this.hi = Double.MIN_VALUE;
        this.lo = Double.MAX_VALUE;
    }

    Num(int pos, String name, int weight) {
        super(pos, name, weight);
        this.count = 0;
        this.mean = 0.0;
        this.m2 = 0.0;
        this.hi = Double.MIN_VALUE;
        this.lo = Double.MAX_VALUE;
    }

    @Override
    public void add(double v) {
        count++;
        double delta = v-mean;
        mean += delta / count;
        m2+=delta*(v-mean);
        hi = Math.max(hi, v);
        lo = Math.min(lo, v);
    }

    @Override
    public void remove(double v) {
        if (count == 0) return;
        double delta = v-mean;
        double diff = mean - v/count;
        mean = diff * ((double)count/(((double)count)-1));
        count--;
        m2-=delta*(v-mean);
    }

    public double like(double v) {
        double sd = getSD();
        double mean = getMean();
        double variance = sd*sd;
        double denom = Math.sqrt(Math.PI * 2.0 * variance);
        double num = Math.exp(-((v - mean)*(v - mean))/(2.0 * variance + EPSILON2));
        return num/(denom + EPSILON);
    }

    @Override
    public double getVariety() {
        return getSD();
    }

    @Override
    public void dump(Prefix prefix) {
        System.out.println(prefix + "add: Num1");
        System.out.println(prefix + "col: " + pos);
        System.out.println(prefix + "hi: " + hi);
        System.out.println(prefix + "lo: " + lo);
        System.out.println(prefix + "m2: " + m2);
        System.out.println(prefix + "mu: " + mean);
        System.out.println(prefix + "n: " + count);
        System.out.println(prefix + "oid: " + oid);
        System.out.println(prefix + "sd: " + getSD());
        System.out.println(prefix + "txt: " + name);
    }

    @Override
    public int getCount() {
        return count;
    }

    private double getSD() {
        if (count < 2) return 0.0;
        if (m2 < 0.0) return 0.0;
        return Math.sqrt(m2/(count-1));
    }

    public double getMean() {
        return mean;
    }

    @Override
    public String getSummary() {
        return String.format("%s = %.1f (%.1f)", name, getMean(), getSD());
//        return String.format("%s.lo %.5f %s.hi %.5f", name, lo, name, hi);
    }

    @Override
    public Cell getMiddle() {
        return new NumberCell(getMean());
    }

    @Override
    public Range toRange() {
        return new NumericalRange(lo, hi);
    }

    @Override
    public Supplier<Col> getSupplier() {
        return () -> new Num(pos, name, weight);
    }

    @Override
    public double dis(Cell a, Cell b) {
        Function<Cell, Double> normalize = cell -> ((hi != lo) ? (cell.diff(new NumberCell(lo)) / (hi - lo)) : (0.0));
        double aNorm, bNorm;
        if (a.isSkipped()) {
            if (b.isSkipped()) return 1.0;
            bNorm = normalize.apply(b);
            aNorm = (bNorm > 0.5) ? 0 : 1;
        } else {
            aNorm = normalize.apply(a);
            if (b.isSkipped()) {
                bNorm = (aNorm > 0.5) ? 0 : 1;
            } else {
                bNorm = normalize.apply(b);
            }
        }
        return Math.abs(aNorm - bNorm);
    }

    @Override
    public double norm(Cell cell) {
        return ((hi != lo) ? (cell.diff(new NumberCell(lo)) / (hi - lo)) : (0.0));
    }

    @Override
    public boolean same(Col col) {
        if (col instanceof Num) {
            Num otherNum = (Num) col;
//            System.out.println("My mu = " + getMean());
//            System.out.println("My sd = " + getSD());
//            System.out.println("My count = " + getCount());
//            System.out.println("Other mu = " + otherNum.getMean());
//            System.out.println("Other sd = " + otherNum.getSD());
//            System.out.println("Other count = " + otherNum.getCount());
            return tTestSame(otherNum, 0.95) || hedges(otherNum, 0.38);
        } else {
            return false;
        }
    }

    private boolean tTestSame(Num num, double conf) {
        double nom = Math.abs(getMean() - num.getMean());
        double s1 = getSD();
        double s2 = num.getSD();
        double denom = ((s1 + s2) != 0.0) ? Math.sqrt(s1/getCount() + s2/num.getCount()) : 1.0;
        double df = Math.min(getCount() - 1, num.getCount() - 1);
        return criticalValue(df, conf) >= nom/denom;
    }

    private double criticalValue(double df, double conf) {
        double[] xs = new double[]{1,2,5,10,15,20,25,30,60,100};
        Map<Double, List<Double>> ys = Map.of(0.9, List.of(3.078, 1.886,1.476,1.372,1.341, 1.325, 1.316, 1.31, 1.296, 1.29),
                0.95, List.of(6.314, 2.92,  2.015, 1.812, 1.753, 1.725, 1.708, 1.697, 1.671, 1.66),
                0.99, List.of(31.821, 6.965, 3.365, 2.764, 2.602, 2.528, 2.485, 2.457, 2.39,  2.364));
        return interpolate(df, xs, ys.get(conf).stream().mapToDouble(i -> i).toArray());
    }

    private double interpolate(double x, double[] xs, double[] ys) {
        if (x <= xs[0]) return ys[0];
        if (x >= xs[xs.length-1]) return ys[ys.length-1];
        double x0 = xs[0];
        double y0 = ys[0];
        List<Pair<Double, Double>> zipped = IntStream.range(0, xs.length)
                .mapToObj(index -> Pair.of(xs[index], ys[index])).collect(Collectors.toList());
        Pair<Double, Double> lastPr = null;
        for(Pair<Double, Double> pr : zipped) {
            lastPr = pr;
            if ((x < x0) || (x > xs[xs.length-1]) || ((x0 <= x) && (x < pr.getLeft()))) {
                break;
            }
            x0 = pr.getLeft();
            y0 = pr.getRight();
        }
        double gap = (x - x0) / (lastPr.getLeft() - x0);
        return y0 + gap*(lastPr.getRight() - y0);
    }

    private boolean hedges(Num num, double small) {
        double thisSD = getSD();
        double otherSD = num.getSD();
        double numerator = (getCount()-1) * thisSD*thisSD + (num.getCount()-1)*otherSD*otherSD;
        double denom = (getCount()-1) + (num.getCount()-1);
        double sp = Math.sqrt(numerator / denom);
        double delta = Math.abs(getMean() - num.getMean()) / sp;
        double c = 1.0-(3.0/(4.0*(denom)-1.0));
        return delta*c < small;
    }
}

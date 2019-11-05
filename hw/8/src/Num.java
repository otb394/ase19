import java.util.function.Function;
import java.util.function.Supplier;

public class Num extends Col {
    private int count;
    private double mean;
    private double m2;
    private double hi;
    private double lo;
    private static final Double EPSILON = 1e-64;
    private static final Double EPSILON2 = 1e-4;

    Num(int pos, String name) {
        super(pos, name);
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
    public String getMiddle() {
        return Double.toString(getMean());
    }

    @Override
    public Range toRange() {
        return new NumericalRange(lo, hi);
    }

    @Override
    public double diffMiddle(Col other) {
        if (other instanceof Num) {
            Num otherNum = (Num) other;
            return getMean() - otherNum.getMean();
        } else {
            return Double.MAX_VALUE;
        }
    }

    @Override
    public Supplier<Col> getSupplier() {
        return () -> new Num(pos, name);
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
}

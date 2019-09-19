public class Num extends Col {
    private int count;
    private double mean;
    private double m2;
    private int hi;
    private int lo;

    Num(int pos, String name) {
        super(pos, name);
        this.count = 0;
        this.mean = 0.0;
        this.m2 = 0.0;
        this.hi = Integer.MIN_VALUE;
        this.lo = Integer.MAX_VALUE;
    }

    @Override
    public void add(int v) {
        count++;
        double delta = v-mean;
        mean += delta / count;
        m2+=delta*(v-mean);
        hi = Math.max(hi, v);
        lo = Math.min(lo, v);
    }

    @Override
    public void add(String v) {
        // Do nothing
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

    private double getSD() {
        if (count < 2) return 0.0;
        if (m2 < 0.0) return 0.0;
        return Math.sqrt(m2/(count-1));
    }

    private double getMean() {
        return mean;
    }
}

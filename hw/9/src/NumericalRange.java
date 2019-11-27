import java.util.Optional;

/**
 * Represents a numeric range
 */
public class NumericalRange implements Range {
    private Double low;
    private Double high;

    public NumericalRange(Double low, Double high) {
        this.low = low;
        this.high = high;
    }

    @Override
    public boolean contains(Cell cell) {
        return cell.within(this);
    }

    public Optional<Double> getLow() {
        return Optional.ofNullable(low);
    }

    public Optional<Double> getHigh() {
        return Optional.ofNullable(high);
    }

    @Override
    public String toString() {
        String sl = Optional.ofNullable(low).map(low -> Double.toString(low)).orElse("-inf");
        String sh = Optional.ofNullable(high).map(hi -> Double.toString(hi)).orElse("inf");
        return sl + " .. " + sh;
    }

    @Override
    public int compareTo(Range range) {
        if (range instanceof NumericalRange) {
            NumericalRange other = (NumericalRange) range;
            Pair.ComparablePair<Double, Double> lPr =
                    Pair.ComparablePair.of(Optional.ofNullable(low).orElse(Double.MIN_VALUE),
                            Optional.ofNullable(high).orElse(Double.MAX_VALUE));
            Pair.ComparablePair<Double, Double> rPr =
                    Pair.ComparablePair.of(other.getLow().orElse(Double.MIN_VALUE),
                            other.getHigh().orElse(Double.MAX_VALUE));
            return lPr.compareTo(rPr);
        } else {
            return 1;
        }
    }
}

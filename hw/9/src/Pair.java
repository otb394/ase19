import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public class Pair<L, R> {
    private L left;
    private R right;

    private Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }

    @Override
    public String toString() {
        return "(" + left + "," + right + ")";
    }

    public static class ComparablePair<A extends Comparable<? super A>, B extends Comparable<? super B>>
            extends Pair<A,B> implements Comparable<Pair<A,B>> {

        private ComparablePair(A left, B right) {
            super(left, right);
        }

        public static <P extends Comparable<? super P>,Q extends Comparable<? super Q>> ComparablePair<P,Q> of(P p,
                                                                                                               Q q) {
            return new ComparablePair<>(p,q);
        }

        public static <P extends Comparable<? super P>,Q extends Comparable<? super Q>> ComparablePair<P,Q> of(
                Pair<P,Q> pr) {
            return new ComparablePair<>(pr.getLeft(),pr.getRight());
        }

        @Override
        public int compareTo(Pair<A, B> abPair) {
            Function<Pair<A,B>, A> fLeft = Pair::getLeft;
            Function<Pair<A,B>, B> fRight = Pair::getRight;
            return Comparator.comparing(fLeft).thenComparing(fRight).compare(this, abPair);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return left.equals(pair.left) &&
                right.equals(pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}

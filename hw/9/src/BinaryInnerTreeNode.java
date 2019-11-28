import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BinaryInnerTreeNode implements Tree {
    private Tree left;
    private Tree right;
    private Row leftPivot;
    private Row rightPivot;
    private BiFunction<Row, Row, Double> distanceCalculator;
    private double c;
    private double splitDistance;

    public BinaryInnerTreeNode(Tree left, Tree right, Row leftPivot, Row rightPivot, BiFunction<Row, Row, Double> dis,
                               double splitDistance) {
        this.left = left;
        this.right = right;
        this.leftPivot = leftPivot;
        this.rightPivot = rightPivot;
        this.distanceCalculator = dis;
        this.c = distanceCalculator.apply(leftPivot, rightPivot);
        this.splitDistance = splitDistance;
    }

    @Override
    public void print(Prefix prefix) {
        System.out.println(prefix.toString() + size());
        left.print(prefix.next());
        right.print(prefix.next());
    }

    @Override
    public void print(Prefix prefix, PrintWriter writer) {
        writer.println(prefix.toString() + size());
        left.print(prefix.next(), writer);
        right.print(prefix.next(), writer);
    }

    @Override
    public int size() {
        return left.size() + right.size();
    }

    @Override
    public List<Cluster> getClusters() {
        return Stream.concat(left.getClusters().stream(), right.getClusters().stream()).collect(Collectors.toList());
    }

    @Override
    public Tree update(Row row, double alpha) {
        if (isAnomaly(row, alpha)) {
            double a = distanceCalculator.apply(leftPivot, row);
            double b = distanceCalculator.apply(rightPivot, row);
            if (a < b) {
                left = left.update(row, alpha);
            } else {
                right = right.update(row, alpha);
            }
        }
        return this;
    }

    private boolean isAnomaly(Row row, double alpha) {
        double a = distanceCalculator.apply(leftPivot, row);
        double b = distanceCalculator.apply(rightPivot, row);
        double x = (a*a + c*c - b*b) / (2.0*c);
        if (splitDistance < (c/2.0)) {
            double far = splitDistance*alpha;
            return x < far;
        } else {
            double far = splitDistance + (c-splitDistance)*alpha;
            return x > far;
        }
    }
}

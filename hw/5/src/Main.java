import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final int MAX = 1001;
    private static final int COUNT = 100;
    private static final double THRESHOLD = 0.001;

    public static void main(String[] args) {
        run("num.txt", () -> new Num(0, "Num"), () -> new Num(0, "Num"));
        System.out.println();
        run("symbol.txt", () -> new Num(0, "Num"), () -> new Sym(0, "Sym"));
    }

    private static void run(String fileName, Supplier<Num> xSupplier, Supplier<Col> ySupplier) {
        try(Stream<String> data = Files.lines(Path.of("5/input/" + fileName))) {
            List<Pair<NumberCell, Cell>> input = data.map(Main::getDataPoint).collect(Collectors.toList());
            DecisionTreeLearner learner = new DecisionTreeLearner(input, xSupplier, ySupplier);
            List<Pair<Col, Col>> ranges = learner.getRanges();
            for (int i = 0; i < ranges.size(); i++) {
                print(i+1, ranges.get(i).getLeft(), ranges.get(i).getRight());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Pair<NumberCell, Cell> getDataPoint(String line) {
        String[] elements = line.split(",");
        return Pair.of(NumberCell.of(elements[0].strip()), Cell.of(elements[1].strip()));
    }

    private static void print(int index, Col x, Col y) {
        x.setName("x");
        y.setName("y");
        System.out.printf("%d x.n%5d | %s | %s\n", index, x.getCount(), x.getSummary(), y.getSummary());
    }
}

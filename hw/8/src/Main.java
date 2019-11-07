import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        TblObject.reset();
        Table table = new Table("auto");
        table.read("8/input/auto.csv");
        table.process();
        List<Row> rows = table.getRows();
        List<Col> goals = table.getGoals();
        List<Pair<Row, Integer>> countedRows = rows.stream().map(row -> Pair.of(row, dominationCount(row, rows, goals, 100)))
                .collect(Collectors.toList());
        Comparator<Pair<Row, Integer>> comparator = Comparator.comparing(Pair::getRight);
        countedRows.sort(comparator.reversed());
        List<Col> cols = table.getCols();
        System.out.println(cols.stream().map(Col::getName).collect(Collectors.joining("|", "|", "|")));
        System.out.println(getHeaderSeparator(cols.size()));
        for (Pair<Row, Integer> countedRow : countedRows) {
            countedRow.getLeft().printGithub();
        }

        TblObject.reset();
        Table table1 = new Table("auto");
        table1.read("8/input/auto.csv");
        table1.process();
        Tree clusterTree = table1.clusteringTree();
        List<Cluster> clusters = clusterTree.getClusters();
        List<Row> centroids = clusters.stream().map(Cluster::getCentroid).collect(Collectors.toList());
        List<Col> goals1 = table1.getGoals();
        Map<Cluster, Integer> dominatingCounts = clusters.stream()
                .collect(Collectors.toMap(clu -> clu, clu -> dominationCount(clu.getCentroid(), centroids, goals1,
                        centroids.size())));
//        double normalizingFactor = dominatingCounts.values().stream().mapToLong(i -> i).sum();
        double normalizingFactor = clusters.size();
        Function<Cluster, Double> normalizedDominationCount = cluster
                -> dominatingCounts.get(cluster) / normalizingFactor;
        for (Cluster cluster : clusters) {
            Optional<Cluster> mostEnviedCluster = getMostEnviedCluster(cluster, clusters, normalizedDominationCount,
                    goals1, table1.getXs());
            mostEnviedCluster.ifPresent(otherCluster -> {
                Function<Row, Cell> classifier = row -> cluster.contains(row) ? new SymbolCell("class1")
                        : new SymbolCell("class2");
                List<Row> clusterRows = Stream.concat(cluster.getRows().stream(), otherCluster.getRows().stream())
                        .collect(Collectors.toList());
                int steps = (int) Math.sqrt(clusterRows.size());
                Tree decisionTree = table1.tree(clusterRows, classifier, () -> new Sym(0, "Class"), 0, steps);
                System.out.println();
                System.out.println();
                System.out.println("-".repeat(15));
                System.out.print("For cluster with centroid = ");
                cluster.getCentroid().print();
                System.out.print("Most envied cluster = ");
                otherCluster.getCentroid().print();
                decisionTree.print(new Prefix(0));
            });
        }
    }

    private static Optional<Cluster> getMostEnviedCluster(Cluster cluster, List<Cluster> clusters,
                                                          Function<Cluster, Double> dominationFunction,
                                                          List<Col> goals,
                                                          List<Col> xCols) {
        return clusters
                .stream()
                .filter(clu -> clu.getCentroid().dominates(cluster.getCentroid(), goals) < 0.0)
                .map(other -> Pair.of(other, (1.0 - cluster.getCentroid().dis(other.getCentroid(), xCols))
                        + dominationFunction.apply(other)))
                .max(Comparator.comparing(Pair::getRight))
                .map(Pair::getLeft);
    }

    private static String getHeaderSeparator(int n) {
        return Collections.nCopies(n, "---").stream().collect(Collectors.joining("|", "|", "|"));
    }

    private static int dominationCount(Row row, List<Row> rows, List<Col> goals, int count) {
        int n = rows.size();
        List<Integer> indexes = getRandomIndexes(n, Math.min(n, count));
        int ret = 0;
        for (int index : indexes) {
            if (row.dominates(rows.get(index), goals) < 0) {
                ret++;
            }
        }
        return ret;
    }

    private static List<Integer> getRandomIndexes(int n, int count) {
        List<Integer> indexes = IntStream.range(0, n).boxed().collect(Collectors.toList());
        Collections.shuffle(indexes);
        return indexes.subList(0, count);
    }
}

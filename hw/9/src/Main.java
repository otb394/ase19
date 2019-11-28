import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        Path debugPath = Path.of("9/output/temp.txt");
        Path outputPath = Path.of("9/output/output.md");
        try (PrintWriter writer = new PrintWriter(debugPath.toFile());
             PrintWriter outputWriter = new PrintWriter(outputPath.toFile())) {
            execute("xomo10000", writer, outputWriter);
            execute("pom310000", writer, outputWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void execute(String fileName, PrintWriter writer, PrintWriter outputWriter) {
        TblObject.reset();
        Table baseLineTable = new Table(fileName);
        baseLineTable.read("9/input/" + fileName + ".csv");
        baseLineTable.process();
        Tree baseLineTree = baseLineTable.clusteringTree();
        writer.println("Baseline tree");
        baseLineTree.print(new Prefix(0), writer);
        List<Col> goals = baseLineTable.getGoals();
        Map<Row, List<Col>> probeBefore = new HashMap<>();
        List<Row> rows = baseLineTable.getRows();
        int rowSize = rows.size();
        List<Integer> probeIndexes = getRandomIndexes(rowSize, 100);
        Set<Row> probeSet = probeIndexes.stream().map(rows::get).collect(Collectors.toSet());
        Set<Integer> goalsPositions = baseLineTable.getGoals().stream().map(Col::getPos).collect(Collectors.toSet());
        List<Cluster> clusters = baseLineTree.getClusters();
        for (Cluster cluster : clusters) {
            List<Col> clusterGoals = cluster.getCols().stream().filter(col -> goalsPositions.contains(col.getPos()))
                    .collect(Collectors.toList());
            List<Row> clusterRows = cluster.getRows();
            for (Row thisRow : clusterRows) {
                if (probeSet.contains(thisRow)) {
                    probeBefore.put(thisRow, clusterGoals);
                }
            }
        }

        int noOfTrials = 20;
        List<Map<Pair<String, Integer>, Integer>> trialsWiseGoalCounts = new ArrayList<>();
        for (int i = 0; i < noOfTrials; i++) {
            Table newTable = new Table(fileName);
            newTable.read("9/input/" + fileName + ".csv");
            newTable.process();
            Tree baseLineAfterTree = newTable.clusteringTree();
            writer.println("Trial no = " + i);
            baseLineAfterTree.print(new Prefix(0), writer);
            trialsWiseGoalCounts.add(getGoalCounts(baseLineAfterTree, goals, goalsPositions, probeSet, probeBefore));
        }
        outputWriter.println("All results\\");
        printResults(fileName, goals, trialsWiseGoalCounts, outputWriter);

        executeIncremental(fileName, writer, goals, goalsPositions, probeSet, probeBefore, 0.1, outputWriter);
        executeIncremental(fileName, writer, goals, goalsPositions, probeSet, probeBefore, 0.3, outputWriter);
        executeIncremental(fileName, writer, goals, goalsPositions, probeSet, probeBefore, 0.5, outputWriter);
        executeIncremental(fileName, writer, goals, goalsPositions, probeSet, probeBefore, 0.7, outputWriter);

        outputWriter.println("-".repeat(20));
    }

    private static void executeIncremental(String fileName, PrintWriter writer, List<Col> goals,
                                    Set<Integer> goalsPositions,
                                    Set<Row> probeSet,
                                    Map<Row, List<Col>> probeBefore, double alpha, PrintWriter outputWriter) {
        List<Map<Pair<String, Integer>, Integer>> trialWiseMapList = new ArrayList<>();
        int noOfTrials = 20;
        for (int i = 0; i < noOfTrials; i++) {
            TblObject.reset();
            Table table = new Table(fileName);
            table.read("9/input/" + fileName + ".csv");
            List<Row> incRows = table.getRows();
            List<Row> sample = incRows.subList(0, 5000);
            Collections.shuffle(sample);
            List<Row> initialRows = sample.subList(0, 500);
            Table initTable = new Table(fileName, table.getCols());
            for (Row row : initialRows) {
                initTable.addRow(row);
            }
            initTable.process();
            Tree clusterTree = initTable.clusteringTree();
            writer.println("Initial tree");
            clusterTree.print(new Prefix(0), writer);
            List<Row> restRows = Stream.concat(sample.subList(500, 5000).stream(),
                    incRows.subList(5000, incRows.size()).stream()).collect(Collectors.toList());
            for (Row row : restRows) {
                clusterTree.update(row, alpha);
            }
            writer.println("After updates tree");
            clusterTree.print(new Prefix(0), writer);
            trialWiseMapList.add(getGoalCountsN(clusterTree, goals, goalsPositions, probeSet, probeBefore,
                    (r1, r2) -> r1.dis(r2, initTable.getXs())));
            writer.println("-".repeat(50));
        }
        outputWriter.println("-".repeat(50));
        outputWriter.println("Incremental results for alpha = " + alpha + "\\");
        printResults(fileName, goals, trialWiseMapList, outputWriter);
    }

    private static Map<Pair<String, Integer>, Integer> getGoalCountsN(Tree tree,
                                                                      List<Col> theseGoals,
                                                                      Set<Integer> goalPositions,
                                                                      Set<Row> probeSet,
                                                                      Map<Row, List<Col>> probeBefore,
                                                                      BiFunction<Row, Row, Double> dis) {
        Map<Pair<String, Integer>, Integer> goalCounts = new HashMap<>();
        for (Col goal : theseGoals) {
            goalCounts.put(Pair.of(goal.getName(), goal.getPos()), 0);
        }
        Map<Row, List<Col>> probeAfter = new HashMap<>();
        List<Cluster> trialClusters = tree.getClusters();
        for (Row row : probeSet) {
            Cluster rowCluster = trialClusters.stream().map(clu -> Pair.of(dis.apply(row, clu.getCentroid()), clu))
                    .min(Comparator.comparing(Pair::getLeft)).map(Pair::getRight).orElseThrow();
            probeAfter.put(row, rowCluster.getCols().stream().filter(col -> goalPositions.contains(col.getPos()))
                    .collect(Collectors.toList()));
        }
//        for (Cluster cluster : trialClusters) {
//            List<Col> goals = cluster.getCols().stream().filter(col -> goalPositions.contains(col.getPos()))
//                    .collect(Collectors.toList());
//            List<Row> clusterRows = cluster.getRows();
//            for (Row thisRow : clusterRows) {
//                if (probeSet.contains(thisRow)) {
//                    probeAfter.put(thisRow, goals);
//                }
//            }
//        }
        for (Map.Entry<Row, List<Col>> probes : probeAfter.entrySet()) {
            List<Col> beforeValues = probeBefore.get(probes.getKey());
            beforeValues.sort(Comparator.comparing(Col::getPos));
            List<Col> afterValues = probes.getValue();
            afterValues.sort(Comparator.comparing(Col::getPos));
            int size = beforeValues.size();
            for (int j = 0; j < size; j++) {
                Col bfr = beforeValues.get(j);
                boolean same = bfr.same(afterValues.get(j));
                if (same) {
                    Pair<String, Integer> pr = Pair.of(bfr.getName(), bfr.getPos());
                    int val = goalCounts.getOrDefault(pr, 0);
                    goalCounts.put(pr, val+1);
                }
            }
        }
        return goalCounts;
    }
    private static Map<Pair<String, Integer>, Integer> getGoalCounts(Tree tree,
                                                                     List<Col> theseGoals,
                                                                     Set<Integer> goalPositions,
                                                                     Set<Row> probeSet,
                                                                     Map<Row, List<Col>> probeBefore) {
        Map<Pair<String, Integer>, Integer> goalCounts = new HashMap<>();
        for (Col goal : theseGoals) {
            goalCounts.put(Pair.of(goal.getName(), goal.getPos()), 0);
        }
        Map<Row, List<Col>> probeAfter = new HashMap<>();
        List<Cluster> trialClusters = tree.getClusters();
        for (Cluster cluster : trialClusters) {
            List<Col> goals = cluster.getCols().stream().filter(col -> goalPositions.contains(col.getPos()))
                    .collect(Collectors.toList());
            List<Row> clusterRows = cluster.getRows();
            for (Row thisRow : clusterRows) {
                if (probeSet.contains(thisRow)) {
                    probeAfter.put(thisRow, goals);
                }
            }
        }
        for (Map.Entry<Row, List<Col>> probes : probeAfter.entrySet()) {
            List<Col> beforeValues = probeBefore.get(probes.getKey());
            beforeValues.sort(Comparator.comparing(Col::getPos));
            List<Col> afterValues = probes.getValue();
            afterValues.sort(Comparator.comparing(Col::getPos));
            int size = beforeValues.size();
            for (int j = 0; j < size; j++) {
                Col bfr = beforeValues.get(j);
                boolean same = bfr.same(afterValues.get(j));
                if (same) {
                    Pair<String, Integer> pr = Pair.of(bfr.getName(), bfr.getPos());
                    int val = goalCounts.getOrDefault(pr, 0);
                    goalCounts.put(pr, val+1);
                }
            }
        }
        return goalCounts;
    }

    private static void printResults(String fileName, List<Col> goals,
                                     List<Map<Pair<String, Integer>, Integer>> trialsWiseGoalCounts,
                                     PrintWriter outputWriter) {
        outputWriter.println(fileName + "\\");
        outputWriter.println("Trial wise counts when probes land on same distribution for each goal");
        outputWriter.println();
        List<String> headings = Stream.concat(List.of("Trial no").stream(), goals.stream()
                .sorted(Comparator.comparing(Col::getPos)).map(Col::getName)).collect(Collectors.toList());
        outputWriter.println(headings.stream().collect(Collectors.joining("|", "|", "|")));
        outputWriter.println(getHeaderSeparator(goals.size()+1));
        List<Num> freshGoalCounts = goals.stream().map(col -> new Num(col.getPos(), col.getName()))
                .collect(Collectors.toList());
        Map<Pair<String, Integer>, Num> freshGoalMap = new HashMap<>();
        for (Num goalCount : freshGoalCounts) {
            freshGoalMap.put(Pair.of(goalCount.getName(), goalCount.getPos()), goalCount);
        }
        for (int i = 0; i < trialsWiseGoalCounts.size(); i++) {
            int trialIndex = i+1;
            String outputRow = Stream.concat(Stream.of(Integer.toString(trialIndex)),
                    trialsWiseGoalCounts.get(i).entrySet().stream()
                            .sorted(Comparator.comparing(entry -> entry.getKey().getRight()))
                            .map(entry -> entry.getValue().toString()))
                    .collect(Collectors.joining("|","|","|"));
            outputWriter.println(outputRow);
            for (Map.Entry<Pair<String, Integer>, Integer> entry : trialsWiseGoalCounts.get(i).entrySet()) {
                Pair<String, Integer> key = entry.getKey();
                Num valCol = freshGoalMap.get(key);
                int delta = entry.getValue();
                valCol.add(delta);
                freshGoalMap.put(key, valCol);
            }
        }
        String outputMean = Stream.concat(Stream.of("Mean"), freshGoalMap.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getRight()))
                .map(entry -> String.format("%.2f", entry.getValue().getMean())))
                .collect(Collectors.joining("|","|","|"));
        outputWriter.println(outputMean);
    }

    private static String getHeaderSeparator(int n) {
        return Collections.nCopies(n, "---").stream().collect(Collectors.joining("|", "|", "|"));
    }

    private static List<Integer> getRandomIndexes(int n, int count) {
        List<Integer> indexes = IntStream.range(0, n).boxed().collect(Collectors.toList());
        Collections.shuffle(indexes);
        return indexes.subList(0, count);
    }
}

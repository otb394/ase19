
public class Main {
    private static final int MAX = 1001;
    private static final int COUNT = 100;
    private static final double THRESHOLD = 0.001;

    public static void main(String[] args) {
        System.out.println("#--- zerorok -----------------------");
        Table table = new Table("weathernon");
        table.read("4/input/weathernon.csv");
        ZeroR zeroR = new ZeroR(table, 2);
        zeroR.run();
        zeroR.report();

        Table table1 = new Table("diabetes");
        table1.read("4/input/diabetes.csv");
        ZeroR zeroR1 = new ZeroR(table1, 2);
        zeroR1.run();
        zeroR1.report();

        System.out.println();
        System.out.println();
        System.out.println("#--- nbok -----------------------");
        Table table2 = new Table("weathernon");
        table2.read("4/input/weathernon.csv");
        NaiveBayesClassifier nb = new NaiveBayesClassifier(table2, 3);
        nb.run();
        nb.report();

        Table table3 = new Table("diabetes");
        table3.read("4/input/diabetes.csv");
        NaiveBayesClassifier nb2 = new NaiveBayesClassifier(table3, 19);
        nb2.run();
        nb2.report();
    }
}


public class Main {
    private static final int MAX = 1001;
    private static final int COUNT = 100;
    private static final double THRESHOLD = 0.001;

    public static void main(String[] args) {
        System.out.println("#--- zerorok -----------------------");
        Table table = new Table("weathernon");
        table.read("4/input/weathernon.csv");
        ZeroR zeroR = new ZeroR(table);
        zeroR.run();
        zeroR.report();

        Table table1 = new Table("diabetes");
        table1.read("4/input/diabetes.csv");
        ZeroR zeroR1 = new ZeroR(table1);
        zeroR1.run();
        zeroR1.report();
    }
}

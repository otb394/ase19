public class Main {
    public static void main(String[] args) {
        TblObject.reset();
        Table table = new Table("diabetes");
        table.read("6/input/diabetes.csv");
        table.process();
        Tree decisionTree = table.decisionTree();
        decisionTree.print(new Prefix(0));

        TblObject.reset();
        Table table1 = new Table("auto");
        table1.read("6/input/auto.csv");
        table1.process();
        Tree decisionTree1 = table1.regressionTree();
        System.out.println();
        System.out.println();
        decisionTree1.print(new Prefix(0));
    }
}

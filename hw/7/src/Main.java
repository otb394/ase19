public class Main {
    public static void main(String[] args) {
        TblObject.reset();
        Table table = new Table("xomo");
        table.read("7/input/xomo10000.csv");
        table.process();
        Tree clusteringTree = table.clusteringTree();
        clusteringTree.print(new Prefix(0));

        TblObject.reset();
        Table table1 = new Table("pom");
        table1.read("7/input/pom310000.csv");
        table1.process();
        Tree clusteringTree1 = table1.clusteringTree();
        clusteringTree1.print(new Prefix(0));
    }
}

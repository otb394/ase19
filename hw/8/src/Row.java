import java.util.List;
import java.util.StringJoiner;

public class Row extends TblObject {
    // For distance function
    private static final int P = 2;
    private int pos;
    private List<Cell> cells;
    private boolean isSkipped;
    private boolean isBlank;

    Row(int pos, List<Cell> cells, boolean isBlank) {
        super();
        this.pos = pos;
        this.cells = cells;
        this.isSkipped = false;
        this.isBlank = isBlank;
    }

    public void print() {
        if (this.isBlank) {
            return;
        } else if (this.isSkipped) {
            System.out.println("E> skipping line " + (pos + 1));
        } else {
            int size = cells.size();
            System.out.print("[");
            for (int index = 0; index < size; index++) {
                cells.get(index).print();
                if (index != size - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }
    }

    public void dump(Prefix prefix) {
        System.out.println(prefix + "cells");
        dumpCells(prefix.next());
        System.out.println(prefix + "cooked");
        System.out.println(prefix + "dom: 0");
        System.out.println(prefix + "oid: " + oid);
    }

    private void dumpCells(Prefix prefix) {
        for (int cellIndex = 0; cellIndex < cells.size(); cellIndex++) {
            System.out.print(prefix);
            System.out.print(cellIndex + 1);
            System.out.print(": ");
            cells.get(cellIndex).print();
            System.out.println();
        }
    }

    public double dominates(Row other, List<Col> goals) {
        double z = 0.00001;
        double s1 = z;
        double s2 = z;
        double n = z + goals.size();
        for (Col goal : goals) {
            double a = goal.norm(this.getCells().get(goal.getPos() - 1));
            double b = goal.norm(other.getCells().get(goal.getPos() - 1));
            s1 -= Math.pow(10, goal.getWeight() * (a-b)/n);
            s2 -= Math.pow(10, goal.getWeight() * (b-a)/n);
        }
        return s1/n - s2/n;
    }

    /**
     * To calculate envy
     *
     * @param delta Distance between this row and the other row [0,1]
     * @param dominate Measure of how much this row dominates the other row, with negative values denoting domination
     * @return Envy
     */
    public double envy(double delta, double dominate) {
        return (1.0 - delta) - dominate;
    }

    public double dis(Row other, List<Col> independentCols) {
        double d = 0.0;
        int n = 0;
        for(Col col: independentCols) {
            n++;
            double d0 = col.dis(this.getCells().get(col.getPos() - 1), other.getCells().get(col.getPos() - 1));
            d += Math.pow(d0, P);
        }
        return Math.pow(d, (1.0/((double)P))) / Math.pow(n, (1.0/((double)P)));
    }

    public boolean isSkipped() {
        return isSkipped;
    }

    public void skip() {
        this.isSkipped = true;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void printGithub() {
        StringJoiner joiner = new StringJoiner("|", "|", "|");
        for (Cell cell : cells) {
            joiner.add(cell.toString());
        }
        System.out.println(joiner.toString());
    }
}

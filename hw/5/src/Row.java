import java.util.List;

public class Row extends TblObject {
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

    public boolean isSkipped() {
        return isSkipped;
    }

    public void skip() {
        this.isSkipped = true;
    }

    public List<Cell> getCells() {
        return cells;
    }
}

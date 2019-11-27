public interface Cell extends Comparable<Cell> {
    void print();
    String toString();

    default boolean isSkipped() {
        return false;
    }

    void addTo(Col col);

    void removeFrom(Col col);

    default boolean within(NumericalRange numericalRange) {
        return false;
    }

    default boolean within(SymbolicRange symbolicRange) {
        return false;
    }

    default double likedBy(Num num) {
        throw new RuntimeException("This should either be overriden or not called");
    }

    default double likedBy(Sym sym, double prior, double pseudocountM) {
        throw new RuntimeException("This should either be overriden or not called");
    }

    default double diff(Cell cell) {
        if (this.equals(cell)) {
            return 0.0;
        } else {
            return Double.MAX_VALUE;
        }
    }

    static Cell of(String text) {
        return of(text, false);
    }

    static Cell of(String text, boolean knownSymbolColumn) {
        String stripped = text.strip();
        if (stripped.equals("?")) {
            return new QuestionMark();
        } else if (knownSymbolColumn) {
            return new SymbolCell(stripped);
        } else {
            try {
                return new NumberCell(Double.parseDouble(stripped));
            } catch (Exception e) {
                return new SymbolCell(stripped);
            }
        }
    }
}

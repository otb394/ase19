public interface Cell extends Comparable<Cell> {
    void print();
    String toString();

    default boolean isSkipped() {
        return false;
    }

    void addTo(Col col);

    void removeFrom(Col col);

    default double likedBy(Num num) {
        throw new RuntimeException("This should either be overriden or not called");
    }

    default double likedBy(Sym sym, double prior, double pseudocountM) {
        throw new RuntimeException("This should either be overriden or not called");
    }

    static Cell of(String text) {
        String stripped = text.strip();
        if (stripped.equals("?")) {
            return new QuestionMark();
        } else {
            try {
                return new NumberCell(Double.parseDouble(stripped));
            } catch (Exception e) {
                return new SymbolCell(stripped);
            }
        }
    }
}

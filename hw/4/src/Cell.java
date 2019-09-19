public interface Cell {
    void print();
    String toString();

    default boolean isSkipped() {
        return false;
    }

    void addTo(Col col);

    static Cell of(String text) {
        String stripped = text.strip();
        if (stripped.equals("?")) {
            return new QuestionMark();
        } else {
            try {
                return new NumberCell(Integer.parseInt(stripped));
            } catch (Exception e) {
                return new SymbolCell(stripped);
            }
        }
    }
}

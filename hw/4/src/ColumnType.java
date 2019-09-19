public enum ColumnType {
    LESS, MORE, DOLLAR, EXCLAMATION, OTHER, IGNORE;

    public static ColumnType of(String name) {
        if (name.contains("?")) {
            return IGNORE;
        }
        if (name.contains("<")) {
            return LESS;
        }
        if (name.contains(">")) {
            return MORE;
        }
        if (name.contains("$")) {
            return DOLLAR;
        }
        if (name.contains("!")) {
            return EXCLAMATION;
        }
        return OTHER;
    }
}

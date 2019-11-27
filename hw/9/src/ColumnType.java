public enum ColumnType {
    LESS, MORE, DOLLAR, EXCLAMATION, EXCLAMATION_DOLLAR, OTHER, IGNORE;

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
        if (name.contains("!")) {
            if (name.contains("$")) {
                return EXCLAMATION_DOLLAR;
            } else {
                return EXCLAMATION;
            }
        }
        if (name.contains("$")) {
            return DOLLAR;
        }
        return OTHER;
    }
}

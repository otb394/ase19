import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SymbolicRange implements Range {
    private Set<String> symbols;

    public SymbolicRange(Collection<String> symbols) {
        this.symbols = new HashSet<>();
        this.symbols.addAll(symbols);
    }

    @Override
    public boolean contains(Cell cell) {
        return cell.within(this);
    }

    @Override
    public int compareTo(Range range) {
        return 0;
    }

    public Set<String> getSymbols() {
        return symbols;
    }

    @Override
    public String toString() {
        return String.join(", ", symbols);
    }
}

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

public interface Tree {
    void print(Prefix prefix);
    default void print(Prefix prefix, PrintWriter writer) {
        this.print(prefix);
    }
    int size();
    List<Cluster> getClusters();
    default Tree update(Row row, double alpha) {
        return this;
    }
}

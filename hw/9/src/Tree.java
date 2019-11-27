import java.util.List;

public interface Tree {
    void print(Prefix prefix);
    int size();
    List<Cluster> getClusters();
}

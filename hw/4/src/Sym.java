import java.util.HashMap;
import java.util.Map;

public class Sym extends Col {
    private Map<String, Integer> frequencyMap;
    private int count;
    private String mode;
    private int modeCount;

    public Sym(int pos, String name) {
        super(pos, name);
        this.frequencyMap = new HashMap<>();
        this.count = 0;
        this.mode = null;
        this.modeCount = 0;
    }

    @Override
    public void dump(Prefix prefix) {
        System.out.println(prefix + "add: Sym1");
        System.out.println(prefix + "cnt");
        dumpFrequencyMap(prefix.next());
        System.out.println(prefix + "col: " + pos);
        System.out.println(prefix + "mode: " + mode);
        System.out.println(prefix + "most: " + modeCount);
        System.out.println(prefix + "n: " + count);
        System.out.println(prefix + "oid: " + oid);
        System.out.println(prefix + "txt: " + name);
    }

    private void dumpFrequencyMap(Prefix prefix) {
        frequencyMap.forEach((cat, freq) -> System.out.println(prefix + cat + ": " + freq));
    }

    @Override
    public void add(String v) {
        count++;
        int newFreq = frequencyMap.getOrDefault(v, 0) + 1;
        frequencyMap.put(v, newFreq);
        if (newFreq > modeCount) {
            modeCount = newFreq;
            mode = v;
        }
    }

    /**
     * This gives the laplace estimate
     */
    public double like(String v, double pseudoCountK) {
        int totalDistinct = frequencyMap.containsKey(v) ? frequencyMap.size() : frequencyMap.size() + 1;
        return (frequencyMap.getOrDefault(v, 0) + pseudoCountK) / (count + totalDistinct*pseudoCountK);
    }

    /**
     * This gives the M-estimate
     */
    public double like(String v, double prior, double pseudocountM) {
        return (frequencyMap.getOrDefault(v, 0) + pseudocountM*prior) / (count + pseudocountM);
    }

    public Double getEntropy() {
        return frequencyMap.values().stream()
                .map(freq -> ((double)freq)/((double)count))
                .map(p -> -(p*(Math.log(p))/(Math.log(2))))
                .reduce(0.0, Double::sum);
    }

    public String getMode() {
        return mode;
    }
}

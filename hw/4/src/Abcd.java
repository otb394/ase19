import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

public class Abcd {
    private Map<String, Integer> mapA;
    private Map<String, Integer> mapB;
    private Map<String, Integer> mapC;
    private Map<String, Integer> mapD;
    private Set<String> known;
    private int yes;
    private int no;

    public Abcd() {
        mapA = new HashMap<>();
        mapB = new HashMap<>();
        mapC = new HashMap<>();
        mapD = new HashMap<>();
        known = new HashSet<>();
        yes = no = 0;
    }

    public void abcd1(String predicted, String actual) {
        if (!known.contains(actual)) {
            known.add(actual);
            mapA.put(actual, yes + no);
            mapB.put(actual, 0);
            mapC.put(actual, 0);
            mapD.put(actual, 0);
        }

        if (!known.contains(predicted)) {
            known.add(predicted);
            mapA.put(predicted, yes + no);
            mapB.put(predicted, 0);
            mapC.put(predicted, 0);
            mapD.put(predicted, 0);
        }

        if (actual.equals(predicted)) {
            yes++;
        } else {
            no++;
        }
        known.forEach(category -> update(category, actual, predicted));
    }

    public void report() {
        String p = " %4.2f";
        String q = " %4s";
        String r = " %5s";
        String s = " |";
        String ds = "----";
        String formatString = r+s+r+s+r+s+r+s+r+s+r+s+r+s+q+s+q+s+q+s+q+s+q+s+q+s+" class\n";
        System.out.printf(formatString, "db","rx","num","a","b","c","d","acc","pre","pd","pf","f","g");
        formatString = r+ s+r+s+r+s+r+s+r+s+r+s+r+s+q+s+q+s+q+s+q+s+q+s+q+s+"-----\n";
        System.out.printf(formatString, ds,ds,ds,ds,ds,ds,ds,ds,ds,ds,ds,ds,ds);
        for (String category: known) {
            double pd = 0;
            double pf = 0;
            double pn = 0;
            double prec = 0;
            double g = 0;
            double f = 0;
            double acc = 0;
            int a = mapA.get(category);
            int b = mapB.get(category);
            int c = mapC.get(category);
            int d = mapD.get(category);
            if (b+d > 0) {
                pd = ((double)d) / ((double) (b+d));
            }
            if (a+c > 0) {
                pf = ((double) c) / ((double) (a+c));
                pn = ((double)(b+d))/((double)(a+c));
            }
            if (c+d > 0) {
                prec = ((double)d) / ((double)(c+d));
            }
            if (1-pf+pd > 0) {
                g = 2.0*(1-pf)*pd/(1.0-pf+pd);
            }
            if (prec+pd > 0) {
                f = 2.0*prec*pd/(prec+pd);
            }
            if (yes + no > 0) {
                acc = ((double) yes) / ((double)(yes+no));
            }
            formatString = r+s+   r+s+ r+s+       r+s+r+s+r+s+r+s+p+s+p+s+ p+s+p+s+p+s+p+s+" %s\n";
            System.out.printf(formatString, "data","rx",yes+no, a, b, c, d, acc, prec, pd, pf, f, g, category);
        }
    }

    private void update(String category, String actual, String predicted) {
        if (actual.equals(category)) {
            if (actual.equals(predicted)) {
                updateMap(mapD, category, x -> x+1);
            } else {
                updateMap(mapB, category, x -> x+1);
            }
        } else {
            if (predicted.equals(category)) {
                updateMap(mapC, category, x -> x+1);
            } else {
                updateMap(mapA, category, x -> x+1);
            }
        }
    }

    private <A,B> void updateMap(Map<A, B> mp, A key, UnaryOperator<B> operator) {
        Optional<B> newValue = Optional.ofNullable(mp.get(key)).map(operator);
        newValue.ifPresentOrElse(val -> mp.put(key, val), () -> {
            throw new RuntimeException("Value not found");
        });
    }
}

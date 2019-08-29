import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    private static final int MAX = 1001;
    private static final int COUNT = 100;
    private static final double THRESHOLD = 0.001;

    public static void main(String[] args) {
        int[] randomNums = new int[100];
        for (int i = 0; i < COUNT; i++) {
            randomNums[i] = ThreadLocalRandom.current().nextInt(MAX);
        }
        System.out.println("List of random numbers:");
        for (int i = 0; i < COUNT; i++) {
            System.out.print(randomNums[i]);
            System.out.print(" ");
        }
        System.out.println();
        System.out.println();
        Col col = new Col();
        col.update(randomNums);
    }

    public static class Col {
        private List<Double> cachedMeans;
        private List<Double> cachedSDs;

        public Col() {
            cachedMeans = new ArrayList<>();
            cachedSDs = new ArrayList<>();
        }

        public void update(int[] nums) {
            int n = nums.length;
            Num num = new Num();
            int z=0;
            for (int i = 1; i <= n; i++) {
                num.add(nums[i-1]);
                if (i%10 == 0) {
                    cachedMeans.add(num.getMean());
                    cachedSDs.add(num.getSD());
                }
            }

            int noOfMismatch = 0;

            for (int i = n; i >= 1; i--) {
                if (i%10 == 0) {
                    double mean = num.getMean();
                    double sd = num.getSD();
                    double cachedMean = cachedMeans.get((i/10)-1);
                    double cachedSD = cachedSDs.get((i/10)-1);
                    System.out.println("Number index: " + i);
                    System.out.println("Mean on removal: " + mean);
                    System.out.println("Mean in cache: " + cachedMean);
                    System.out.println("SD on removal: " + sd);
                    System.out.println("SD in cache: " + cachedSD);
                    System.out.println();
                    if (!Maths.equals(mean, cachedMean) || !Maths.equals(sd, cachedSD)) {
                        noOfMismatch++;
                    }
                }
                num.remove(nums[i-1]);
            }
            if (noOfMismatch == 0) {
                System.out.println("All values are equal with cache upto the threshold of " + THRESHOLD);
            } else {
                System.out.println("No. of mismatches = " + noOfMismatch);
            }
        }

        private static class Num {
            private int count;
            private double mean;
            private double m2;

            Num() {
                this.count = 0;
                this.mean = 0.0;
                this.m2 = 0.0;
            }

            private double getSD() {
                if (count < 2) return 0.0;
                if (m2 < 0.0) return 0.0;
                return Math.sqrt(m2/(count-1));
            }

            private double getMean() {
                return mean;
            }

            private void add(int v) {
                count++;
                double delta = v-mean;
                mean += delta / count;
                m2+=delta*(v-mean);
            }

            private void remove(int v) {
                if (count == 0) return;
                double delta = v-mean;
                double diff = mean - ((double)v)/((double)count);
                mean = diff * ((double)count/(((double)count)-1));
                count--;
                m2-=delta*(v-mean);
            }
        }
    }

    public static class Maths {
        public static boolean equals(double a, double b) {
            return Math.abs(a-b) <= THRESHOLD;
        }
    }
}

package eecs545;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Augie
 */
public class MultiKMeansClassifier extends MultiClassifier {

    public static final int MAX_ITERATIONS = 150;
    // Only need to adjust slightly after the first iteration
    private final double[][][][] centroids;
    private int K;
    // Assignments of pixels to clusters
    private Map<Input, Integer> r = new HashMap<Input, Integer>();
    private int[] clusterLabels;
    double[][][] avgNumPointsInBin = new double[Utils.RGB_HIST_DIM_BINS][Utils.RGB_HIST_DIM_BINS][Utils.RGB_HIST_DIM_BINS];

    public MultiKMeansClassifier(int K) {
        this.K = K;
        centroids = new double[K][Utils.RGB_HIST_DIM_BINS][Utils.RGB_HIST_DIM_BINS][Utils.RGB_HIST_DIM_BINS];
        MathUtils.fill(centroids, 0);
        clusterLabels = new int[K];
        Arrays.fill(clusterLabels, 0);
    }

    @Override
    public void train(List<Input> train) throws Exception {
        // Can't be empty
        if (train.isEmpty()) {
            throw new Exception("Training set is empty.");
        }
        // Must be at least K examples
        if (train.size() < K) {
            throw new Exception("Must provide at least K examples");
        }

        // Calculate the necessary chi-squared statistic
        MathUtils.fill(avgNumPointsInBin, 0);
        double points = 0;
        for (Input input : train) {
            for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
                for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                    for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                        avgNumPointsInBin[rBin][gBin][bBin] += input.rgbHist[rBin][gBin][bBin];
                        points += input.rgbHist[rBin][gBin][bBin];
                    }
                }
            }
        }
        // Avg
        for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
            for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                    avgNumPointsInBin[rBin][gBin][bBin] /= points;
                }
            }
        }

        // Initialize each cluster centroid
        for (int i = 0; i < K; i++) {
            double[][][] histProp = train.get(MathUtils.RANDOM.nextInt(train.size())).rgbHistProp;
            for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
                for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                    for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                        centroids[i][rBin][gBin][bBin] = histProp[rBin][gBin][bBin];
                    }
                }
            }
        }

        // Iterate until convergence or max
        int its = 0;
        for (int i = 0, sames = 0; i < MAX_ITERATIONS && sames < 5; i++, its++) {
            // Do at least 10 iterations
            boolean somethingChanged = (i < 5);
            
            // E-step: assign each input to the closest centroid
            int[] pointsPerCluster = new int[K];
            Arrays.fill(pointsPerCluster, 0);
            for (Input input : train) {
                // Calculate distances
                int closestCluster = -1;
                double[] distance = new double[K];
                for (int k = 0; k < K; k++) {
                    for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
                        for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                            for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                                distance[k] += Math.pow(centroids[k][rBin][gBin][bBin] - input.rgbHistProp[rBin][gBin][bBin], 2) / avgNumPointsInBin[rBin][gBin][bBin];
                            }
                        }
                    }
                    distance[k] = Math.sqrt(distance[k]);
                    if (closestCluster == -1 || distance[k] < distance[closestCluster]) {
                        closestCluster = k;
                    }
                }

                // Assign pixel to closest
                if (r.containsKey(input) && r.get(input) != closestCluster) {
                    somethingChanged = true;
                }
                r.put(input, closestCluster);
                pointsPerCluster[closestCluster]++;
            }

            // Nothing changed
            if (!somethingChanged) {
                sames++;
            } else {
                sames = 0;
            }

            // M-step
            for (int k = 0; k < K; k++) {
                if (pointsPerCluster[k] == 0) {
                    continue;
                }
                for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
                    for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                        for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                            centroids[k][rBin][gBin][bBin] = 0;
                        }
                    }
                }
            }
            for (Input input : train) {
                for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
                    for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                        for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                            centroids[r.get(input).intValue()][rBin][gBin][bBin] += input.rgbHistProp[rBin][gBin][bBin];
                        }
                    }
                }
            }
            for (int k = 0; k < K; k++) {
                if (pointsPerCluster[k] == 0) {
                    continue;
                }
                for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
                    for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                        for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                            centroids[k][rBin][gBin][bBin] /= (double) pointsPerCluster[k];
                        }
                    }
                }
            }
        }
        System.out.println("Its: " + its);

        // ONLY ACTUALLY CARE ABOUT THE ONE
//        // Plurality of assigned inputs to clusters
//        for (int k = 0; k < K; k++) {
//            // What is the plurality label at this cluster?
//            Map<Integer, Integer> labelVotes = new HashMap<Integer, Integer>();
//            for (Input input : r.keySet()) {
//                if (r.get(input) == k) {
//                    if (!labelVotes.containsKey(input.label)) {
//                        labelVotes.put(input.label, 1);
//                    } else {
//                        labelVotes.put(input.label, labelVotes.get(input.label) + 1);
//                    }
//                }
//            }
//            int predictedLabel = 0, predictedLabelVotes = 0;
//            for (int label : labelVotes.keySet()) {
//                if (labelVotes.get(label) > predictedLabelVotes) {
//                    predictedLabel = label;
//                    predictedLabelVotes = labelVotes.get(label);
//                }
//            }
//            System.out.println(k + ": " + predictedLabel);
//            clusterLabels[k] = predictedLabel;
//        }
    }

    @Override
    public MultiResults test(List<Input> test) throws Exception {
        MultiResults results = new MultiResults();
        for (Input unknown : test) {
            // Get the closest cluster centroid
            int closestCluster = -1;
            double[] distance = new double[K];
            for (int k = 0; k < K; k++) {
                for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
                    for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                        for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                            distance[k] += Math.pow(centroids[k][rBin][gBin][bBin] - unknown.rgbHistProp[rBin][gBin][bBin], 2) / avgNumPointsInBin[rBin][gBin][bBin];
                        }
                    }
                }
                distance[k] = Math.sqrt(distance[k]);
                if (closestCluster == -1 || distance[k] < distance[closestCluster]) {
                    closestCluster = k;
                }
            }
            // What is the plurality label at this cluster?
            Map<Integer, Integer> labelVotes = new HashMap<Integer, Integer>();
            for (Input input : r.keySet()) {
                if (r.get(input) == closestCluster) {
                    if (!labelVotes.containsKey(input.label)) {
                        labelVotes.put(input.label, 1);
                    } else {
                        labelVotes.put(input.label, labelVotes.get(input.label) + 1);
                    }
                }
            }
            int predictedLabel = 0, predictedLabelVotes = 0;
            for (int label : labelVotes.keySet()) {
                if (labelVotes.get(label) > predictedLabelVotes) {
                    predictedLabel = label;
                    predictedLabelVotes = labelVotes.get(label);
                }
            }
//            System.out.println(predictedLabel + " / " + unknown.label);
            results.add(unknown, predictedLabel);
        }
        return results;
    }
}

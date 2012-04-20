package eecs545;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Augie
 */
public class MultiKNNClassifier extends MultiClassifier {

    private int K;
    private Collection<Input> train;
    // A statistic necessary for the chi-squared distance function
    double[][][] avgNumPointsInBin = new double[Utils.RGB_HIST_DIM_BINS][Utils.RGB_HIST_DIM_BINS][Utils.RGB_HIST_DIM_BINS];

    public MultiKNNClassifier(int K) {
        this.K = K;
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
        // Just remember all the training inputs
        this.train = train;
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
    }

    @Override
    public MultiResults test(List<Input> test) throws Exception {
        MultiResults results = new MultiResults();
        for (Input unknown : test) {
            // Find the K nearest neighbors to the test example
            Input[] closestInputs = new Input[K];
            double[] closestInputDistances = new double[K];
            // Find closest inputs
            for (Input known : train) {
//                // Calculate distance (L2)
//                double distance = 0;
//                int[][][] knownRGBHist = known.rgbHist;
//                int[][][] unknownRGBHist = unknown.rgbHist;
//                for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
//                    for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
//                        for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
//                            distance += Math.pow(knownRGBHist[rBin][gBin][bBin] - unknownRGBHist[rBin][gBin][bBin], 2);
//                        }
//                    }
//                }
//                distance = Math.sqrt(distance);
                // Calculate distance (Chi-Squared)
                double distance = 0;
                for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
                    for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                        for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                            distance += Math.pow(known.rgbHistProp[rBin][gBin][bBin] - unknown.rgbHistProp[rBin][gBin][bBin], 2) / avgNumPointsInBin[rBin][gBin][bBin];
                        }
                    }
                }
                distance = Math.sqrt(distance);

                // Replace one of the current closest?
                for (int k = 0; k < K; k++) {
                    if (closestInputs[k] == null
                            || distance < closestInputDistances[k]) {
                        closestInputs[k] = known;
                        closestInputDistances[k] = distance;
                        break;
                    }
                }
            }
            // What is the majority 
            Map<Integer, Integer> votes = new HashMap<Integer, Integer>();
            for (int k = 0; k < K; k++) {
                if (!votes.containsKey(closestInputs[k].label)) {
                    votes.put(closestInputs[k].label, 1);
                } else {
                    votes.put(closestInputs[k].label, votes.get(closestInputs[k].label) + 1);
                }
            }
            int pluralityLabel = -1, pluralityLabelVotes = -1;
            for (int voteLabel : votes.keySet()) {
                if (pluralityLabel == -1 || votes.get(voteLabel) > pluralityLabelVotes) {
                    pluralityLabel = voteLabel;
                    pluralityLabelVotes = votes.get(voteLabel);
                }
            }
            results.add(unknown, pluralityLabel);
        }
        return results;
    }
}

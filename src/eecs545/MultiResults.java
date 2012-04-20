package eecs545;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Augie
 */
public class MultiResults {

    public Map<Input, Integer> classifications = new HashMap<Input, Integer>();
    private Map<Integer, double[]> confusionMatrixMap = new HashMap<Integer, double[]>();

    public void add(Input input, int classification) {
        confusionMatrixMap.clear();
        classifications.put(input, classification);
    }

    public void combine(MultiResults results) {
        confusionMatrixMap.clear();
        classifications.putAll(results.classifications);
    }

    public double precision() {
        // Normalized weighted average precision for each label
        Map<Integer, Integer> labelCount = new HashMap<Integer, Integer>();
        for (Input input : classifications.keySet()) {
            if (!labelCount.containsKey(input.label)) {
                labelCount.put(input.label, 1);
            } else {
                labelCount.put(input.label, labelCount.get(input.label) + 1);
            }
        }
        double precision = 0;
        for (int label : labelCount.keySet()) {
            precision += (labelCount.get(label).doubleValue() / (double) classifications.size()) * precision(label);
        }
        return precision;
    }

    public double precision(int label) {
        double[] confusion = confusionMatrix(label);
        if (confusion[0] + confusion[2] == 0) {
            return 0;
        }
        return confusion[0] / (confusion[0] + confusion[2]);
    }

    public double recall() {
        // Normalized weighted average recall for each label
        Map<Integer, Integer> labelCount = new HashMap<Integer, Integer>();
        for (Input input : classifications.keySet()) {
            if (!labelCount.containsKey(input.label)) {
                labelCount.put(input.label, 1);
            } else {
                labelCount.put(input.label, labelCount.get(input.label) + 1);
            }
        }
        double precision = 0;
        for (int label : labelCount.keySet()) {
            precision += (labelCount.get(label).doubleValue() / (double) classifications.size()) * recall(label);
        }
        return precision;
    }

    public double recall(int label) {
        double[] confusion = confusionMatrix(label);
        if (confusion[0] + confusion[3] == 0) {
            return 0;
        }
        return confusion[0] / (confusion[0] + confusion[3]);
    }

    public double F() {
        if (precision() + recall() == 0) {
            return 0;
        }
        return (2 * precision() * recall()) / (precision() + recall());
    }

    public double F(int label) {
        if (precision(label) + recall(label) == 0) {
            return 0;
        }
        return (2 * precision(label) * recall(label)) / (precision(label) + recall(label));
    }

    // [true positive, true negative, false positive, false negative]
    public double[] confusionMatrix(int label) {
        if (!confusionMatrixMap.containsKey(label)) {
            if (classifications.isEmpty()) {
                confusionMatrixMap.put(label, new double[]{0, 0, 0, 0});
            } else {
                double truePositive = 0, trueNegative = 0, falsePositive = 0, falseNegative = 0;
                for (Input input : classifications.keySet()) {
                    int assigned = classifications.get(input);
                    if (assigned == label && input.label == label) {
                        truePositive++;
                    } else if (assigned != label && input.label != label) {
                        trueNegative++;
                    } else if (assigned == label && input.label != label) {
                        falsePositive++;
                    } else if (assigned != label && input.label == label) {
                        falseNegative++;
                    }
                }
                confusionMatrixMap.put(label, new double[]{truePositive, trueNegative, falsePositive, falseNegative});
            }
        }
        return confusionMatrixMap.get(label);
    }

    public void write(File outFile) throws Exception {
        PrintStream out = null;
        try {
            out = new PrintStream(FileUtils.openOutputStream(outFile));
            Set<Integer> labels = new HashSet<Integer>();
            for (Input input : classifications.keySet()) {
                labels.add(input.label);
            }
            for (int label : labels) {
                if (label == 0) {
                    continue;
                }
                // Print the confusion matrix
                double[] thhisConfusionMatrix = confusionMatrix(label);
                // true positive, true negative, false positive, false negative
                out.println(label + " " + thhisConfusionMatrix[0] + " " + thhisConfusionMatrix[1] + " " + thhisConfusionMatrix[2] + " " + thhisConfusionMatrix[3] + " (P: " + precision(label) + ", R: " + recall(label) + ", F: " + F(label) + ")");
            }
            // Print the expected and actual classifications
            for (Input input : classifications.keySet()) {
                // scene id, obj/seg id, really this label, classified whether this label
                out.println(input.scene + " " + input.objectOrSegment + " " + input.label + " " + classifications.get(input));
            }
        } finally {
            try {
                out.flush();
            } catch (Exception e) {
            }
            IOUtils.closeQuietly(out);
        }
    }
}

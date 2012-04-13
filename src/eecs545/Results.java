package eecs545;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Augie
 */
public class Results {

    public int label;
    public Map<Input, Boolean> classifications = new HashMap<Input, Boolean>();

    public Results(int label) {
        this.label = label;
    }

    public void combine(Results results) {
        if (results.label == label) {
            classifications.putAll(results.classifications);
        }
    }

    public void add(Input input, boolean classification) {
        classifications.put(input, classification);
    }

    // [true positive, true negative, false positive, false negative]
    public double[] confusionMatrix() {
        if (classifications.isEmpty()) {
            return new double[]{0, 0, 0, 0};
        }
        double truePositive = 0, trueNegative = 0, falsePositive = 0, falseNegative = 0;
        for (Input input : classifications.keySet()) {
            boolean assigned = classifications.get(input);
            if (assigned && input.label == label) {
                truePositive++;
            } else if (!assigned && input.label != label) {
                trueNegative++;
            } else if (assigned && input.label != label) {
                falsePositive++;
            } else if (!assigned && input.label == label) {
                falseNegative++;
            }
        }
        return new double[]{truePositive, trueNegative, falsePositive, falseNegative};
    }

    public void write(File outFile) throws Exception {
        PrintStream out = null;
        try {
            out = new PrintStream(FileUtils.openOutputStream(outFile));
            // Print the label
            out.println(label);
            // Print the confusion matrix
            double[] confusionMatrix = confusionMatrix();
            // true positive, true negative, false positive, false negative
            out.println(confusionMatrix[0] + " " + confusionMatrix[1] + " " + confusionMatrix[2] + " " + confusionMatrix[3]);
            // Print the expected and actual classifications
            for (Input input : classifications.keySet()) {
                // scene id, obj id, really this label, classified whether this label
                out.println(input.scene + " " + input.object + " " + (input.label == label) + " " + classifications.get(input));
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

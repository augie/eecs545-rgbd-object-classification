package eecs545;

import libsvm.svm_node;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Augie
 */
public class Input {

    public static String HEADER;
    public final String[] values;
    public final int scene;
    public final int objectOrSegment;
    public final int[][][] rgbHist;
    public final double[][][] rgbHistProp;
    public int rgbHistPoints = 0;
    public int label;
    // Save svm features
    private svm_node[] svmFeatures;
    // Save weka instance
    private Instance instance;

    public Input(String row) {
        this.values = row.split(",");
        scene = Integer.valueOf(values[0]);
        objectOrSegment = Integer.valueOf(values[1]);
        rgbHist = new int[Utils.RGB_HIST_DIM_BINS][Utils.RGB_HIST_DIM_BINS][Utils.RGB_HIST_DIM_BINS];
        int index = 2;
        for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
            for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                    int val = Integer.valueOf(values[index++]);
                    rgbHist[rBin][gBin][bBin] = val;
                    rgbHistPoints += val;
                }
            }
        }
        rgbHistProp = new double[Utils.RGB_HIST_DIM_BINS][Utils.RGB_HIST_DIM_BINS][Utils.RGB_HIST_DIM_BINS];
        for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
            for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                    rgbHistProp[rBin][gBin][bBin] = (double) rgbHist[rBin][gBin][bBin] / (double) rgbHistPoints;
                }
            }
        }
        label = Integer.valueOf(values[values.length - 1]);
    }

    public static void setHeader(String header) {
        HEADER = header;
    }

    public final svm_node[] getSVMFeatures() {
        if (svmFeatures == null) {
            // rgb hist
            svmFeatures = new svm_node[(int) Math.pow(Utils.RGB_HIST_DIM_BINS, 3)];
            int index = 1;
            for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
                for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                    for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                        svm_node feat = new svm_node();
                        feat.index = index;
                        feat.value = rgbHistProp[rBin][gBin][bBin];
                        svmFeatures[index - 1] = feat;
                        index++;
                    }
                }
            }
        }
        return svmFeatures;
    }

    public final Instance getInstance(Instances instances) {
        if (instance == null) {
            instance = new Instance((int) Math.pow(Utils.RGB_HIST_DIM_BINS, 3) + 1);
            int index = 0;
            for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
                for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                    for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                        instance.setValue(index++, (double) rgbHist[rBin][gBin][bBin]);
                    }
                }
            }
            instance.setDataset(instances);
            if (instances != null) {
                instance.setClassValue(String.valueOf(label));
            }
        }
        instance.setDataset(instances);
        return instance;
    }
}

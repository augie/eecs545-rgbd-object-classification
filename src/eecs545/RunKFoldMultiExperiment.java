package eecs545;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Augie
 */
public class RunKFoldMultiExperiment {

    public static final int K = 4;
    public static final int REPETITIONS = 1;
    public static final int MIN_TRAINING_EXAMPLES = 25;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("Expecting 2 args: [preprocessed input CSV file] [results directory]");
        }
        int argCount = 0;

        File inFile = new File(args[argCount++]);
        if (!inFile.exists()) {
            throw new Exception("Input file does not exist.");
        }

        File outDir = new File(args[argCount++]);
        if (!outDir.exists() && !outDir.mkdirs()) {
            throw new Exception("Output directory could not be created.");
        }

        // Read in the inputs
        List<Input> inputs = new LinkedList<Input>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(FileUtils.openInputStream(inFile)));
            // Header
            Input.HEADER = in.readLine().trim();
            // Read in all of the inputs
            String row = null;
            while ((row = in.readLine()) != null) {
                inputs.add(new Input(row));
            }
        } finally {
            IOUtils.closeQuietly(in);
        }
        
        // Labels for which to run tests
        List<Integer> testLabels = new LinkedList<Integer>();

        // How many objects are there of each label?
        Map<Integer, List<Input>> inputsByLabel = new HashMap<Integer, List<Input>>();
        for (Input input : inputs) {
            if (!inputsByLabel.containsKey(input.label)) {
                inputsByLabel.put(input.label, new LinkedList<Input>());
            }
            inputsByLabel.get(input.label).add(input);
        }
        // Print out the # of objects assigned each label
        System.out.println("# objects per label (at least " + MIN_TRAINING_EXAMPLES + " examples):");
        for (int label : inputsByLabel.keySet()) {
            int count = inputsByLabel.get(label).size();
            if (count >= MIN_TRAINING_EXAMPLES && label != 0) {
                testLabels.add(label);
                System.out.println(" " + label + ": " + count);
            }
        }
        System.out.println();

        // Set the class number of everything we don't care about to 0
        for (Input input : inputs) {
            if (!testLabels.contains(input.label)) {
                input.label = 0;
            }
        }

//        for (int clusters : new int[]{225, 250, 275, 250, 275, 300, 350, 400, 450, 500, 550}) {
            // Repeat the experiment multiple times
            for (int i = 0; i < REPETITIONS; i++) {
                // Divide up the inputs K times
                List<List<Input>> trainingSets = new LinkedList<List<Input>>();
                List<List<Input>> testingSets = new LinkedList<List<Input>>();
                for (int k = 0; k < K; k++) {
                    trainingSets.add(new LinkedList<Input>());
                    testingSets.add(new LinkedList<Input>());
                    int index = 0;
                    for (Input input : inputs) {
                        if (index % K == k) {
                            testingSets.get(k).add(input);
                        } else {
                            trainingSets.get(k).add(input);
                        }
                        index++;
                    }
                }

                MultiResults totalResults = new MultiResults();
//                File outFile = new File(outDir, "K" + clusters + "_r" + i + ".txt");
                File outFile = new File(outDir, "r" + i + ".txt");
//                if (outFile.exists()) {
//                    System.out.println("Skipping");
//                    continue;
//                }
                for (int k = 0; k < K; k++) {
                    // Choose a classifier to run
//                    MultiClassifier c = new MultiKMeansClassifier(clusters);
                    MultiClassifier c = new MultiSVMClassifier();
                    // Train the classifier
                    c.train(trainingSets.get(k));
                    // Test the classifier
                    totalResults.combine(c.test(testingSets.get(k)));
                }
                for (int label : testLabels) {
                    double[] confusion = totalResults.confusionMatrix(label);
                    System.out.println(" " + label + ":   " + confusion[0] + " " + confusion[1] + " " + confusion[2] + " " + confusion[3] + " (P: " + totalResults.precision(label) + ", R: " + totalResults.recall(label) + ", F: " + totalResults.F(label) + ")");
                }
                System.out.println(" T:   (P: " + totalResults.precision() + ", R: " + totalResults.recall() + ", F: " + totalResults.F() + ")");
                // Write out the results of this training/testing
                totalResults.write(outFile);
            }
//        }
    }
}

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
public class RunKFoldBinaryExperiment {

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

        // Labels for which to run tests
        List<Integer> testLabels = new LinkedList<Integer>();

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

        // K-fold cross validation for each label
        for (int label : testLabels) {
            System.out.println("Label " + label);
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

                BinaryResults totalResults = new BinaryResults(label);
                File outFile = new File(outDir, "l" + label + "_r" + i + ".txt");
                if (outFile.exists()) {
                    System.out.println("Skipping");
                    continue;
                }
                for (int k = 0; k < K; k++) {
                    // Choose a classifier to run
                    BinaryClassifier c = new BinarySVMClassifier(label);
                    // Train the classifier
                    c.train(trainingSets.get(k));
                    // Test the classifier
                    totalResults.combine(c.test(testingSets.get(k)));
                }
                // Write out the results of this training/testing
                totalResults.write(outFile);
            }
        }
    }
}

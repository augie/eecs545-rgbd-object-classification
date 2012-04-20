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
public class RunLOOBinaryExperiment {

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

        // leave-one-out cross validation for each of the test labels
        for (int label : testLabels) {
            System.out.println("Label " + label);
            // Repeat the experiment multiple times
            for (int i = 0; i < REPETITIONS; i++) {
                // # of iterations = inputs size
                BinaryResults totalResults = new BinaryResults(label);
                File outFile = new File(outDir, "l" + label + "_r" + i + ".txt");
                if (outFile.exists()) {
                    System.out.println("Skipping");
                    continue;
                }
                for (int it = 0; it < inputs.size(); it++) {
//                    // Choose a classifier to run
//                    Classifier c = new RandomGuessClassifier(label);
//                    Classifier c = new KNNClassifier(label, 1);
                    BinaryClassifier c = new EnsembleBinarySVMClassifier(label);
                    // Copy the inputs list
                    List<Input> trainingSet = new LinkedList<Input>();
                    trainingSet.addAll(inputs);
                    // Leave the it'th input out
                    Input leaveOut = trainingSet.remove(it);
                    // Train the classifier
                    c.train(trainingSet);
                    // Test on the it'th input
                    List<Input> testingSet = new LinkedList<Input>();
                    testingSet.add(leaveOut);
                    totalResults.combine(c.test(testingSet));
                }
                // Write out the results of this training/testing
                totalResults.write(outFile);
            }
        }
    }
}

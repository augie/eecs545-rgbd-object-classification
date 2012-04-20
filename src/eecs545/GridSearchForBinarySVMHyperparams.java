package eecs545;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_print_interface;
import libsvm.svm_problem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Augie
 */
public class GridSearchForBinarySVMHyperparams {

    public static final int K = 4;

    public static void main(String[] args) throws Exception {
//        if (args.length != 1) {
//            throw new Exception("Expecting 1 args: [preprocessed input CSV file]");
//        }
//        int argCount = 0;
//
//        File inFile = new File(args[argCount++]);
//        if (!inFile.exists()) {
//            throw new Exception("Input file does not exist.");
//        }
//
//        // Read in the inputs
//        List<Input> inputs = new LinkedList<Input>();
//        BufferedReader in = null;
//        try {
//            in = new BufferedReader(new InputStreamReader(FileUtils.openInputStream(inFile)));
//            // Header
//            Input.HEADER = in.readLine().trim();
//            // Read in all of the inputs
//            String row = null;
//            while ((row = in.readLine()) != null) {
//                inputs.add(new Input(row));
//            }
//        } finally {
//            IOUtils.closeQuietly(in);
//        }
//
//        // Divide up the inputs K times
//        List<List<Input>> trainingSets = new LinkedList<List<Input>>();
//        List<List<Input>> testingSets = new LinkedList<List<Input>>();
//        for (int k = 0; k < K; k++) {
//            trainingSets.add(new LinkedList<Input>());
//            testingSets.add(new LinkedList<Input>());
//            int index = 0;
//            for (Input input : inputs) {
//                if (index % K == k) {
//                    testingSets.get(k).add(input);
//                } else {
//                    trainingSets.get(k).add(input);
//                }
//                index++;
//            }
//        }
//
//        // Search for the weights which classify the full data set best
//        int[] weight_label = new int[]{-1, 1};
//        double[] weight = new double[]{1, 1};
//        for (double C : new double[]{6, 8, 10, 12, 14, 16}) {
//            for (double gamma : new double[]{6, 8, 10, 12, 14, 16}) {
//
//                MultiResults totalResults = new MultiResults();
//                // K-fold for every label
//                for (int )
//                for (int k = 0; k < K; k++) {
//                    svm_problem problem = new svm_problem();
//                    // l appears to be the number of training samples
//                    problem.l = trainingSets.get(k).size();
//                    // The features of each sample
//                    ArrayList<svm_node[]> svmTrainingSamples = new ArrayList<svm_node[]>();
//                    double[] labels = new double[trainingSets.get(k).size()];
//                    int count = 0;
//                    for (Input input : trainingSets.get(k)) {
//                        svmTrainingSamples.add(input.getSVMFeatures());
//                        labels[count++] = input.label;
//                    }
//                    problem.x = svmTrainingSamples.toArray(new svm_node[0][]);
//                    // y is probably the labels to each sample
//                    problem.y = labels;
//
//                    // Set the training parameters
//                    svm_parameter param = new svm_parameter();
//                    // SVM Type
//                    //  0 -- C-SVC (classification)
//                    //  1 -- nu-SVC (classification)
//                    //  2 -- one-class SVM
//                    //  3 -- epsilon-SVR (regression)
//                    //  4 -- nu-SVR (regression)
//                    param.svm_type = svm_parameter.C_SVC;
//                    // Other C-SVC parameters:
//                    // param.weight[] : sets the parameter C of class i to weight*c (default 1)
//                    // param.C : cost, set the parameter C of C-SVC (default 1)
//                    // Type of kernel
//                    //  0 -- linear: u'*v
//                    //  1 -- polynomial: (gamma*u'*v + coef0)^degree
//                    //  2 -- radial basis function: exp(-gamma*|u-v|^2)
//                    //  3 -- sigmoid: tanh(gamma*u'*v + coef0)
//                    param.kernel_type = svm_parameter.RBF;
//                    // Kernel gamma
//                    param.gamma = gamma;
//
//                    // Degree for poly
//                    param.degree = 3;
//                    // Coefficient for poly/sigmoid
//                    param.coef0 = 1;
//                    // Make larger for faster training
//                    param.cache_size = 850;
//                    // Stopping criteria (make larger for shorter training times)
//                    param.eps = 0.05;
//                    // Cost
//                    param.C = C;
//                    // Number of label weights
//                    param.nr_weight = weight_label.length;
//                    // Weight labels
//                    param.weight_label = weight_label;
//                    param.weight = weight;
//                    // Nu
//                    param.nu = 0.5;
//                    // p
//                    param.p = 0.1;
//                    // Shrinking heuristic (1 makes for shorter training time)
//                    param.shrinking = 1;
//                    // Estimate probabilities
//                    param.probability = 0;
//
//                    // Train the SVM classifier
//                    svm.svm_set_print_string_function(new svm_print_interface() {
//
//                        @Override
//                        public void print(String s) {
//                        }
//                    });
//                    svm_model model = svm.svm_train(problem, param);
//
//                    // Test against the same inputs
//                    MultiResults results = new MultiResults();
//                    for (Input unknown : testingSets.get(k)) {
//                        int prediction = (int) svm.svm_predict(model, unknown.getSVMFeatures());
//                        results.add(unknown, prediction);
//                    }
//                    totalResults.combine(results);
//                }
//                // How bad is it?
//                double[] confusion = totalResults.confusionMatrix(1);
//                System.out.println(C + " " + gamma + " " + confusion[0] + " " + confusion[1] + " " + confusion[2] + " " + confusion[3]);
//            }
//        }
    }
}
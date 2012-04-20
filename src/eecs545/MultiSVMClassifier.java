package eecs545;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_print_interface;
import libsvm.svm_problem;

/**
 *
 * @author Augie
 */
public class MultiSVMClassifier extends MultiClassifier {

    private svm_model model;

    @Override
    public void train(List<Input> inputs) throws Exception {
        svm_problem problem = new svm_problem();
        // l appears to be the number of training samples
        problem.l = inputs.size();
        // The features of each sample
        ArrayList<svm_node[]> svmTrainingSamples = new ArrayList<svm_node[]>();
        Map<Integer, Integer> labelCounts = new HashMap<Integer, Integer>();
        double[] labels = new double[inputs.size()];
        int count = 0;
        for (Input input : inputs) {
            svmTrainingSamples.add(input.getSVMFeatures());
            labels[count++] = input.label;
            if (!labelCounts.containsKey(input.label)) {
                labelCounts.put(input.label, 1);
            } else {
                labelCounts.put(input.label, labelCounts.get(input.label) + 1);
            }
        }
        problem.x = svmTrainingSamples.toArray(new svm_node[0][]);
        // y is probably the labels to each sample
        problem.y = labels;

        // Set the training parameters
        svm_parameter param = new svm_parameter();
        // SVM Type
        //  0 -- C-SVC (classification)
        //  1 -- nu-SVC (classification)
        //  2 -- one-class SVM
        //  3 -- epsilon-SVR (regression)
        //  4 -- nu-SVR (regression)
        param.svm_type = svm_parameter.C_SVC;
        // Other C-SVC parameters:
        // param.weight[] : sets the parameter C of class i to weight*c (default 1)
        // param.C : cost, set the parameter C of C-SVC (default 1)
        // Type of kernel
        //  0 -- linear: u'*v
        //  1 -- polynomial: (gamma*u'*v + coef0)^degree
        //  2 -- radial basis function: exp(-gamma*|u-v|^2)
        //  3 -- sigmoid: tanh(gamma*u'*v + coef0)
        param.kernel_type = svm_parameter.RBF;
        // Cost
        param.C = 18;
        // Kernel gamma
        param.gamma = 5;

        // Degree for poly
        param.degree = 3;
        // Coefficient for poly/sigmoid
        param.coef0 = 1;
        // Make larger for faster training
        param.cache_size = 900;
        // Stopping criteria
        param.eps = 0.0001;
        // Number of label weights
        param.nr_weight = labelCounts.size();
        // Weight labels
        // Search for the weights which classify the full data set best
        param.weight_label = new int[labelCounts.size()];
        {
            int weightLabelCount = 0;
            for (int label : labelCounts.keySet()) {
                param.weight_label[weightLabelCount++] = label;
            }
        }
        param.weight = new double[labelCounts.size()];
        Arrays.fill(param.weight, 1);
        // Nu
        param.nu = 0.5;
        // p
        param.p = 0.1;
        // Shrinking heuristic
        param.shrinking = 1;
        // Estimate probabilities
        param.probability = 0;

        // Train the SVM classifier
        svm.svm_set_print_string_function(new svm_print_interface() {

            @Override
            public void print(String s) {
            }
        });
        model = svm.svm_train(problem, param);
    }

    @Override
    public MultiResults test(List<Input> inputs) throws Exception {
        MultiResults results = new MultiResults();
        for (Input unknown : inputs) {
            int prediction = (int) svm.svm_predict(model, unknown.getSVMFeatures());
            results.add(unknown, prediction);
        }
        return results;
    }
}

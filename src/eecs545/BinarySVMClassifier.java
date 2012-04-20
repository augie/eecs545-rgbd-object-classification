package eecs545;

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
public class BinarySVMClassifier extends BinaryClassifier {

    private svm_model model;

    public BinarySVMClassifier(int label) {
        super(label);
    }

    @Override
    public void train(List<Input> inputs) throws Exception {
        // What is the balance of the data set?
        Map<Integer, Integer> labelCounts = new HashMap<Integer, Integer>();
        for (Input input : inputs) {
            int thisLabel = (input.label == label ? 1 : -1);
            if (!labelCounts.containsKey(thisLabel)) {
                labelCounts.put(thisLabel, 1);
            } else {
                labelCounts.put(thisLabel, labelCounts.get(thisLabel) + 1);
            }
        }

        svm_problem problem = new svm_problem();
        // l appears to be the number of training samples
        problem.l = inputs.size();
        // The features of each sample
        svm_node[][] svmTrainingSamples = new svm_node[problem.l][];
        double[] labels = new double[problem.l];
        int count = 0;
        for (Input input : inputs) {
            svmTrainingSamples[count] = input.getSVMFeatures();
            labels[count++] = (input.label == label ? 1 : -1);
        }
        problem.x = svmTrainingSamples;
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
        param.C = 16;
        // Kernel gamma
        param.gamma = 5;
        // Degree for poly
        param.degree = 3;
        // Coefficient for poly/sigmoid
        param.coef0 = 0.1;
        // Stopping criteria
        param.eps = 0.01;
        // Make larger for faster training
        param.cache_size = 850;
        // Number of label weights
        param.nr_weight = 2;
        // Weight labels
        param.weight_label = new int[]{1, -1};
        param.weight = new double[]{1, 1};
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
    public BinaryResults test(List<Input> inputs) throws Exception {
        BinaryResults results = new BinaryResults(label);
        for (Input unknown : inputs) {
            int prediction = (int) svm.svm_predict(model, unknown.getSVMFeatures());
            results.add(unknown, prediction == 1);
        }
        double[] confusion = results.confusionMatrix();
        System.out.println(confusion[0] + " " + confusion[1] + " " + confusion[2] + " " + confusion[3]);
        return results;
    }
}

package eecs545;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;

/**
 *
 * @author Augie
 */
public class MultiDecisionTreeClassifier extends MultiClassifier {

    private Instances instances;
    private FastVector classAttributeVals;
    private J48 tree;

    @Override
    public void train(List<Input> train) throws Exception {
        // Can't be empty
        if (train.isEmpty()) {
            throw new Exception("Training set is empty.");
        }
        // Get the training data
        FastVector attribs = new FastVector((int) Math.pow(Utils.RGB_HIST_DIM_BINS, 3) + 1);
        for (int i = 0; i < Math.pow(Utils.RGB_HIST_DIM_BINS, 3); i++) {
            Attribute binAttr = new Attribute("bin" + i);
            attribs.addElement(binAttr);
        }
        Set<Integer> labels = new HashSet<Integer>();
        for (Input input : train) {
            labels.add(input.label);
        }
        classAttributeVals = new FastVector();
        for (int label : labels) {
            classAttributeVals.addElement(String.valueOf(label));
        }
        attribs.addElement(new Attribute("label", classAttributeVals));
        instances = new Instances("Training", attribs, train.size());
        instances.setClassIndex(attribs.size() - 1);
        for (Input input : train) {
            instances.add(input.getInstance(instances));
        }
        // Train the classifier
        tree = new J48();
        // Lowering decreases the amount of post-pruning
        tree.setConfidenceFactor(0.05f);
        // The minimum number of elements in a leave node
        tree.setMinNumObj(2);
        tree.buildClassifier(instances);
    }

    @Override
    public MultiResults test(List<Input> test) throws Exception {
        MultiResults results = new MultiResults();
        for (Input unknown : test) {
            int predictedLabelIndex = (int) tree.classifyInstance(unknown.getInstance(instances));
            int predictedLabel = Integer.valueOf((String) classAttributeVals.elementAt(predictedLabelIndex));
            System.out.println(predictedLabel + " / " + unknown.label);
            results.add(unknown, predictedLabel);
        }
        return results;
    }
}

package eecs545;

import java.util.List;

/**
 *
 * @author Augie
 */
public abstract class BinaryClassifier extends Classifier {

    public int label;

    public BinaryClassifier(int label) {
        this.label = label;
    }
    
    public abstract BinaryResults test(List<Input> inputs) throws Exception;
}

package eecs545;

import java.util.List;

/**
 *
 * @author Augie
 */
public abstract class MultiClassifier extends Classifier {
    
    public abstract MultiResults test(List<Input> inputs) throws Exception;
}

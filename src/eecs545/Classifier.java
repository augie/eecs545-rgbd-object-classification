package eecs545;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Augie
 */
public abstract class Classifier {

    public int label;

    public Classifier(int label) {
        this.label = label;
    }

    public abstract void train(Collection<Input> inputs);

    public abstract Results test(Collection<Input> inputs);
}

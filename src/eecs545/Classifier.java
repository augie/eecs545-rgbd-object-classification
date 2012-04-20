package eecs545;

import java.util.List;

/**
 *
 * @author Augie
 */
public abstract class Classifier {

    public abstract void train(List<Input> inputs) throws Exception;
}

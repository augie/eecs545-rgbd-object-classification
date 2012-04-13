package eecs545;

import java.util.Collection;

/**
 * 
 * @author Augie
 */
public class RandomGuessClassifier extends Classifier {

    public RandomGuessClassifier(int label) {
        super(label);
    }

    @Override
    public void train(Collection<Input> inputs) {
        // No training here
    }

    @Override
    public Results test(Collection<Input> inputs) {
        Results results = new Results(label);
        for (Input input : inputs) {
            results.add(input, MathUtils.RANDOM.nextBoolean());
        }
        return results;
    }
}

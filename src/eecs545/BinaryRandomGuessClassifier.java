package eecs545;

import java.util.List;

/**
 * 
 * @author Augie
 */
public class BinaryRandomGuessClassifier extends BinaryClassifier {

    public BinaryRandomGuessClassifier(int label) {
        super(label);
    }

    @Override
    public void train(List<Input> inputs) throws Exception {
        // No training here
    }

    @Override
    public BinaryResults test(List<Input> inputs) throws Exception {
        BinaryResults results = new BinaryResults(label);
        for (Input input : inputs) {
            results.add(input, MathUtils.RANDOM.nextBoolean());
        }
        return results;
    }
}

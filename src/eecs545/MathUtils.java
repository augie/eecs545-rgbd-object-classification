package eecs545;

import java.util.Arrays;
import java.util.Random;
import org.apache.commons.math3.analysis.function.Sigmoid;

/**
 *
 * @author Augie
 */
public class MathUtils {

    public static final Random RANDOM = new Random();
    public static final Sigmoid SIGMOID = new Sigmoid();

    public static double[][] flipHorizontalAndVertical(double[][] m) {
        // Check null;
        if (m == null) {
            return null;
        }
        // Flip
        double[][] temp = new double[m.length][];
        for (int i = m.length - 1, row = 0; i >= 0; i--, row++) {
            double[] temp2 = new double[m[i].length];
            for (int j = m[i].length - 1, col = 0; j >= 0; j--, col++) {
                temp2[col] = m[i][j];
            }
            temp[row] = temp2;
        }
        return temp;
    }

    public static void fill(double[][] m, double v) {
        for (int i = 0; i < m.length; i++) {
            Arrays.fill(m[i], v);
        }
    }

    public static void copy(double[][] from, double[][] to) {
        for (int i = 0; i < from.length; i++) {
            for (int j = 0; j < from[i].length; j++) {
                to[i][j] = from[i][j];
            }
        }
    }

    public static double[][] convolutionBig(double[][] input, int inputWidth, int inputHeight, double[][] kernel, int kernelWidth, int kernelHeight) {
        int outputWidth = inputWidth + kernelWidth - 1;
        int outputHeight = inputHeight + kernelHeight - 1;
        double[][] output = new double[outputWidth][outputHeight];
        fill(output, 0);
        for (int x = 0; x < outputWidth; x++) {
            for (int y = 0; y < outputHeight; y++) {
                for (int i = 0; i < kernelWidth; i++) {
                    for (int j = 0; j < kernelHeight; j++) {
                        double value = 0;
                        if (x + i < inputWidth && y + j < inputHeight) {
                            value = input[x + i][y + j];
                        }
                        output[x][y] += value * kernel[i][j];
                    }
                }
            }
        }
        return output;
    }

    public static double[][] convolutionSmall(double[][] input, int inputWidth, int inputHeight, double[][] kernel, int kernelWidth, int kernelHeight) {
        int outputWidth = inputWidth - kernelWidth + 1;
        int outputHeight = inputHeight - kernelHeight + 1;
        double[][] output = new double[outputWidth][outputHeight];
        fill(output, 0);
        for (int x = 0; x < outputWidth; x++) {
            for (int y = 0; y < outputHeight; y++) {
                for (int i = 0; i < kernelWidth; i++) {
                    for (int j = 0; j < kernelHeight; j++) {
                        output[i][j] += input[x + i][y + j] * kernel[i][j];
                    }
                }
            }
        }
        return output;
    }
}

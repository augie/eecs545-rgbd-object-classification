package eecs545;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Augie
 */
public class LibSVMUtils {

    public static svm_model read(File file) throws IOException, NullPointerException {
        // Check for null
        if (file == null) {
            throw new NullPointerException("File is null.");
        }
        // Check for file existence
        if (!file.exists()) {
            throw new IOException("Output file does not exist.");
        }
        // Open a stream to read the file
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(FileUtils.openInputStream(file)));
            // Create the base model
            svm_model model = new svm_model();
            // Read the nodes
            {
                int outerLength = Integer.valueOf(in.readLine());
                ArrayList<svm_node[]> outerArr = new ArrayList<svm_node[]>();
                for (int i = 0; i < outerLength; i++) {
                    int innerLength = Integer.valueOf(in.readLine());
                    svm_node[] innerArr = new svm_node[innerLength];
                    for (int j = 0; j < innerLength; j++) {
                        svm_node node = new svm_node();
                        node.index = Integer.valueOf(in.readLine());
                        node.value = Double.valueOf(in.readLine());
                        innerArr[j] = node;
                    }
                    outerArr.add(innerArr);
                }
                model.SV = outerArr.toArray(new svm_node[0][0]);
            }
            // Read l
            model.l = Integer.valueOf(in.readLine());
            // Read label
            {
                int[] label = new int[Integer.valueOf(in.readLine())];
                for (int i = 0; i < label.length; i++) {
                    label[i] = Integer.valueOf(in.readLine());
                }
                model.label = label;
            }
            // Read nSV
            {
                int[] nSV = new int[Integer.valueOf(in.readLine())];
                for (int i = 0; i < nSV.length; i++) {
                    nSV[i] = Integer.valueOf(in.readLine());
                }
                model.nSV = nSV;
            }
            // Read nr class
            model.nr_class = Integer.valueOf(in.readLine());
            // Read the svm parameters
            {
                svm_parameter param = new svm_parameter();
                param.C = Double.valueOf(in.readLine());
                param.cache_size = Double.valueOf(in.readLine());
                param.coef0 = Double.valueOf(in.readLine());
                param.degree = Integer.valueOf(in.readLine());
                param.eps = Double.valueOf(in.readLine());
                param.gamma = Double.valueOf(in.readLine());
                param.kernel_type = Integer.valueOf(in.readLine());
                param.nr_weight = Integer.valueOf(in.readLine());
                param.nu = Double.valueOf(in.readLine());
                param.p = Double.valueOf(in.readLine());
                param.probability = Integer.valueOf(in.readLine());
                param.shrinking = Integer.valueOf(in.readLine());
                param.svm_type = Integer.valueOf(in.readLine());
                {
                    double[] weight = new double[Integer.valueOf(in.readLine())];
                    for (int i = 0; i < weight.length; i++) {
                        weight[i] = Double.valueOf(in.readLine());
                    }
                    param.weight = weight;
                }
                {
                    int[] weight_label = new int[Integer.valueOf(in.readLine())];
                    for (int i = 0; i < weight_label.length; i++) {
                        weight_label[i] = Integer.valueOf(in.readLine());
                    }
                    param.weight_label = weight_label;
                }
                model.param = param;
            }
            // Read probA
            {
                double[] probA = new double[Integer.valueOf(in.readLine())];
                for (int i = 0; i < probA.length; i++) {
                    probA[i] = Integer.valueOf(in.readLine());
                }
                model.probA = probA;
            }
            // Read probB
            {
                double[] probB = new double[Integer.valueOf(in.readLine())];
                for (int i = 0; i < probB.length; i++) {
                    probB[i] = Integer.valueOf(in.readLine());
                }
                model.probB = probB;
            }
            // Read rho
            {
                double[] rho = new double[Integer.valueOf(in.readLine())];
                for (int i = 0; i < rho.length; i++) {
                    rho[i] = Integer.valueOf(in.readLine());
                }
                model.rho = rho;
            }
            // Read the SV coefficients
            {
                int outerLength = Integer.valueOf(in.readLine());
                ArrayList<double[]> outerArr = new ArrayList<double[]>();
                for (int i = 0; i < outerLength; i++) {
                    int innerLength = Integer.valueOf(in.readLine());
                    double[] innerArr = new double[innerLength];
                    for (int j = 0; j < innerLength; j++) {
                        innerArr[j] = Double.valueOf(in.readLine());
                    }
                    outerArr.add(innerArr);
                }
                model.sv_coef = outerArr.toArray(new double[0][0]);
            }
            // Output
            return model;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static void write(svm_model model, File file) throws IOException, NullPointerException {
        // Check for null
        if (model == null) {
            throw new NullPointerException("SVM model is null.");
        }
        if (file == null) {
            throw new NullPointerException("Output file is null.");
        }
        // Check for file existence
        if (file.exists()) {
            throw new IOException("Output file already exists.");
        }
        // Make parent directory if doesn't exist
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IOException("Could not create output file parent directory.");
        }
        // Open a stream to the file
        PrintStream out = null;
        try {
            out = new PrintStream(FileUtils.openOutputStream(file));
            // Write the nodes
            out.println(model.SV.length);
            for (svm_node[] nodes : model.SV) {
                out.println(nodes.length);
                for (svm_node node : nodes) {
                    out.println(node.index);
                    out.println(node.value);
                }
            }
            // Write l
            out.println(model.l);
            // Write label
            out.println(model.label.length);
            for (int l : model.label) {
                out.println(l);
            }
            // Write nSV
            out.println(model.nSV.length);
            for (int n : model.nSV) {
                out.println(n);
            }
            // Write nr class
            out.println(model.nr_class);
            // Write the svm parameters
            {
                out.println(model.param.C);
                out.println(model.param.cache_size);
                out.println(model.param.coef0);
                out.println(model.param.degree);
                out.println(model.param.eps);
                out.println(model.param.gamma);
                out.println(model.param.kernel_type);
                out.println(model.param.nr_weight);
                out.println(model.param.nu);
                out.println(model.param.p);
                out.println(model.param.probability);
                out.println(model.param.shrinking);
                out.println(model.param.svm_type);
                out.println(model.param.weight.length);
                for (double w : model.param.weight) {
                    out.println(w);
                }
                out.println(model.param.weight_label.length);
                for (int l : model.param.weight_label) {
                    out.println(l);
                }
            }
            // Wrrite the probA's
            out.println(model.probA.length);
            for (double a : model.probA) {
                out.println(a);
            }
            // Wrrite the probB's
            out.println(model.probB.length);
            for (double b : model.probB) {
                out.println(b);
            }
            // Wrrite the rho's
            out.println(model.rho.length);
            for (double r : model.rho) {
                out.println(r);
            }
            // Write the SV coefficients
            out.println(model.sv_coef.length);
            for (double[] svcs : model.sv_coef) {
                out.println(svcs.length);
                for (double c : svcs) {
                    out.println(c);
                }
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}

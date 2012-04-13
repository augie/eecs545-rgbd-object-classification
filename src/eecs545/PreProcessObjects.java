package eecs545;

import java.io.File;
import java.io.PrintStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Augie
 */
public class PreProcessObjects {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("Expecting 2 args: [directory of object file directories] [output CSV file]");
        }
        int argCount = 0;

        File inDir = new File(args[argCount++]);
        if (!inDir.exists()) {
            throw new Exception("Input directory does not exist.");
        }

        File outFile = new File(args[argCount++]);
        if (outFile.exists()) {
            throw new Exception("Output file already exists.");
        }

        // Open a stream to the output file
        PrintStream out = null;
        try {
            out = new PrintStream(FileUtils.openOutputStream(outFile));
            // Header
            out.println("scene,object,label");
            // Each label
            for (String labelDirName : inDir.list()) {
                File labelDir = new File(inDir, labelDirName);
                // Each object in each label dir
                for (String objFileName : labelDir.list()) {
                    String[] objFileNameSplit = objFileName.split("_");
                    // Identifying information
                    String scene = objFileNameSplit[0].replace("scene", "");
                    String object = "1";
                    
                    // Read in the point cloud representing the object
                    File objFile = new File(labelDir, objFileName);
                    PointCloud objPointCloud = new PointCloud(objFile);
                    
                    // Write out the row
                    out.println(scene + "," + object + "," + labelDirName);
                }
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}

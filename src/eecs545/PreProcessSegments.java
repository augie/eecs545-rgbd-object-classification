package eecs545;

import java.io.File;
import java.io.PrintStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Augie
 */
public class PreProcessSegments {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("Expecting 2 args: [directory of segment file directories] [output CSV file]");
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
            String header = "scene,segment";
            for (int rgbHistBinID = 0; rgbHistBinID < Math.pow(Utils.RGB_HIST_DIM_BINS, 3); rgbHistBinID++) {
                header += ",h" + rgbHistBinID;
            }
            header += ",label";
            out.println(header);
            // Each label
            for (String labelDirName : inDir.list()) {
                // We do not care about the '0' label
                if (labelDirName.equals("0")) {
                    continue;
                }
                File labelDir = new File(inDir, labelDirName);
                // Each object in each label dir
                for (String objFileName : labelDir.list()) {
                    String[] objFileNameSplit = objFileName.split("_");
                    // Identifying information
                    String scene = objFileNameSplit[0].replace("scene", "");
                    String segment = objFileNameSplit[2].replace("s", "").replace(".ply", "");

                    // Read in the point cloud representing the object
                    File objFile = new File(labelDir, objFileName);
                    PointCloud objPointCloud = new PointCloud(objFile);

                    // Compute a histogram of the RGB values for the segment
                    int[][][] rgbHist = new int[Utils.RGB_HIST_DIM_BINS][Utils.RGB_HIST_DIM_BINS][Utils.RGB_HIST_DIM_BINS];
                    // Everything starts with 1 value (expect computation problems if 0 value in hists)
                    for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
                        for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                            for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                                rgbHist[rBin][gBin][bBin] = 1;
                            }
                        }
                    }
                    // Accumulate the histogram
                    for (Point p : objPointCloud.points) {
                        int rBin = (int) Math.floor(p.r / Utils.RGB_HIST_BID_SIZE);
                        int gBin = (int) Math.floor(p.g / Utils.RGB_HIST_BID_SIZE);
                        int bBin = (int) Math.floor(p.b / Utils.RGB_HIST_BID_SIZE);
                        rgbHist[rBin][gBin][bBin]++;
                    }

                    // Write out the row
                    String row = scene + "," + segment;
                    for (int rBin = 0; rBin < Utils.RGB_HIST_DIM_BINS; rBin++) {
                        for (int gBin = 0; gBin < Utils.RGB_HIST_DIM_BINS; gBin++) {
                            for (int bBin = 0; bBin < Utils.RGB_HIST_DIM_BINS; bBin++) {
                                row += "," + rgbHist[rBin][gBin][bBin];
                            }
                        }
                    }
                    row += "," + labelDirName;
                    out.println(row);
                }
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}

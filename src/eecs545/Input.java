package eecs545;

/**
 *
 * @author Augie
 */
public class Input {

    public static String HEADER;
    public final String[] values;
    public final int scene;
    public final int object;
    public final int label;

    public Input(String row) {
        this.values = row.split(",");
        scene = Integer.valueOf(values[0]);
        object = Integer.valueOf(values[1]);
        label = Integer.valueOf(values[values.length - 1]);
    }

    public static void setHeader(String header) {
        HEADER = header;
    }
}

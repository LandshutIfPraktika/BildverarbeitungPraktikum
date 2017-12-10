package ransac;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.util.ArrayList;

public class Line_Ransac implements PlugInFilter {


    private int width;
    private int height;
    private byte[] pixels;

    @Override
    public int setup(String arg, ImagePlus imp) {
        return 0;
    }

    @Override
    public void run(ImageProcessor ip) {

        ip.autoThreshold();
        width = ip.getWidth();
        height = ip.getHeight();
        pixels = (byte[]) ip.getPixels();


    }


    public void ransac() {
    }


    private static class Model {
        private final int value;
        private final ArrayList<Point> points;
        private final DrawingAction drawingAction;

        private Model(int value, ArrayList<Point> points, DrawingAction drawingAction) {
            this.value = value;
            this.points = points;
            this.drawingAction = drawingAction;
        }
    }

    private static class Point {
        private final int row;
        private final int column;

        private Point(int row, int column) {
            this.row = row;
            this.column = column;
        }
    }

    @FunctionalInterface
    public interface DrawingAction {
        void draw(final ArrayList<Point> points, final ImageProcessor ip);
    }
}

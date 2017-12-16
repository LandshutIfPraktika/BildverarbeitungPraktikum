package ransac;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

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


    public Set<Point> getPoints(final byte threshold) {
        final Set<Point> points = new LinkedHashSet<>();
        for (int row = 0; row < height; row++) {
            int rowStart = row * width;
            for (int col = 0; col < width; col++) {
                if ((pixels[rowStart + col] & 0xff) == 0xff) {
                    points.add(new Point(row, col));
                }
            }
        }
        return points;
    }


    public void ransac(final Set<Point> points, final int m, final int k, final int d, final int v) {

        Stream.generate(new PointsSupplier(width, height, m))
                .limit(k)
                .map()
                .;

    }


    private static class Model {
        private int value;
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

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Point point = (Point) o;
            return row == point.row &&
                    column == point.column;
        }

        @Override
        public int hashCode() {

            return Objects.hash(row, column);
        }
    }

    @FunctionalInterface
    public interface DrawingAction {
        void draw(final ArrayList<Point> points, final ImageProcessor ip);
    }

    private class PointsSupplier implements Supplier<ArrayList<Point>> {

        private final Random random = new Random(System.currentTimeMillis());
        private final int width;
        private final int height;
        private final int m;

        public PointsSupplier(final int width, final int height, final int m) {

            this.width = width;
            this.height = height;
            this.m = m;
        }


        @Override
        public ArrayList<Point> get() {
            final ArrayList<Point> points = new ArrayList<>();

            for (int i = 0; i < m; i++) {
                points.add(new Point(random.nextInt(width), random.nextInt(height)));
            }

            return points;
        }
    }
}

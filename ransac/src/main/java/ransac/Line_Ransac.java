package ransac;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.awt.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Line_Ransac implements PlugInFilter {


    private int width;
    private int height;
    private byte[] pixels;
    private Overlay fP;

    @Override
    public int setup(String arg, ImagePlus imp) {
        return NO_UNDO | NO_CHANGES | DOES_8G;
    }

    @Override
    public void run(ImageProcessor ip) {

        ip.autoThreshold();
        width = ip.getWidth();
        height = ip.getHeight();
        pixels = (byte[]) ip.getPixels();

        Set<Point> points = getPoints();

        Model ransac = ransac(points, 2, 1000, 0.001, points.size() / 100);

        ransac.draw(ip);
    }


    public Set<Point> getPoints() {
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


    public Model ransac(final Set<Point> points, final int m, final int k, final double d, final int v) {
        Model model = Stream.generate(new PointsSupplier(points, m))
                .parallel()
                .map(ps -> {
                    Line line = getLine(ps);
                    Set<Point> containingPoints = getValue(points, line, d);
                    int value = containingPoints.size();
                    //System.err.println("Line:"+line +" value: " +value);

                    return new Model(value, ps, new LineDrawinAction(line), containingPoints);
                })
                .filter(m0 -> m0.value > v)
                .limit(k)
                .reduce((m1, m2) -> m1.value > m2.value ? m1 : m2).get();
        return model;

    }

    Set<Point> getValue(final Set<Point> points, final Line line, final double d) {

        final Set<Point> containingPoint = new HashSet<>();
        final double deltaSquare = d * d;

        for (Point point : points) {
            final double a = line.slope * point.column + (point.row) + line.offset;
            final double distanceSqared = (a * a) / (line.slope * line.slope + line.offset * line.offset);
            if (distanceSqared <= deltaSquare) {
                containingPoint.add(point);
            }
        }
        return containingPoint;
    }

    private Line getLine(final ArrayList<Point> ps) {
        final Point firstPoint = ps.get(0);
        final Point secondPoint = ps.get(1);

        final int dx = secondPoint.column - firstPoint.column;
        final int dy = -secondPoint.row + firstPoint.row;
        final double slope = (double) dy / (double) dx;
        final double offset = (double) ((secondPoint.column * -firstPoint.row) - (firstPoint.column * -secondPoint.row)) / (double) (dx);


        return new Line(slope, offset);
    }


    static class Line {
        final double slope;
        final double offset;

        Line(final double slope, final double offset) {
            this.slope = slope;
            this.offset = offset;
        }

        public double getSlope() {
            return slope;
        }

        public double getOffset() {
            return offset;
        }

        @Override
        public String toString() {
            return "Line{" +
                    "slope=" + slope +
                    ", offset=" + offset +
                    '}';
        }
    }

    static class Model {
        private int value;
        private final ArrayList<Point> points;
        private final DrawingAction drawingAction;
        private Set<Point> containingPoints;

        private Model(int value, ArrayList<Point> points, DrawingAction drawingAction, final Set<Point> containingPoints) {
            this.value = value;
            this.points = points;
            this.drawingAction = drawingAction;
            this.containingPoints = containingPoints;
        }

        public void draw(final ImageProcessor imageProcessor) {
            Overlay myOverlay = new Overlay();  // ij.gui.Overlay
            SimpleRegression simpleRegression = new SimpleRegression();
            for (Point containingPoint : containingPoints) {
                simpleRegression.addData(containingPoint.column, containingPoint.row);
                PointRoi roi = new PointRoi(containingPoint.column, containingPoint.row);
                roi.setFillColor(new Color(0, 255, 0));
                myOverlay.add(roi);
            }
            double intercept = simpleRegression.getIntercept();
            double slope = simpleRegression.getSlope();

            ij.gui.Line myLine = new ij.gui.Line(0, intercept, imageProcessor.getWidth(), imageProcessor.getWidth() * slope + intercept);  // ij.gui.Line
            myLine.setStrokeColor(new Color(255, 0, 0));
            myLine.setStrokeWidth(1);
            myOverlay.add(myLine);

            ImagePlus ddd = new ImagePlus("ddd", imageProcessor);
            ddd.setOverlay(myOverlay);
            ddd.show();
        }


    }

    static class Point {
        private final int row;
        private final int column;

        Point(int row, int column) {
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
        Overlay draw(final ArrayList<Point> points, final ImageProcessor ip);
    }

    private static class LineDrawinAction implements DrawingAction {

        private Line line;

        public LineDrawinAction(final Line line) {

            this.line = line;
        }

        @Override
        public Overlay draw(final ArrayList<Point> points, final ImageProcessor ip) {

            Point firstPoint = points.get(0);
            Point secondPoint = points.get(1);

            ij.gui.Line myLine = new ij.gui.Line(firstPoint.column, firstPoint.row, secondPoint.column, secondPoint.row);  // ij.gui.Line
            myLine.setStrokeColor(new Color(255, 0, 0));
            myLine.setStrokeWidth(2);
            Overlay myOverlay = new Overlay();  // ij.gui.Overlay
            myOverlay.add(myLine);

            return myOverlay;
            //fP.drawLine(firstPoint.column, firstPoint.row, secondPoint.column, secondPoint.row);
        }
    }

    class PointsSupplier implements Supplier<ArrayList<Point>> {

        private final Random random = new Random(System.currentTimeMillis());
        private Set<Point> foundPoints;
        private int m;

        public PointsSupplier(final Set<Point> foundPoints, final int m) {

            this.foundPoints = foundPoints;
            this.m = m;
        }


        @Override
        public ArrayList<Point> get() {
            final ArrayList<Point> points = new ArrayList<>();
            final ArrayList<Point> duplicate = new ArrayList<>(this.foundPoints);

            for (int i = 0; i < m; i++) {

                int rand = random.nextInt(foundPoints.size() - i);
                Point remove = duplicate.remove(rand);
                points.add(remove);
            }

            return points;
        }
    }
}

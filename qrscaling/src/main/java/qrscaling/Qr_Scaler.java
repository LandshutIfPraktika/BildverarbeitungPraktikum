package qrscaling;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import util.Util;

import java.util.LinkedList;
import java.util.Queue;

public class Qr_Scaler implements PlugInFilter {

    public static final byte floodFillMarker = (byte) 0xf0;
    private int numberOfSeams = 0;

    public int setup(final String arg, final ImagePlus imp) {
        numberOfSeams = Integer.parseInt(arg);
        return DOES_RGB | NO_UNDO | NO_CHANGES;
    }

    public void run(final ImageProcessor ip) {

        final ByteProcessor gray = Util.makeGray(ip);

        //gray.findEdges();
        gray.autoThreshold();

        final int width = gray.getWidth();
        final int height = gray.getHeight();

        final byte[] pixels = (byte[]) gray.getPixels();

        for (int row = 0; row < height; row++) {
            final int rowStart = row * width;
            for (int col = 0; col < width; col++) {
                final int pos = rowStart + col;
                if (pixels[pos] != 0) {
                    continue;
                }

                final Queue<Integer> stack = new LinkedList<>();
                stack.add(pos);
                final LinkedList<Integer> area = floodFill(stack, pixels, height, width);

                System.err.println(area);
            }
        }


        new ImagePlus("edges", gray).show();
    }


    private LinkedList<Integer> floodFill(final Queue<Integer> stack, final byte[] pixels, final int height, final int width) {
        final int[] shifts = new int[]{
                -width, -width - 1, -width + 1, -1, +1, +width, +width - 1, +width + 1
        };
        final LinkedList<Integer> area = new LinkedList<>();

        final int size = height * width;
        while (!stack.isEmpty()) {
            final int position = stack.poll();
            pixels[position] = floodFillMarker;
            area.add(position);


            for (int i = 0; i < shifts.length; i++) {
                final int candidate = position + shifts[i];
                if (candidate < size && candidate >= 0 && pixels[candidate] == 0) {
                    stack.add(candidate);
                }
            }
        }
        return area;
    }


    private static class Position {
        private final int row;
        private final int column;


        private Position(final int row, final int column) {
            this.row = row;
            this.column = column;
        }

        private Position(final int abs, final int height, final int width) {
            this.row = abs / width;
            this.column = abs % width;
        }
    }

    private ByteProcessor addWhiteBorder(final ByteProcessor gray) {
        final int width = gray.getWidth();
        final int height = gray.getHeight();
        final byte[] pixels = (byte[]) gray.getPixels();

        final int newWidth = width + 2;
        final int newHeight = height + 2;
        final byte[] newPixels = new byte[newHeight * newWidth];

        for (int row = 0; row < newHeight; row++) {
            final int rowStart = row * newWidth;
            for (int col = 0; col < newWidth; col++) {
                if (col == 0 || row == 0 || col == newWidth - 1 || row == newHeight - 1) {
                    newPixels[rowStart + col] = (byte) (255 & 0xff);
                } else {

                }
            }
        }
        return null;
    }

}

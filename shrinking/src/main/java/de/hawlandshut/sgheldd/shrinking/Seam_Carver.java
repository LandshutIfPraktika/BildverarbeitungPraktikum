package de.hawlandshut.sgheldd.shrinking;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import util.Util;

import java.util.Deque;
import java.util.LinkedList;

public class Seam_Carver implements PlugInFilter {

    private int numberOfSeams = 0;

    public int setup(final String arg, final ImagePlus imp) {
        numberOfSeams = Integer.parseInt(arg);
        return DOES_RGB | NO_UNDO | NO_CHANGES;
    }

    public void run(final ImageProcessor ip) {

        final int height = ip.getHeight();
        final int width = ip.getWidth();

        final Deque<int[]> seams = new LinkedList<>();

        final long start = System.nanoTime();
        final ImageProcessor carvedImage = getCarvedImage(ip, seams, numberOfSeams);
        System.out.println((System.nanoTime() - start) / 1000000 + "ms");


        ByteProcessor oldSeams = new ByteProcessor(carvedImage.getWidth(), carvedImage.getHeight());
        final byte[] pixels = (byte[]) oldSeams.getPixels();
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (byte) 0xff;
        }


        byte grey = (byte) 0x00;
        for (int[] seam : seams) {
            oldSeams = drawSeam(oldSeams, seam, grey -= 3);
        }


        new ImagePlus("carved", carvedImage).show();
        new ImagePlus("seams", oldSeams).show();
    }


    ByteProcessor drawSeam(final ByteProcessor src, final int[] seam, final byte gray) {
        final byte[] srcPixels = (byte[]) src.getPixels();
        final int height = src.getHeight();
        final int width = src.getWidth();
        final int newWidth = width + 1;
        final ByteProcessor dst = new ByteProcessor(newWidth, height);
        final byte[] dstPixels = (byte[]) dst.getPixels();

        for (int row = 0; row < height; row++) {
            int oldColumn = 0;
            final int marked = seam[row];
            for (int column = 0; column < newWidth; column++) {
                if (column == marked) {
                    dstPixels[row * newWidth + column] = gray;
                } else {
                    dstPixels[row * newWidth + column] = srcPixels[row * width + oldColumn];
                    oldColumn++;
                }
            }
        }
        return dst;
    }

    ImageProcessor getCarvedImage(final ImageProcessor ip, final Deque<int[]> seams, final int removedSeames) {

        ImageProcessor duplicate = ip.duplicate();
        while (duplicate.getWidth() > ip.getWidth() - removedSeames) {
            final ByteProcessor ip_gray;
            if (duplicate instanceof ColorProcessor) {
                ip_gray = Util.makeGray(duplicate);
            } else {
                ip_gray = (ByteProcessor) duplicate;
            }
            ip_gray.findEdges();
            final int height = ip_gray.getHeight();
            final int width = ip_gray.getWidth();
            final byte[] gray_pixels = (byte[]) ip_gray.getPixels();
            final int[] cumulativeEnergy = getCumulativeEnergy(gray_pixels, width, height);
            final int[] seam = getSeam(cumulativeEnergy, width, height);
            seams.addFirst(seam);
            duplicate = removeSeam(duplicate, seam, width, height);
        }
        return duplicate;
    }

    ColorProcessor removeSeam(final ImageProcessor duplicate, final int[] seam, final int width, final int height) {
        final int newWidth = width - 1;
        final ColorProcessor colorProcessor = new ColorProcessor(newWidth, height);
        final int[] colorPixels = (int[]) colorProcessor.getPixels();


        final int[] duplicatePixels = (int[]) duplicate.getPixels();
        for (int row = 0; row < height; row++) {
            final int dropped = seam[row];
            int newColumn = 0;
            for (int column = 0; column < width; column++) {
                if (column != dropped) {
                    colorPixels[row * newWidth + newColumn] = duplicatePixels[row * width + column];
                    newColumn++;
                }
            }
        }
        return colorProcessor;
    }

    int[] getSeam(final int[] cumulativeEnergy, final int width, final int height) {
        int minimumPosition = 0;
        int minimumValue = Integer.MAX_VALUE;
        for (int column = width * (height - 1); column < cumulativeEnergy.length; column++) {
            final int currentValue = cumulativeEnergy[column];
            if (currentValue < minimumValue) {
                minimumPosition = column;
                minimumValue = currentValue;
            }
        }

        final int[] seam = new int[height];
        int lastColumn = minimumPosition - width * (height - 1);
        seam[height - 1] = lastColumn;

        for (int row = height - 2; row >= 0; row--) {
            final int rowStart = row * width;
            final int middle = rowStart + lastColumn;
            final int left = middle - 1;
            final int right = middle + 1;
            if (lastColumn == 0) {
                lastColumn = (cumulativeEnergy[middle] < cumulativeEnergy[right] ? middle : right) - rowStart;
            } else if (lastColumn == width - 1) {
                lastColumn = (cumulativeEnergy[left] < cumulativeEnergy[middle] ? left : middle) - rowStart;
            } else {
                lastColumn = (cumulativeEnergy[left] < cumulativeEnergy[middle] ?
                        (cumulativeEnergy[left] < cumulativeEnergy[right] ? left : right) :
                        (cumulativeEnergy[middle] < cumulativeEnergy[right] ? middle : right)) - rowStart;
            }

            seam[row] = lastColumn;
        }
        return seam;
    }

    int[] getCumulativeEnergy(final byte[] gray_pixels, final int width, final int height) {
        final int[] cumulativeEnergy = new int[gray_pixels.length];


        for (int column = 0; column < width; column++) {
            cumulativeEnergy[column] = gray_pixels[column];
        }

        for (int row = 1; row < height; row++) {
            for (int column = 0; column < width; column++) {
                final int middle = (row - 1) * width + column;
                final int left = middle - 1;
                final int right = middle + 1;
                final int minimum;
                if (column == 0) {
                    minimum = cumulativeEnergy[middle] < cumulativeEnergy[right] ? cumulativeEnergy[middle] : cumulativeEnergy[right];
                } else if (column == width - 1) {
                    minimum = cumulativeEnergy[left] < cumulativeEnergy[middle] ? cumulativeEnergy[left] : cumulativeEnergy[middle];
                } else {
                    minimum = cumulativeEnergy[left] < cumulativeEnergy[middle] ?
                            (cumulativeEnergy[left] < cumulativeEnergy[right] ? cumulativeEnergy[left] : cumulativeEnergy[right]) :
                            (cumulativeEnergy[middle] < cumulativeEnergy[right] ? cumulativeEnergy[middle] : cumulativeEnergy[right]);
                }
                final int position = row * width + column;
                final int value = (gray_pixels[position] & 0xff) + minimum;
                cumulativeEnergy[position] = value;
            }
        }
        return cumulativeEnergy;
    }
}

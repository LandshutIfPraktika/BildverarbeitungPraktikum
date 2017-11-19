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
        return DOES_RGB | NO_UNDO | NO_CHANGES;
    }

    public void run(final ImageProcessor ip) {

        new ImagePlus("original",ip.duplicate()).show();

        final QRFinder qrFinder = new QRFinder();
        ip.setValue(0x00ff0000);

        qrFinder.find(ip);
        qrFinder.rotate(ip);
        qrFinder.draw(ip);
        qrFinder.crop(ip);
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

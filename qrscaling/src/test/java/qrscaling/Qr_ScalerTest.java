package qrscaling;

import ij.process.ByteProcessor;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Qr_ScalerTest {

    private final static byte white = (byte) (255 & 0xff);
    private final static byte black = 0;

    private final byte[] onePattern = new byte[]{
            white, white, white, white, white, white, white, white, white,
            white, black, black, black, black, black, black, black, white,
            white, black, white, white, white, white, white, black, white,
            white, black, white, black, black, black, white, black, white,
            white, black, white, black, black, black, white, black, white,
            white, black, white, black, black, black, white, black, white,
            white, black, white, white, white, white, white, black, white,
            white, black, black, black, black, black, black, black, white,
            white, white, white, white, white, white, white, white, white
    };

    private final byte[] twoPattern = new byte[]{
            white, white, white, white, white, white, white, white, white, white, white, white, white, white, white, white, white, white,
            white, black, black, black, black, black, black, black, white, white, black, black, black, black, black, black, black, white,
            white, black, white, white, white, white, white, black, white, white, black, white, white, white, white, white, black, white,
            white, black, white, black, black, black, white, black, white, white, black, white, black, black, black, white, black, white,
            white, black, white, black, black, black, white, black, white, white, black, white, black, black, black, white, black, white,
            white, black, white, black, black, black, white, black, white, white, black, white, black, black, black, white, black, white,
            white, black, white, white, white, white, white, black, white, white, black, white, white, white, white, white, black, white,
            white, black, black, black, black, black, black, black, white, white, black, black, black, black, black, black, black, white,
            white, white, white, white, white, white, white, white, white, white, white, white, white, white, white, white, white, white
    };


    @Test
    public void test_find_one_possible_patterns() {
        final QRFinder qrFinder = new QRFinder();
        final ByteProcessor byteProcessor = new ByteProcessor(9, 9, onePattern);
        final byte[] pixels = qrFinder.prepareImage(byteProcessor);
        final LinkedList<PossiblePattern> possiblePatterns = qrFinder.getPossiblePatterns(pixels);
        assertEquals(1, possiblePatterns.size());
    }

    @Test
    public void test_find_two_possible_patterns() {
        final QRFinder qrFinder = new QRFinder();
        final ByteProcessor byteProcessor = new ByteProcessor(18, 9, twoPattern);
        final byte[] pixels = qrFinder.prepareImage(byteProcessor);
        final LinkedList<PossiblePattern> possiblePatterns = qrFinder.getPossiblePatterns(pixels);
        assertEquals(2, possiblePatterns.size());
    }

    @Test
    public void test_check_one_vertical() {
        final QRFinder qrFinder = new QRFinder();
        final ByteProcessor byteProcessor = new ByteProcessor(9, 9, onePattern);
        final byte[] pixels = qrFinder.prepareImage(byteProcessor);
        final LinkedList<PossiblePattern> possiblePatterns = qrFinder.getPossiblePatterns(pixels);
        final PossiblePattern possiblePattern = possiblePatterns.get(0);

        final int centreCol = qrFinder.centreFromEndOfPattern(possiblePattern.pixelCounts, possiblePattern.col);
        assertEquals(4, centreCol);
        final int centreRow = qrFinder.crossCheck(possiblePattern.row, centreCol, possiblePattern.pixelCounts[2], possiblePattern.result.totalPixelCount, possiblePattern.pixels, byteProcessor.getWidth()).centre;
        assertEquals(4, centreRow);
    }

    @Test
    public void test_check_two_vertical() {
        final QRFinder qrFinder = new QRFinder();
        final ByteProcessor byteProcessor = new ByteProcessor(18, 9, twoPattern);
        final byte[] pixels = qrFinder.prepareImage(byteProcessor);
        final LinkedList<PossiblePattern> possiblePatterns = qrFinder.getPossiblePatterns(pixels);
        assertEquals(2, possiblePatterns.size());
        final PossiblePattern firstPattern = possiblePatterns.get(0);
        final int firstCentreCol = qrFinder.centreFromEndOfPattern(firstPattern.pixelCounts, firstPattern.col);
        assertEquals(4, firstCentreCol);
        final int firstCentreRow = qrFinder.crossCheck(firstPattern.row, firstCentreCol, firstPattern.pixelCounts[2], firstPattern.result.totalPixelCount, firstPattern.pixels, byteProcessor.getWidth()).centre;
        assertEquals(4, firstCentreRow);


        final PossiblePattern secondPattern = possiblePatterns.get(1);
        final int centreCol = qrFinder.centreFromEndOfPattern(secondPattern.pixelCounts, secondPattern.col);
        assertEquals(13, centreCol);
        final int centreRow = qrFinder.crossCheck(secondPattern.row, centreCol, secondPattern.pixelCounts[2], secondPattern.result.totalPixelCount, secondPattern.pixels, byteProcessor.getWidth()).centre;
        assertEquals(4, centreRow);
    }

    @Test
    public void test_check_one_complete() {
        final QRFinder qrFinder = new QRFinder();
        final ByteProcessor byteProcessor = new ByteProcessor(9, 9, onePattern);
        final byte[] pixels = qrFinder.prepareImage(byteProcessor);
        final LinkedList<PossiblePattern> possiblePatterns = qrFinder.getPossiblePatterns(pixels);
        final PossiblePattern possiblePattern = possiblePatterns.get(0);

        int centreCol = qrFinder.centreFromEndOfPattern(possiblePattern.pixelCounts, possiblePattern.col);
        assertEquals(4, centreCol);
        final int centreRow = qrFinder.crossCheck(possiblePattern.row, centreCol, possiblePattern.pixelCounts[2], possiblePattern.result.totalPixelCount, possiblePattern.pixels, byteProcessor.getWidth()).centre;
        assertEquals(4, centreRow);
        centreCol = qrFinder.crossCheck(centreRow, centreCol, possiblePattern.pixelCounts[2], possiblePattern.result.totalPixelCount, possiblePattern.pixels, 1).centre % byteProcessor.getWidth();
        assertEquals(4, centreCol);
        final int diagonal = qrFinder.crossCheck(centreRow, centreCol, possiblePattern.pixelCounts[2], possiblePattern.result.totalPixelCount, possiblePattern.pixels, 1 + byteProcessor.getWidth()).centre;
        assertFalse(diagonal == -1);
    }

}
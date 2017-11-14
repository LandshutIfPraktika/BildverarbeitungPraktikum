package qrscaling;

public class PossiblePattern {
    final byte[] pixels;
    final QRFinder.PatternCheckResult result;
    final int[] pixelCounts;
    final int row;
    final int col;

    public PossiblePattern(byte[] pixels, QRFinder.PatternCheckResult result, int[] pixelCounts, int row, int col) {
        this.pixels = pixels;
        this.result = result;
        this.pixelCounts = pixelCounts;
        this.row = row;
        this.col = col;
    }
}

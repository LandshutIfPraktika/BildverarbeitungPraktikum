package util;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Util {

    public static ByteProcessor makeGray(final ImageProcessor ip) {
        final ByteProcessor ip_gray = new ByteProcessor(ip.getWidth(), ip.getHeight());
        final int[] rgb_pixels = (int[]) ip.getPixels(); // efficient access
        final byte[] gray_pixels = (byte[]) ip_gray.getPixels(); // efficient access
        for (int i = 0; i < rgb_pixels.length; i++) {
            int r = (rgb_pixels[i] & 0xff0000) >> 16; // efficient access
            int g = (rgb_pixels[i] & 0x00ff00) >> 8; // efficient access
            int b = (rgb_pixels[i] & 0x0000ff); // efficient access
            gray_pixels[i] = (byte) (0.299 * r + 0.587 * g + 0.114 * b + 0.5); // no Math.round()!
        }
        return ip_gray;
    }
}

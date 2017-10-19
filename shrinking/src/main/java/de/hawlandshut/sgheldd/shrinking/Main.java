package de.hawlandshut.sgheldd.shrinking;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

public class Main {
    public static void main(String[] args) {
        final ImageJ imageJ = new ImageJ();
        imageJ.exitWhenQuitting(true);

        final String input = System.getenv("image.path");
        final ImagePlus imagePlus = IJ.openImage(input);
        if (imagePlus == null) {
            IJ.error("could not open " + input);
        }

        //imagePlus.show();

        IJ.runPlugIn(imagePlus, Seam_Carver.class.getName(), "56");
    }
}

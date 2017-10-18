package de.hawlandshut.sgheldd.shrinking;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

public class Main {
    public static void main(String[] args) {
        final ImageJ imageJ = new ImageJ();
        imageJ.exitWhenQuitting(true);

        final String input = "/Users/shared/Dropbox/Landshut/WiSe2017/bildverarbeitung/GruenesHaus.jpg";
        final ImagePlus imagePlus = IJ.openImage(input);
        if (imagePlus == null) {
            IJ.error("could not open " + input);
        }

        //imagePlus.show();

        IJ.runPlugIn(imagePlus, Seam_Carver.class.getName(), "68");
    }
}

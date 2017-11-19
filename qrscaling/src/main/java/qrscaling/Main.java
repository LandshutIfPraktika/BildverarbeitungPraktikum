package qrscaling;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

public class Main {
    public static void main(String[] args) {
        final ImageJ imageJ = new ImageJ();
        imageJ.exitWhenQuitting(true);

        final String input = "/Users/Shared/Dropbox/Landshut/WiSe2017/bildverarbeitung/bildverarbeitung_praktikum/qrscaling/src/main/resources/img/QR_Scr.jpg";
        final ImagePlus imagePlus = IJ.openImage(input);
        if (imagePlus == null) {
            IJ.error("could not open " + input);
        }

        //imagePlus.show();

        IJ.runPlugIn(imagePlus, Qr_Scaler.class.getName(), "");
    }
}

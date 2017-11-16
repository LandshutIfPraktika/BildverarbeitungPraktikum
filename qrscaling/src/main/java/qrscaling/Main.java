package qrscaling;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

public class Main {
    public static void main(String[] args) {
        final ImageJ imageJ = new ImageJ();
        imageJ.exitWhenQuitting(true);

        final String input = "/users5/s-gheldd/development/BildverarbeitungPraktikum/qrscaling/src/main/resources/img/QR_Blau.jpg";
        final ImagePlus imagePlus = IJ.openImage(input);
        if (imagePlus == null) {
            IJ.error("could not open " + input);
        }

        //imagePlus.show();

        IJ.runPlugIn(imagePlus, Qr_Scaler.class.getName(), "");
    }
}

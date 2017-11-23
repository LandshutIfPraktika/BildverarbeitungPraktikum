package qrscaling;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        final ImageJ imageJ = new ImageJ();
        imageJ.exitWhenQuitting(true);

        File folder = new File("qrscaling/src/main/resources/img/");


        for (final File file : folder.listFiles()) {

            final ImagePlus imagePlus = IJ.openImage(file.getAbsolutePath());
            if (imagePlus == null) {
                IJ.error("could not open " + file.getAbsolutePath());
            }

            //imagePlus.show();

            IJ.runPlugIn(imagePlus, Qr_Scaler.class.getName(), file.getName() + "_");
        }

    }
}

package qrscaling;

public class Qr_ScalerTest {

    private final static byte white = (byte) 0xff;
    private final static byte black = (byte) 0x00;

    private final byte[] testImage = new byte[]{
            white,white,white,white,white,white,
            white,black,black,white,white,white,
            white,black,white,white,white,white,
            white,black,white,white,black,black,
    };

}
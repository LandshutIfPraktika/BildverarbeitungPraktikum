package de.hawlandshut.sgheldd.shrinking;

import static org.junit.Assert.assertArrayEquals;

public class Seam_CarverTest {
    @org.junit.Test
    public void getInts() throws Exception {
        final byte[] bytes = {1, 2, 3,
                4, 5, 6};

        final Seam_Carver seam_carver = new Seam_Carver();
        final int[] ints = seam_carver.getCumulativeEnergy(bytes, 3, 2);

        assertArrayEquals(new int[]{1, 2, 3, 5, 6, 8}, ints);
    }

}
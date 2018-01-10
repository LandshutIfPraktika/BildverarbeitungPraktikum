package ransac;

import jdk.nashorn.internal.ir.SetSplitState;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class Line_RansacTest {

    private Line_Ransac line_ransac;
    private HashSet<Line_Ransac.Point> points;
    private ArrayList<Line_Ransac.Point> ps;

    @Before
    public void setUp()throws Exception{
        line_ransac = new Line_Ransac();

        points = new HashSet<>(Arrays.asList(new Line_Ransac.Point(1, 3)));
        ps = new ArrayList<>( Arrays.asList(new Line_Ransac.Point(0, 0), new Line_Ransac.Point(2, 2)));
    }


}
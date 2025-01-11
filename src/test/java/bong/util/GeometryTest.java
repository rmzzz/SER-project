package bong.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import javafx.geometry.Point2D;

public class GeometryTest {
    @Test
    public void testDistanceToLineSegment(){
      assertEquals(0.0, Geometry.distanceToLineSegment(new Point2D(0, 0), new Point2D(0, 0), new Point2D(0, 0)));
      assertEquals(0.0, Geometry.distanceToLineSegment(new Point2D(5, 5), new Point2D(0, 0), new Point2D(10, 10)));
      assertEquals(0.0, Geometry.distanceToLineSegment(new Point2D(5, 0), new Point2D(0, 0), new Point2D(10, 0)));
      assertEquals(5.0, Geometry.distanceToLineSegment(new Point2D(0, 0), new Point2D(5, 0), new Point2D(10, 0)));
      assertEquals(5.0, Geometry.distanceToLineSegment(new Point2D(3, 4), new Point2D(-10, 0), new Point2D(0, 0)));
    }

    @Test
    public void testDistance(){
      assertEquals(0.0, Geometry.distance(new Point2D(0, 0), new Point2D(0, 0)));
      assertEquals(5.0, Geometry.distance(new Point2D(3, 4), new Point2D(0, 0)));
      assertEquals(0.001, Geometry.distance(new Point2D(0.001,0), new Point2D(0, 0)));
    }

    @Test
    public void testDistance2(){
      assertEquals(0.0, Geometry.distance(0,0,0,0));
      assertEquals(5.0, Geometry.distance(3,4,0,0));
      assertEquals(0.001, Geometry.distance(0.001,0,0,0));
    }

}
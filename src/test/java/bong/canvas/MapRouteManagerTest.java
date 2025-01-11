package bong.canvas;

import bong.OSMReader.Node;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class MapRouteManagerTest {

    private MapCanvas canvas;

    @BeforeEach
    public void setUp() {
        canvas = new MapCanvas();
    }

    @Test
    public void testClearOriginDestination() {
        canvas.getMapRouteManager().setCurrentRouteOrigin();
        canvas.getMapRouteManager().setCurrentRouteDestination();

        canvas.getMapRouteManager().clearOriginDestination(canvas);

        assertNull(canvas.getMapRouteManager().getCurrentRouteOrigin());
        assertNull(canvas.getMapRouteManager().getCurrentRouteDestination());
    }

    @Test
    public void testSetRouteOriginWithPoint() {
        Point2D point = new Point2D(5.0, 10.0);
        canvas.getMapRouteManager().setRouteOrigin(point, canvas);

        assertNotNull(canvas.getMapRouteManager().getCurrentRouteOrigin());
        assertEquals(5.0f, canvas.getMapRouteManager().getCurrentRouteOrigin().getCenterX(), 0.01f);
        assertEquals(10.0f, canvas.getMapRouteManager().getCurrentRouteOrigin().getCenterY(), 0.01f);
    }

    @Test
    public void testSetRouteOriginWithNullPoint() {
        canvas.getMapRouteManager().setRouteOrigin(null, canvas);
        assertNull(canvas.getMapRouteManager().getCurrentRouteOrigin());
    }

    @Test
    public void testSetRouteDestinationWithPoint() {
        Point2D point = new Point2D(15.0, 20.0);
        canvas.getMapRouteManager().setRouteDestination(point, canvas);
        assertNotNull(canvas.getMapRouteManager().getCurrentRouteDestination());
        assertEquals(15.0f, canvas.getMapRouteManager().getCurrentRouteDestination().getCenterX(), 0.01f);
        assertEquals(20.0f, canvas.getMapRouteManager().getCurrentRouteDestination().getCenterY(), 0.01f);
    }

    @Test
    public void testSetRouteDestinationWithNullPoint() {
        canvas.getMapRouteManager().setRouteDestination(null, canvas);
        assertNull(canvas.getMapRouteManager().getCurrentRouteDestination());
    }

    @Test
    public void testSetStartDestPoint() {
        Node startNode = new Node(1, 40.0f, 50.0f);
        Node destinationNode = new Node(2, 60.0f, 70.0f);
        canvas.getMapRouteManager().setStartDestPoint(startNode, destinationNode);

        assertEquals(startNode, canvas.getMapRouteManager().getStartNode());
        assertEquals(destinationNode, canvas.getMapRouteManager().getDestinationNode());
    }
}

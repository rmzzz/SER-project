package bong.canvas;

import bong.OSMReader.Model;
import bong.OSMReader.Node;
import bong.OSMReader.OSMReader;
import bong.routeFinding.Edge;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.NonInvertibleTransformException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MapMouseInteractionTest {

    private MapMouseInteraction mapMouseInteraction;
    private MapCanvas canvas;
    private Model model;
    OSMReader osmReader = new OSMReader(
            getClass().getClassLoader().getResourceAsStream("bong/noCoastline.osm")
    );

    @BeforeEach
    public void setUp() {
        canvas = new MapCanvas();
        model = new Model(osmReader);
        mapMouseInteraction = canvas.getMapMouseInteraction();
    }

    @Test
    public void testGetShowStreetNodeCloseToMouse() {
        assertFalse(mapMouseInteraction.getShowStreetNodeCloseToMouse());
    }

    @Test
    public void testSetShowStreetNodeCloseToMouse() {
        mapMouseInteraction.setShowStreetNodeCloseToMouse(true);
        assertTrue(mapMouseInteraction.getShowStreetNodeCloseToMouse());

        mapMouseInteraction.setShowStreetNodeCloseToMouse(false);
        assertFalse(mapMouseInteraction.getShowStreetNodeCloseToMouse());
    }

    @Test
    public void testGetTranslatedCoords() throws NonInvertibleTransformException {
        MouseEvent mockMouseEvent = new MouseEvent(
                MouseEvent.MOUSE_MOVED,
                100.0, 200.0, 100.0, 200.0,
                null, 0, false, false, false, false,
                false, false, false, false, false, false, null
        );

        Point2D result = mapMouseInteraction.getTranslatedCoords(mockMouseEvent, canvas);
        assertNotNull(result);
        assertEquals(100.0, result.getX(), 0.01);
        assertEquals(200.0, result.getY(), 0.01);
    }

    @Test
    public void testFindNearestNode() {
        Point2D testCoords = new Point2D(1587974.75, -5879452.5);
        Node nearestNode = mapMouseInteraction.findNearestNode(model, testCoords);
        assertNotNull(nearestNode);
        // Validate the returned node based on the test model setup
        assertEquals(testCoords.getX(), nearestNode.getLon(), 0.01);
        assertEquals(testCoords.getY(), nearestNode.getLat(), 0.01);
    }

    @Test
    public void testCalculateAngle() {
        Point2D vector1 = new Point2D(1.0, 0.0);
        Point2D vector2 = new Point2D(0.0, 1.0);

        double angle = mapMouseInteraction.calculateAngle(vector1, vector2);
        assertEquals(Math.PI / 2, angle, 0.01);
    }

}

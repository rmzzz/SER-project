package bong.canvas;

import bong.OSMReader.Node;
import javafx.geometry.Point2D;
import javafx.scene.transform.Affine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MapRendererTest {

    private MapRenderer mapRenderer;
    private MapCanvas mapCanvas;

    @BeforeEach
    public void setUp() {
        mapCanvas = new MapCanvas();
        Affine trans = new Affine();
        mapRenderer = mapCanvas.getMapRenderer();
    }

    @Test
    public void testZoomIn() {
        double initialMxx = mapRenderer.getTrans().getMxx();
        mapRenderer.zoom(1.5, 100, 100, mapCanvas);
        assertTrue(mapRenderer.getTrans().getMxx() > initialMxx);
    }

    @Test
    public void testZoomOut() {
        double initialMxx = mapRenderer.getTrans().getMxx();
        mapRenderer.zoom(0.5, 100, 100, mapCanvas);
        assertTrue(mapRenderer.getTrans().getMxx() < initialMxx);
    }

    @Test
    public void testUpdateSearchRange() {
        double canvasWidth = 500.0;
        double canvasHeight = 300.0;

        mapCanvas.setWidth(canvasWidth);
        mapCanvas.setHeight(canvasHeight);

        mapRenderer.updateSearchRange(mapCanvas);

        assertNotNull(mapRenderer.getRenderRange());
    }

    @Test
    public void testSetDraggedSquare() {
        LinePath draggedSquare = new LinePath(new Node(10L, 10.0f, 10.0f), new Node(20L, 20.0f, 20.0f));
        mapRenderer.setDraggedSquare(draggedSquare, mapCanvas);

        assertNotNull(mapRenderer.getDraggedSquare());
        assertEquals(draggedSquare, mapRenderer.getDraggedSquare());
    }

    @Test
    public void testSetShowCities() {
        mapRenderer.setShowCities(true, mapCanvas);
        assertTrue(mapRenderer.getShowCities());

        mapRenderer.setShowCities(false, mapCanvas);
        assertFalse(mapRenderer.getShowCities());
    }

    @Test
    public void testZoomToNode() {
        Node node = new Node(10L, 10.0f, 20.0f);
        mapRenderer.zoomToNode(node, mapCanvas);

        assertNotNull(mapRenderer.getTrans());
    }

    @Test
    public void testZoomToPoint() {
        float lon = 10.0f;
        float lat = 20.0f;

        mapRenderer.zoomToPoint(1.5, lon, lat, mapCanvas);
        assertTrue(mapRenderer.getTrans().getMxx() > 1.0);
    }
}

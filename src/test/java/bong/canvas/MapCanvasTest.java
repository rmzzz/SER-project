package bong.canvas;

import javafx.scene.transform.Affine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapCanvasTest {

    MapCanvas canvas = new MapCanvas();

    @Test
    void clearOriginDestinationTest() {
        canvas.setCurrentRouteDestination();
        canvas.setCurrentRouteOrigin();

        assertEquals(1, canvas.getCurrentRouteDestination().centerX);
        assertEquals(1, canvas.getCurrentRouteOrigin().centerX);

        canvas.clearOriginDestination();
        assertNull(canvas.getCurrentRouteDestination());
        assertNull(canvas.getCurrentRouteOrigin());
    }

    @Test
    void updateSearchRangeTest() {
        canvas = new MapCanvas();
        canvas.repaint();
        canvas.getMapState().setRenderFullScreen(false);
        canvas.getMapRenderer().updateSearchRange(canvas);

        Range actual = canvas.getMapRenderer().getRenderRange();
        assertEquals(-100, actual.getMinX());
        assertEquals(-100, actual.getMinY());
        assertEquals(100, actual.getMaxX());
        assertEquals(100, actual.getMaxY());
    }

    @Test
    void shouldZoomTest() {
        Affine trans;
        boolean actual;

        canvas = new MapCanvas();
        trans = canvas.getMapRenderer().getTrans();
        trans.prependScale(1,1);
        actual = canvas.getMapRenderer().shouldZoom(1.5);
        assertTrue(actual);

        canvas = new MapCanvas();
        trans = canvas.getMapRenderer().getTrans();
        trans.prependScale(20,20);
        actual = canvas.getMapRenderer().shouldZoom(1.5);
        assertFalse(actual);

        canvas = new MapCanvas();
        trans = canvas.getMapRenderer().getTrans();
        trans.prependScale(1,1);
        actual = canvas.getMapRenderer().shouldZoom(0.5);
        assertTrue(actual);

        canvas = new MapCanvas();
        trans = canvas.getMapRenderer().getTrans();
        trans.prependScale(0.0001,0.0001);
        actual = canvas.getMapRenderer().shouldZoom(0.5);
        assertFalse(actual);
    }

}
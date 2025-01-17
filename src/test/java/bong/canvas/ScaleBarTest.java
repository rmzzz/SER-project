package bong.canvas;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScaleBarTest {

    @Test
    void testUpdateScaleBar() {

        MapCanvas canvas = new MapCanvas();
        canvas.getMapRenderer().getTrans().prependScale(0.9, 0.9);
        canvas.getMapRenderer().repaint(canvas);
        canvas.getMapRenderer().getScaleBar().updateScaleBar(canvas);
        String actual = canvas.getMapRenderer().getScaleBar().getBarShowing();
        Assertions.assertEquals("50m", actual);
        Assertions.assertEquals(50, canvas.getMapRenderer().getScaleBar().getBarLength(), 1);

        canvas = new MapCanvas();
        canvas.getMapRenderer().getTrans().prependScale(0.4, 0.4);
        canvas.getMapRenderer().repaint(canvas);
        canvas.getMapRenderer().getScaleBar().updateScaleBar(canvas);
        actual = canvas.getMapRenderer().getScaleBar().getBarShowing();
        Assertions.assertEquals("100m", actual);
        Assertions.assertEquals(100, canvas.getMapRenderer().getScaleBar().getBarLength(), 1);

        canvas = new MapCanvas();
        canvas.getMapRenderer().getTrans().prependScale(0.2, 0.2);
        canvas.getMapRenderer().repaint(canvas);
        canvas.getMapRenderer().getScaleBar().updateScaleBar(canvas);
        actual = canvas.getMapRenderer().getScaleBar().getBarShowing();
        Assertions.assertEquals("250m", actual);
        Assertions.assertEquals(250, canvas.getMapRenderer().getScaleBar().getBarLength(), 1);

        canvas = new MapCanvas();
        canvas.getMapRenderer().getTrans().prependScale(0.1, 0.1);
        canvas.getMapRenderer().repaint(canvas);
        canvas.getMapRenderer().getScaleBar().updateScaleBar(canvas);
        actual = canvas.getMapRenderer().getScaleBar().getBarShowing();
        Assertions.assertEquals("500m", actual);
        Assertions.assertEquals(500, canvas.getMapRenderer().getScaleBar().getBarLength(), 1);

        canvas = new MapCanvas();
        canvas.getMapRenderer().getTrans().prependScale(0.05, 0.05);
        canvas.getMapRenderer().repaint(canvas);
        canvas.getMapRenderer().getScaleBar().updateScaleBar(canvas);
        actual = canvas.getMapRenderer().getScaleBar().getBarShowing();
        Assertions.assertEquals("1km", actual);
        Assertions.assertEquals(1000, canvas.getMapRenderer().getScaleBar().getBarLength(), 2);

        canvas = new MapCanvas();
        canvas.getMapRenderer().getTrans().prependScale(0.025, 0.025);
        canvas.getMapRenderer().repaint(canvas);
        canvas.getMapRenderer().getScaleBar().updateScaleBar(canvas);
        actual = canvas.getMapRenderer().getScaleBar().getBarShowing();
        Assertions.assertEquals("2km", actual);
        Assertions.assertEquals(2000, canvas.getMapRenderer().getScaleBar().getBarLength(), 5);

        canvas = new MapCanvas();
        canvas.getMapRenderer().getTrans().prependScale(0.012, 0.012);
        canvas.getMapRenderer().repaint(canvas);
        canvas.getMapRenderer().getScaleBar().updateScaleBar(canvas);
        actual = canvas.getMapRenderer().getScaleBar().getBarShowing();
        Assertions.assertEquals("5km", actual);
        Assertions.assertEquals(5000, canvas.getMapRenderer().getScaleBar().getBarLength(), 10);

        canvas = new MapCanvas();
        canvas.getMapRenderer().getTrans().prependScale(0.005, 0.005);
        canvas.getMapRenderer().repaint(canvas);
        canvas.getMapRenderer().getScaleBar().updateScaleBar(canvas);
        actual = canvas.getMapRenderer().getScaleBar().getBarShowing();
        Assertions.assertEquals("10km", actual);
        Assertions.assertEquals(10000, canvas.getMapRenderer().getScaleBar().getBarLength(), 20);

        canvas = new MapCanvas();
        canvas.getMapRenderer().getTrans().prependScale(0.0025, 0.0025);
        canvas.getMapRenderer().repaint(canvas);
        canvas.getMapRenderer().getScaleBar().updateScaleBar(canvas);
        actual = canvas.getMapRenderer().getScaleBar().getBarShowing();
        Assertions.assertEquals("20km", actual);
        Assertions.assertEquals(20000, canvas.getMapRenderer().getScaleBar().getBarLength(), 40);
    }

}
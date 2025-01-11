package bong.canvas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MapPinManagerTest {

    private MapCanvas canvas;

    @BeforeEach
    public void setUp() {
        canvas = new MapCanvas();
    }

    @Test
    public void testSetPin() {
        float lon = 30.5f;
        float lat = 45.2f;
        canvas.getMapPinManager().setPin(lon, lat, canvas);

        assertNotNull(canvas.getMapPinManager().getCurrentPin());
        assertEquals(lon, canvas.getMapPinManager().getCurrentPin().getCenterX(), 0.01f);
        assertEquals(lat, canvas.getMapPinManager().getCurrentPin().getCenterY(), 0.01f);
    }

    @Test
    public void testNullPin() {
        canvas.getMapPinManager().setPin(30.5f, 45.2f, canvas);
        canvas.getMapPinManager().nullPin(canvas);

        assertNull(canvas.getMapPinManager().getCurrentPin());
    }

    @Test
    public void testGetCurrentPinWhenNoPinSet() {
        assertNull(canvas.getMapPinManager().getCurrentPin());
    }
}

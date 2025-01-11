package bong.OSMReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class BoundTest {

    @Test
    public void boundTest() {
        Bound bound = new Bound(0f,0f,1f,1f);
        assertEquals(0f, bound.getMinLat());
        assertEquals(0f, bound.getMaxLat());
        assertEquals(1f, bound.getMinLon());
        assertEquals(1f, bound.getMaxLon());
    }
}
package bong.OSMReader;

import bong.canvas.LinePath;
import bong.canvas.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//@Disabled("As long as test .osm resource unavailable")
public class OSMReaderTest {

    @Test
    public void destroyTest(){
        try {
            OSMReader r = new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/noCoastline.osm"));
            r.destroy();

            assertNull(r.getAddresses());

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void selfclosingCoastlineTest(){
        try {
            OSMReader r = new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/selfclosingCoastline.osm"));

            LinePath coastline = (LinePath) r.getDrawableByType().get(Type.COASTLINE).get(0);
            float[] coords = coastline.getCoords();
            Node first = new Node(0, coords[0], coords[1]);
            Node last = new Node(1, coords[coords.length-2], coords[coords.length-1]);

            assertEquals(first.getLon(), last.getLon());
            assertEquals(first.getLat(), last.getLat());

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void noCoastlineTest() {
        try {
            OSMReader r = new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/noCoastline.osm"));

            assertEquals(1, r.getDrawableByType().get(Type.COASTLINE).size());

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void PeninsulaEastTest() {
        try {
            OSMReader r = new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/PenEast.osm"));

            LinePath coastline = (LinePath) r.getDrawableByType().get(Type.COASTLINE).get(0);
            float[] coords = coastline.getCoords();
            Node first = new Node(0, coords[0], coords[1]);
            Node last = new Node(1, coords[coords.length-2], coords[coords.length-1]);
            assertEquals(first.getLon(), last.getLon());
            assertEquals(first.getLat(), last.getLat());

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void PeninsulaSouthTest() {
        try {
            OSMReader r = new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/PenSouth.osm"));

            LinePath coastline = (LinePath) r.getDrawableByType().get(Type.COASTLINE).get(0);
            float[] coords = coastline.getCoords();
            Node first = new Node(0, coords[0], coords[1]);
            Node last = new Node(1, coords[coords.length-2], coords[coords.length-1]);
            assertEquals(first.getLon(), last.getLon());
            assertEquals(first.getLat(), last.getLat());

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void northernCoastlineCutoutTest(){
        try {
            OSMReader r = new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/NorthernCoastlineCutout.osm"));

            LinePath coastline = (LinePath) r.getDrawableByType().get(Type.COASTLINE).get(0);
            float[] coords = coastline.getCoords();
            Node first = new Node(0, coords[0], coords[1]);
            Node last = new Node(1, coords[coords.length-2], coords[coords.length-1]);

            assertEquals(first.getLon(), last.getLon());
            assertEquals(first.getLat(), last.getLat());

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void southernCoastlineCutoutTest(){
        try {
            OSMReader r = new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/SouthernCoastlineCutout.osm"));

            LinePath coastline = (LinePath) r.getDrawableByType().get(Type.COASTLINE).get(0);
            float[] coords = coastline.getCoords();
            Node first = new Node(0, coords[0], coords[1]);
            Node last = new Node(1, coords[coords.length-2], coords[coords.length-1]);

            assertEquals(first.getLon(), last.getLon());
            assertEquals(first.getLat(), last.getLat());

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void westernCoastlineCutoutTest(){
        try {
            OSMReader r = new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/WesternCoastlineCutout.osm"));

            LinePath coastline = (LinePath) r.getDrawableByType().get(Type.COASTLINE).get(0);
            float[] coords = coastline.getCoords();
            Node first = new Node(0, coords[0], coords[1]);
            Node last = new Node(1, coords[coords.length-2], coords[coords.length-1]);

            assertEquals(first.getLon(), last.getLon());
            assertEquals(first.getLat(), last.getLat());

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void easternCoastlineCutoutTest(){
        try {
            OSMReader r = new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/EasternCoastlineCutout.osm"));

            LinePath coastline = (LinePath) r.getDrawableByType().get(Type.COASTLINE).get(0);
            float[] coords = coastline.getCoords();
            Node first = new Node(0, coords[0], coords[1]);
            Node last = new Node(1, coords[coords.length-2], coords[coords.length-1]);

            assertEquals(first.getLon(), last.getLon());
            assertEquals(first.getLat(), last.getLat());

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void parseCityTest() {
        OSMReader reader = new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/pottehuseCity.osm"));

        assertEquals("Pottehuse", reader.getCities().get(0).getName());
        assertEquals(1, reader.getCities().size());

        reader.parseCity("place", "town");
        assertEquals("Pottehuse", reader.getCities().get(0).getName());
        assertEquals(1, reader.getCities().size());

        reader.parseCity("place", "hamlet");
        assertEquals("Pottehuse", reader.getCities().get(0).getName());
        assertEquals(1, reader.getCities().size());

        reader.setPreviousName("TestCity");
        reader.parseCity("place", "suburb");
        assertEquals("TestCity", reader.getCities().get(1).getName());
        assertEquals(2, reader.getCities().size());
    }

    @Test
    void parseStreetAccessTest() {
        OSMReader reader = new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/streetTest.osm"));
        reader.parseStreet();
        boolean firstFound = false;
        boolean secondFound = false;

        for (var a : reader.getGraph().getAdj().entrySet()) {
            for (var b : a.getValue()) {
                if (b.getStreet().getName() == null){
                    continue;
                }
                if (b.getStreet().getName().equals("SpengergassePermissive")) {
                    Assertions.fail();
                }
                if (b.getStreet().getName().equals("Lidmanskygasse")) {
                    firstFound = true;
                }
                if (b.getStreet().getName().equals("Paulitschgasse")) {
                    secondFound = true;
                }
            }
        }
        assertTrue(firstFound && secondFound);
    }

    @Test
    void parseStreetLivingStreetTest() {
        OSMReader reader = new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/streetTest.osm"));
        reader.parseStreet();
        int maxSpeed = 0;
        for (var a : reader.getGraph().getAdj().entrySet()) {
            for (var b : a.getValue()) {
                if (b.getStreet().getName() != null && b.getStreet().getName().equals("Lidmanskygasse")) {
                    maxSpeed = b.getStreet().getMaxspeed();
                }
            }
        }
        assertEquals(30, maxSpeed);

    }

    @Test
    void parseMotorwayStreetTest() {
        OSMReader reader = new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/streetTest.osm"));
        reader.parseStreet();

        int maxSpeed = 0;
        for (var a : reader.getGraph().getAdj().entrySet()) {
            for (var b : a.getValue()) {
                if (b.getStreet().getName() != null && b.getStreet().getName().equals("A1")) {
                    maxSpeed = b.getStreet().getMaxspeed();
                }
            }
        }
        assertEquals(130, maxSpeed);
    }

}

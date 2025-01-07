package bong.controllers;

import bong.OSMReader.OSMReader;
import bong.OSMReader.Model;
import bong.canvas.PointOfInterest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;

class FileControllerTest {
    FileController fileController = new FileController();

    @Test
    public void testLoadBinary() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("bong/POI.bin");
            ArrayList<PointOfInterest> actual = (ArrayList<PointOfInterest>) FileController.loadBinary(is);
            ArrayList<PointOfInterest> expected = new ArrayList<>();

            PointOfInterest poi1 = new PointOfInterest(1401871.2f, -7492294.5f, "poi2");
            PointOfInterest poi2 = new PointOfInterest(1404316.9f, -7497816.5f, "poi3");
            PointOfInterest poi3 = new PointOfInterest(1393406.4f, -7495392.0f, "poi1");
            expected.add(poi1);
            expected.add(poi2);
            expected.add(poi3);

            for (int i = 0; i < 3; i++) {
                Assertions.assertEquals(expected.get(i).toString(), actual.get(i).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    public void testLoadZip() {
        File file = new File(getClass().getClassLoader().getResource("bong/demozip.zip").getFile());
        try {
            InputStream in = new FileInputStream(FileController.loadZip(file).getPath());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            ArrayList<String> expected = new ArrayList<>();
            expected.add("linje 1");
            expected.add("linje 2");
            expected.add("linje 3");

            String line;
            int i = 0;
            while ((line = (br.readLine())) != null) {
                Assertions.assertEquals(expected.get(i), line);
                i++;
            }
            
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

    }

}
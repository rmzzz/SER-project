package bong.controllers;

import bong.canvas.PointOfInterest;
import javafx.geometry.Point2D;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;

public class PointsOfInterestController {

    private static ArrayList<PointOfInterest> pointsOfInterest;

    public PointsOfInterestController() {
        pointsOfInterest = new ArrayList<>();
    }

    public void addToPOI(PointOfInterest poi) {
        pointsOfInterest.add(poi);
    }

    public void setPOI(ArrayList<PointOfInterest> poi) {
        pointsOfInterest = poi;
    }

    public static ArrayList<PointOfInterest> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void removePOI(float x, float y) {
        for (PointOfInterest poi : pointsOfInterest) {
            if (poi.getLon() ==  x && poi.getLat() == y ) {
                pointsOfInterest.remove(poi);
                break;
            }
        }
        savePointsOfInterest();
    }

    public boolean POIContains(float x, float y) {
        for (PointOfInterest poi : pointsOfInterest) {
            if (poi.getLon() ==  x && poi.getLat() == y ) {
                return true;
            }
        }
        return false;
    }

    public void savePointsOfInterest() {
        File dataDir = FileController.getDataDir();
        File file = new File(dataDir, "POI.bin");
        try {
            FileController.saveBinary(file, PointsOfInterestController.getPointsOfInterest());
        } catch (IOException e) {
            AlertController.showError("Unable to save point of interest","Please try again", e);
        }
    }

    public void addPointOfInterest(Point2D point, Optional<String> givenName) {
        if (givenName.isPresent()) {
            PointOfInterest poi = new PointOfInterest((float) point.getX(), (float) point.getY(), givenName.get());
            addToPOI(poi);
        }
        savePointsOfInterest();
    }

    @SuppressWarnings("unchecked")
    public void loadPointsOfInterest() {
        ArrayList<PointOfInterest> list = new ArrayList<>();
        try {
            File file = FileController.getDataFile("POI.bin");
            InputStream is = new FileInputStream(file);
            list = (ArrayList<PointOfInterest>) FileController.loadBinary(is);
        } catch (Exception ignored){
            // TODO Ramiz: log error
        }
        setPOI(list);
    }
}

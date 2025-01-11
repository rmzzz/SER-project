package bong.OSMReader;

import javafx.geometry.Point2D;
import java.io.Serializable;
import java.util.function.LongSupplier;

public class Node implements LongSupplier, Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private float lon;
    private float lat;

    public Node(long id, float lon, float lat){
        this.id = id;
        this.lon = lon;
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public float getLat() {
        return lat;
    }

    @Override
    public long getAsLong() {
        return id;
    }

    @Override
    public String toString() {
        return "Node, lon:" + lon + " lat:" + lat + " + id:" + id;
    }

    public Point2D getAsPoint() {
        return new Point2D(this.lon, this.lat);
    }
}

package bong.OSMReader;


import java.io.Serializable;

public class Bound implements Serializable {
    private static final long serialVersionUID = 2L;
    private float minLat;
    private float maxLat;
    private float minLon;
    private float maxLon;

    public Bound(float minLat, float maxLat, float minLon, float maxLon) {
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLon = minLon;
        this.maxLon = maxLon;
    }

    public float getMinLat(){return minLat;}
    public float getMaxLat(){return maxLat;}
    public float getMinLon(){return minLon;}
    public float getMaxLon(){return maxLon;}
}
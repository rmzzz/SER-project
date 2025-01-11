package bong.canvas;

public class MapPinManager {
    private Pin currentPin;
    public Pin getCurrentPin() {
        return currentPin;
    }

    public void setPin (float lon, float lat, MapCanvas canvas){
        currentPin = new Pin(lon, lat, 1);
        canvas.getMapRenderer().repaint(canvas);
    }

    public void nullPin (MapCanvas canvas) {
        currentPin = null;
        canvas.getMapRenderer().repaint(canvas);
    }
}

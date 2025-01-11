package bong.canvas;

public class MapPinManager {
    private Pin currentPin;
    public Pin getCurrentPin() {
        return currentPin;
    }

    public void setPin (float lon, float lat, MapCanvasInterface canvas){
        currentPin = new Pin(lon, lat, 1);
        canvas.getMapRenderer().repaint(canvas);
    }

    public void nullPin (MapCanvasInterface canvas) {
        currentPin = null;
        canvas.getMapRenderer().repaint(canvas);
    }
}

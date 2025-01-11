package bong.canvas;

import javafx.scene.canvas.GraphicsContext;

public interface MapCanvasInterface {
    MapState getMapState();
    MapRouteManager getMapRouteManager();
    MapPinManager getMapPinManager();
    MapRenderer getMapRenderer();
    MapMouseInteraction getMapMouseInteraction();
    double getWidth();
    double getHeight();
    GraphicsContext getGraphicsContext2D();
}
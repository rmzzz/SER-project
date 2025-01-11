package bong.canvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Affine;
import java.util.*;

public class MapCanvas extends Canvas implements MapCanvasInterface {
    private MapRenderer mapRenderer;
    private MapState mapState;
    private MapRouteManager mapRouteManager;
    private MapPinManager mapPinManager;
    private MapMouseInteraction mapMouseInteraction;

    public MapCanvas() {
        Drawer drawer = new Drawer(getGraphicsContext2D());
        Affine affine = new Affine();
        this.mapRenderer = new MapRenderer(drawer, affine);
        this.mapState = new MapState(null, Arrays.asList(Type.getTypes()), false, true );
        this.mapRouteManager = new MapRouteManager();
        this.mapPinManager = new MapPinManager();
        this.mapMouseInteraction = new MapMouseInteraction();
    }

    @Override
    public MapState getMapState() {
        return mapState;
    }

    @Override
    public MapRouteManager getMapRouteManager() {
        return mapRouteManager;
    }

    @Override
    public MapPinManager getMapPinManager() {
        return mapPinManager;
    }

    @Override
    public MapRenderer getMapRenderer() {
        return mapRenderer;
    }

    @Override
    public MapMouseInteraction getMapMouseInteraction() {
        return mapMouseInteraction;
    }

}

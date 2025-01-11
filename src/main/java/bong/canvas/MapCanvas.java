package bong.canvas;

import bong.OSMReader.*;
import bong.model.RouteModel;
import bong.routeFinding.*;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;
import java.util.*;

public class MapCanvas extends Canvas {
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

    public MapRenderer getMapRenderer() {
        return mapRenderer;
    }

    public MapState getMapState() {
        return mapState;
    }

    public MapRouteManager getMapRouteManager() {
        return mapRouteManager;
    }

    public MapPinManager getMapPinManager() { return mapPinManager; }

    public MapMouseInteraction getMapMouseInteraction() { return mapMouseInteraction; }

}

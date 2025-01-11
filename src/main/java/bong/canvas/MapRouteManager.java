package bong.canvas;

import bong.OSMReader.Node;
import bong.controllers.RouteController;
import javafx.geometry.Point2D;

public class MapRouteManager {
    private Node startNode;
    private Node destinationNode;
    private RouteOriginIndicator currentRouteOrigin;
    private RouteDestinationIndicator currentRouteDestination;

    public RouteDestinationIndicator getCurrentRouteDestination() {
        return currentRouteDestination;
    }

    public RouteOriginIndicator getCurrentRouteOrigin() {
        return currentRouteOrigin;
    }

    public void setCurrentRouteDestination() {
        currentRouteDestination = new RouteDestinationIndicator(1,1,1);
    }

    public void setCurrentRouteOrigin() {
        currentRouteOrigin = new RouteOriginIndicator(1,1,1);
    }

    public void clearOriginDestination(MapCanvas canvas) {
        currentRouteOrigin = null;
        currentRouteDestination = null;
        canvas.getMapRenderer().repaint(canvas);
    }

    public void setRouteOrigin (Point2D point, MapCanvas canvas){
        if (point != null) {
            currentRouteOrigin = new RouteOriginIndicator((float) point.getX(), (float) point.getY(), 1);
        } else {
            currentRouteOrigin = null;
        }
        canvas.getMapRenderer().repaint(canvas);
    }

    public void setRouteDestination (Point2D point, MapCanvas canvas){
        if (point != null) {
            currentRouteDestination = new RouteDestinationIndicator((float) point.getX(), (float) point.getY(), 1);
        } else {
            currentRouteDestination = null;
        }
        canvas.getMapRenderer().repaint(canvas);
    }

    public void setStartDestPoint(Node start, Node dest) {
        this.startNode = start;
        this.destinationNode = dest;
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getDestinationNode() {
        return destinationNode;
    }
}

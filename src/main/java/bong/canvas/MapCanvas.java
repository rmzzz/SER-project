package bong.canvas;

import bong.OSMReader.*;
import bong.controllers.MainController;
import bong.controllers.RouteController;
import bong.routeFinding.*;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;
import java.util.*;

public class MapCanvas extends Canvas  {

    MapRenderer mapRenderer;
    MapState mapState;

    private Node startNode;
    private Node destinationNode;
    private Pin currentPin;
    private RouteOriginIndicator currentRouteOrigin;
    private RouteDestinationIndicator currentRouteDestination;
    private RouteController routeController = new RouteController(this);
    private boolean showStreetNodeCloseToMouse = false;

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


    public void clearOriginDestination() {
        currentRouteOrigin = null;
        currentRouteDestination = null;
        repaint();
    }

    public Pin getCurrentPin() {
        return currentPin;
    }

    public boolean getShowStreetNodeCloseToMouse() {
        return showStreetNodeCloseToMouse;
    }

    public MapCanvas() {
        Drawer drawer = new Drawer(getGraphicsContext2D());
        Affine affine = new Affine();
        this.mapRenderer = new MapRenderer(drawer, affine);
        this.mapState = new MapState(null, Arrays.asList(Type.getTypes()), false, true );
    }

    public void repaint() {
         this.mapRenderer.repaint(this);
    }

    public void showDijkstraTree(){
        this.mapRenderer.showDijkstraTree(this.routeController);
    }

    public void setModel(Model model) {
        this.mapState.setModel(model);
        this.mapRenderer.resetView(model, this);
    }


    public void setPin (float lon, float lat){
        currentPin = new Pin(lon, lat, 1);
        repaint();
    }

    public void nullPin () {
        currentPin = null;
        repaint();
    }

    public void setRouteOrigin (Point2D point){
        if (point != null) {
            currentRouteOrigin = new RouteOriginIndicator((float) point.getX(), (float) point.getY(), 1);
        } else {
            currentRouteOrigin = null;
        }
        repaint();
    }

    public void setRouteDestination (Point2D point){
        if (point != null) {
            currentRouteDestination = new RouteDestinationIndicator((float) point.getX(), (float) point.getY(), 1);
        } else {
            currentRouteDestination = null;
        }
        repaint();
    }


    public void setStartDestPoint(Node start, Node dest) {
        this.startNode = start;
        this.destinationNode = dest;
    }

	public void showStreetNearMouse(MainController mainController, MouseEvent e) {
	    try {
	        Point2D translatedCoords = this.mapRenderer.getTrans().inverseTransform(e.getX(), e.getY());
	        Node nearestNode = (Node) mainController.model.getRoadKDTree().nearestNeighborForEdges(translatedCoords, "Walk");
	        long nodeAsLong = nearestNode.getAsLong();
	        Edge streetEdge = mainController.model.getGraph().getAdj().get(nodeAsLong).getFirst();
	        double bestAngle = Double.POSITIVE_INFINITY;


	        Point2D mouseRelativeToNodeVector = new Point2D(translatedCoords.getX() - nearestNode.getLon(), translatedCoords.getY() - nearestNode.getLat());

	        for (Edge edge : mainController.model.getGraph().getAdj().get(nearestNode.getAsLong())) {
	            Node otherNode = edge.otherNode(nodeAsLong);
	            Point2D otherNodeRelativeToNodeVector = new Point2D(otherNode.getLon() - nearestNode.getLon(), otherNode.getLat() - nearestNode.getLat());

	            double angle = Math.acos((mouseRelativeToNodeVector.getX() * otherNodeRelativeToNodeVector.getX() + mouseRelativeToNodeVector.getY() * otherNodeRelativeToNodeVector.getY()) / (mouseRelativeToNodeVector.magnitude() * otherNodeRelativeToNodeVector.magnitude()));

	            if (angle < bestAngle) {
	                bestAngle = angle;
	                streetEdge = edge;
	            }
	        }

	        String streetName = streetEdge.getStreet().getName();
	        if (streetName == null) {
                streetName = "Unnamed street";
            }
            repaint();
            this.mapRenderer.drawEdge(streetEdge);

	        if (getShowStreetNodeCloseToMouse()) {
                this.mapRenderer.drawNode(nearestNode);
	        }

	        this.mapRenderer.drawStreetName(streetName, this);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
    }
    
    public void setShowStreetNodeCloseToMouse(boolean newValue) {
        showStreetNodeCloseToMouse = newValue;
    }

    public MapRenderer getMapRenderer() {
        return mapRenderer;
    }

    public MapState getMapState() {
        return mapState;
    }

    public RouteController getRouteController() {
        return routeController;
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getDestinationNode() {
        return destinationNode;
    }

}

package bong.canvas;

import bong.OSMReader.Model;
import bong.OSMReader.Node;
import bong.routeFinding.Edge;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.NonInvertibleTransformException;

public class MapMouseInteraction {

    private boolean showStreetNodeCloseToMouse = false;

    public boolean getShowStreetNodeCloseToMouse() {
        return showStreetNodeCloseToMouse;
    }

    public void setShowStreetNodeCloseToMouse(boolean newValue) {
        showStreetNodeCloseToMouse = newValue;
    }

    public void showStreetNearMouse(Model model, MouseEvent e, MapCanvas canvas) {
        try {
            Point2D translatedCoords = getTranslatedCoords(e, canvas);
            Node nearestNode = findNearestNode(model, translatedCoords);
            Edge streetEdge = findBestStreetEdge(model, nearestNode, translatedCoords);
            String streetName = getStreetName(streetEdge);

            renderStreetDetails(canvas, nearestNode, streetEdge, streetName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Point2D getTranslatedCoords(MouseEvent e, MapCanvas canvas) throws NonInvertibleTransformException {
        return canvas.getMapRenderer().getTrans().inverseTransform(e.getX(), e.getY());
    }

    public Node findNearestNode(Model model, Point2D coords) {
        return model.getRoadKDTree().nearestNeighborForEdges(coords, "Walk");
    }

    public Edge findBestStreetEdge(Model model, Node nearestNode, Point2D translatedCoords) {
        long nodeAsLong = nearestNode.getAsLong();
        Edge bestEdge = model.getGraph().getAdj().get(nodeAsLong).getFirst();
        double bestAngle = Double.POSITIVE_INFINITY;

        Point2D mouseRelativeToNodeVector = new Point2D(
                translatedCoords.getX() - nearestNode.getLon(),
                translatedCoords.getY() - nearestNode.getLat()
        );

        for (Edge edge : model.getGraph().getAdj().get(nodeAsLong)) {
            Node otherNode = edge.otherNode(nodeAsLong);
            Point2D otherNodeRelativeToNodeVector = new Point2D(
                    otherNode.getLon() - nearestNode.getLon(),
                    otherNode.getLat() - nearestNode.getLat()
            );

            double angle = calculateAngle(mouseRelativeToNodeVector, otherNodeRelativeToNodeVector);

            if (angle < bestAngle) {
                bestAngle = angle;
                bestEdge = edge;
            }
        }

        return bestEdge;
    }

    public double calculateAngle(Point2D vector1, Point2D vector2) {
        return Math.acos((vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY()) /
                (vector1.magnitude() * vector2.magnitude()));
    }

    public String getStreetName(Edge streetEdge) {
        String streetName = streetEdge.getStreet().getName();
        return (streetName != null) ? streetName : "Unnamed street";
    }

    public void renderStreetDetails(MapCanvas canvas, Node nearestNode, Edge streetEdge, String streetName) {
        canvas.getMapRenderer().repaint(canvas);
        canvas.getMapRenderer().drawEdge(streetEdge);

        if (getShowStreetNodeCloseToMouse()) {
            canvas.getMapRenderer().drawNode(nearestNode);
        }

        canvas.getMapRenderer().drawStreetName(streetName, canvas);
    }
}

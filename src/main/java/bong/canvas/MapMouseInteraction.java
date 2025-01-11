package bong.canvas;

import bong.OSMReader.Model;
import bong.OSMReader.Node;
import bong.routeFinding.Edge;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

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
            Point2D translatedCoords = canvas.getMapRenderer().getTrans().inverseTransform(e.getX(), e.getY());
            Node nearestNode = model.getRoadKDTree().nearestNeighborForEdges(translatedCoords, "Walk");
            long nodeAsLong = nearestNode.getAsLong();
            Edge streetEdge = model.getGraph().getAdj().get(nodeAsLong).getFirst();
            double bestAngle = Double.POSITIVE_INFINITY;


            Point2D mouseRelativeToNodeVector = new Point2D(translatedCoords.getX() - nearestNode.getLon(), translatedCoords.getY() - nearestNode.getLat());

            for (Edge edge : model.getGraph().getAdj().get(nearestNode.getAsLong())) {
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
            canvas.getMapRenderer().repaint(canvas);
            canvas.getMapRenderer().drawEdge(streetEdge);

            if (getShowStreetNodeCloseToMouse()) {
                canvas.getMapRenderer().drawNode(nearestNode);
            }

            canvas.getMapRenderer().drawStreetName(streetName, canvas);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

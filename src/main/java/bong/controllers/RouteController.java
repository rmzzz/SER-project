package bong.controllers;

import bong.OSMReader.MercatorProjector;
import bong.OSMReader.Model;
import bong.OSMReader.Node;
import bong.canvas.MapCanvas;
import bong.model.RouteModel;
import bong.routeFinding.Dijkstra;
import bong.routeFinding.Edge;
import bong.routeFinding.Graph;
import bong.routeFinding.Instruction;
import bong.routeFinding.Street;
import javafx.geometry.Point2D;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteController {
    private Model model;
    private RouteModel routeModel;
    private MapCanvas canvas;

    public RouteController(Model model, MapCanvas canvas) {
        this.model = model;
        this.canvas = canvas;
        routeModel = new RouteModel();
        canvas.getMapState().setRouteModel(routeModel);
    }

    private ArrayList<Edge> route = new ArrayList<>();
    private Dijkstra dijkstra;

    private double routeTime;
    private double routeDistance;
    private int roundaboutCounter = 0;
    private Node lastInstructionNode;
    private String lastActionInstruction;

    public RouteModel getRouteModel() {
        return routeModel;
    }

    public Iterable<Edge> getRoute() {
        return route;
    }

    public void setLastInstructionNode(Node node) {
        lastInstructionNode = node;
    }

    public Dijkstra getDijkstra() {
        return dijkstra;
    }

    public void setRouteTime(double newTime) {
        routeTime = newTime;
    }

    public void setRouteDistance(double newDistance) {
        routeDistance = newDistance;
    }

    public List<Instruction> getInstructions() {
        return routeModel.getInstructions();
    }

    public String getLastActionInstruction() {
        return lastActionInstruction;
    }

    public void clearRoute() {
        route = null;
        dijkstra = null;
        lastInstructionNode = null;
        lastActionInstruction = null;
        if (routeModel.hasRoute()) {
            routeModel.clear();
            canvas.getMapRenderer().repaint(canvas);
        }
    }

    public double calculateTurn(Edge prevEdge, Edge currEdge) {
        Point2D prevVector = new Point2D(prevEdge.getHeadNode().getLon() - prevEdge.getTailNode().getLon(), prevEdge.getHeadNode().getLat() - prevEdge.getTailNode().getLat());
        Point2D currVector = new Point2D(currEdge.getHeadNode().getLon() - currEdge.getTailNode().getLon(), currEdge.getHeadNode().getLat() - currEdge.getTailNode().getLat());

        double prevDirection = Math.atan2(prevVector.getY(), prevVector.getX());
        double currDirection = Math.atan2(currVector.getY(), currVector.getX());
        double turn = currDirection - prevDirection;
        if (turn > Math.PI) {
            turn = - (turn - Math.PI);
        } else if (turn < - Math.PI) {
            turn = - (turn + Math.PI);
        }

        turn *= 180 / Math.PI;
        return turn;
    }

    public void resetRoundaboutCounter() {
        roundaboutCounter = 0;
    }

    public void setActionInstruction(Edge prevEdge, Edge currEdge, int roundaboutCounter) {
        double turn = calculateTurn(prevEdge, currEdge);
        if (currEdge.getStreet().getRole() == Street.Role.MOTORWAY && prevEdge.getStreet().getRole() == Street.Role.MOTORWAY_LINK) {
            lastActionInstruction = "Take the ramp onto the motorway";
        } else if (currEdge.getStreet().getRole() != Street.Role.MOTORWAY_LINK && currEdge.getStreet().getRole() != Street.Role.MOTORWAY && prevEdge.getStreet().getRole() == Street.Role.MOTORWAY_LINK) {
            lastActionInstruction = "Take the off-ramp";
        } else if (roundaboutCounter > 0) {
            lastActionInstruction = "Take exit number " + roundaboutCounter + " in the roundabout";
            resetRoundaboutCounter();
        } else if (turn > 20 && turn < 160 && currEdge.getStreet().getRole() != Street.Role.ROUNDABOUT) {
            lastActionInstruction = "Turn right";
        } else if (turn < -20 && turn > -160 && currEdge.getStreet().getRole() != Street.Role.ROUNDABOUT) {
            lastActionInstruction = "Turn left";
        }
    }

    public void addInstruction(String prevEdgeName, double tempLength, Edge currEdge) {
        String instruction = "Follow ";
        if (lastActionInstruction != null) {
            instruction = lastActionInstruction + " and follow ";
        }

        BigDecimal bd = new BigDecimal(tempLength);
        bd = bd.round(new MathContext(2));
        int roundedLength = bd.intValue();
        if (roundedLength >= 10000) {
            instruction += prevEdgeName + " for " + roundedLength / 1000 + " km";
        } else if (roundedLength > 1000) {
            instruction += prevEdgeName + " for " + (double) roundedLength / 1000 + " km";
        } else {
            instruction += prevEdgeName + " for " + roundedLength + " m";
        }
        routeModel.addInstruction(new Instruction(instruction, lastInstructionNode));
        lastActionInstruction = null;
        lastInstructionNode = currEdge.getTailNode();
    }

    public void addTimeToTotal(String vehicle, Edge currEdge, double distance) {
        switch (vehicle) {
            case "Car":
                routeTime += distance / (currEdge.getStreet().getMaxspeed() / 3.6);
                break;
            case "Walk":
                routeTime += distance / 1.1; //estimate for walking speed, 1.1 m/s.
                break;
            case "Bicycle":
                routeTime += distance / 6; //6 m/s biking speed estimate.
                break;
        }
    }

    public String timeString() {
        String timeString;
        int hourCount = 0;
        double timeInMinutes = routeTime / 60;

        while (timeInMinutes >= 60) {
            hourCount++;
            timeInMinutes -= 60;
        }

        if (hourCount > 0) {
            timeString = hourCount + " h " + (int) timeInMinutes + " min";
        } else {
            timeString = (int) timeInMinutes + " min";
        }
        return timeString;
    }

    public String distanceString() {
        BigDecimal bd = new BigDecimal(routeDistance);
        bd = bd.round(new MathContext(3));
        int roundedDistance = bd.intValue();
        String distanceString;

        if (routeDistance >= 100000) {
            distanceString = roundedDistance / 1000 + " km";
        } else if (routeDistance >= 1000) {
            distanceString = (double) roundedDistance / 1000 + " km";
        } else {
            distanceString = roundedDistance + " m";
        }
        return distanceString;
    }

    public void generateRouteInfo(ArrayList<Edge> list, String vehicle, Graph graph) {

        //instructions = new ArrayList<>();
        routeModel.getInstructions().clear(); // TODO Ramiz: check why instructions are reset here

        routeDistance = 0;
        routeTime = 0;

        Edge first = list.getFirst();
        String prevEdgeName = first.getStreet().getName();
        double tempLength = 0;
        Edge prevEdge = first;
        lastInstructionNode = list.getFirst().getTailNode();
        for (int i = 0; i < list.size(); i++) {
            Edge currEdge = list.get(i);
            double meterMultiplier = - (MercatorProjector.unproject(currEdge.getTailNode().getLon(), currEdge.getTailNode().getLat()).getLat()) / 100;

            if (currEdge.getStreet().getRole() == Street.Role.ROUNDABOUT && graph.getOutDegree(currEdge.getHeadNode().getAsLong(), vehicle) > 1) {
                roundaboutCounter++;
            }

            if (prevEdgeName == null) {
                prevEdgeName = "road";
            }
            String currEdgeName = currEdge.getStreet().getName();

            if (currEdgeName == null) {
                currEdgeName = "road";
            }

            if ((!prevEdgeName.equals(currEdgeName) && currEdge.getStreet().getRole() != Street.Role.ROUNDABOUT) || (currEdge.getStreet().getRole() != Street.Role.ROUNDABOUT && prevEdge.getStreet().getRole() == Street.Role.ROUNDABOUT)) {
                addInstruction(prevEdgeName, tempLength, currEdge);
                setActionInstruction(prevEdge, currEdge, roundaboutCounter);

                if (i == list.size() - 1) {
                    addInstruction(currEdgeName, currEdge.getWeight(), currEdge);
                }

                tempLength = currEdge.getWeight() * meterMultiplier;
            } else {
                tempLength += currEdge.getWeight() * meterMultiplier;
                if (i == list.size() - 1) {
                    addInstruction(prevEdgeName, tempLength, currEdge);
                }
            }

            prevEdgeName = currEdgeName;
            prevEdge = list.get(i);

            double distance = currEdge.getWeight() * 0.56;
            routeDistance += distance;
            addTimeToTotal(vehicle, currEdge, distance);
        }
        routeModel.addInstruction(new Instruction("You have arrived at your destination", list.getLast().getHeadNode()));
    }

    public ArrayList<Edge> singleDirectRoute(ArrayList<Edge> route) {
        ArrayList<Edge> singleDirectedRoute = new ArrayList<>();

        Edge firstEdge = route.get(0);
        Node prevNode;

        if (firstEdge.getTailNode().getAsLong() == route.get(1).getHeadNode().getAsLong() || firstEdge.getTailNode().getAsLong() == route.get(1).getTailNode().getAsLong()) {
            prevNode = firstEdge.getHeadNode();
        } else {
            prevNode = firstEdge.getTailNode();
        }

        for (Edge edge : route) {
            Node otherNode = edge.otherNode(prevNode.getAsLong());
            Edge newEdge = new Edge(prevNode, otherNode, edge.getStreet());
            singleDirectedRoute.add(newEdge);
            prevNode = otherNode;
        }
        return  singleDirectedRoute;
    }

    public void setRoute(boolean useBidirectional) {
        route = dijkstra.pathTo(dijkstra.getLastNode(), 1);

        lastInstructionNode = route.getFirst().getTailNode();
        if (useBidirectional) {
            ArrayList<Edge> secondPart = dijkstra.pathTo(dijkstra.getLastNode(), 2);
            Collections.reverse(secondPart);
            route.addAll(secondPart);
        }
        route = singleDirectRoute(route);

        float[] floats = new float[route.size() * 2 + 2];

        Edge firstEdge = route.getFirst();

        floats[0] = firstEdge.getTailNode().getLon();
        floats[1] = firstEdge.getTailNode().getLat();

        for (int i = 2; i < route.size() * 2 + 2; i += 2) {
            Node currentNode = route.get((i - 2) / 2).getHeadNode();
            floats[i] = currentNode.getLon();
            floats[i + 1] = currentNode.getLat();
        }

        routeModel.setDrawableRoute(floats);
        canvas.getMapState().setRouteModel(routeModel);
        canvas.getMapRenderer().repaint(canvas);
    }

    public void setDijkstra(long startPoint, long endPoint, String vehicle, boolean shortestRoute, boolean useBidirectional, boolean useAStar) throws Exception{
        dijkstra = new Dijkstra(model.getGraph(), startPoint, endPoint, vehicle, shortestRoute, useBidirectional, useAStar);
        setRoute(useBidirectional);
        generateRouteInfo(route, vehicle, model.getGraph());
        canvas.getMapState().setRouteModel(routeModel);
        canvas.getMapRenderer().repaint(canvas);
    }

    public double getRouteTime() {
        return routeTime;
    }

    public void setModel(Model model) {
        this.model = model;
        routeModel.clear();
    }
}

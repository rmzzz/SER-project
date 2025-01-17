package bong.controllers;

import bong.OSMReader.Model;
import bong.OSMReader.Node;
import bong.OSMReader.OSMReader;
import bong.canvas.MapCanvas;
import bong.routeFinding.Edge;
import bong.routeFinding.Graph;
import bong.routeFinding.Street;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RouteControllerTest {
    private RouteController routeController;

    @BeforeEach
    public void setUp() {
        routeController = new RouteController(null, new MapCanvas());
    }

    @Test
    public void addTimeToTotalByCar() {
        assertEquals(0, routeController.getRouteTime());
        Node node1 = new Node(1, 1, 1);
        Node node2 = new Node(2, 4, 5);
        Street street = new Street(new ArrayList<>(), 80);
        Edge edge = new Edge(node1, node2, street);

        routeController.addTimeToTotal("Car", edge, 50);

        assertEquals(2.25, routeController.getRouteTime());
    }
    @Test
    public void addTimeToTotalByWalk() {
        assertEquals(0, routeController.getRouteTime());
        Node node1 = new Node(1, 1, 1);
        Node node2 = new Node(2, 4, 5);
        Street street = new Street(new ArrayList<>(), 80);
        Edge edge = new Edge(node1, node2, street);

        routeController.addTimeToTotal("Walk", edge, 110);

        assertEquals(100, routeController.getRouteTime(), 0.0001);
    }

    @Test
    public void addTimeToTotalByBicycle() {
        assertEquals(0, routeController.getRouteTime());
        Node node1 = new Node(1, 1, 1);
        Node node2 = new Node(2, 4, 5);
        Street street = new Street(new ArrayList<>(), 80);
        Edge edge = new Edge(node1, node2, street);

        routeController.addTimeToTotal("Bicycle", edge, 66);

        assertEquals(11, routeController.getRouteTime());
    }

    @Test
    public void addTimeToTotalByInvalid() {
        assertEquals(0, routeController.getRouteTime());
        Node node1 = new Node(1, 1, 1);
        Node node2 = new Node(2, 4, 5);
        Street street = new Street(new ArrayList<>(), 80);
        Edge edge = new Edge(node1, node2, street);

        routeController.addTimeToTotal("bruh", edge, 66);

        assertEquals(0, routeController.getRouteTime());
    }

    @Test
    public void calculateTurnTest() {
        Edge prevEdge1 = new Edge(new Node(1, 5, 6), new Node(2, 9, 10), null);
        Edge currEdge1 = new Edge(new Node(2, 9, 10), new Node(2, 12, 5), null);

        double expected;
        double actual;
        expected = -104.03624346792648;
        actual = routeController.calculateTurn(prevEdge1, currEdge1);
        assertEquals(expected, actual);

        Edge prevEdge2 = new Edge(new Node(1, 5, 6), new Node(2, 9, 10), null);
        Edge currEdge2 = new Edge(new Node(2, 9, 10), new Node(2, 7, 12), null);
        actual = routeController.calculateTurn(prevEdge2, currEdge2);
        expected = 90.0;
        assertEquals(expected, actual);

        Edge prevEdge3 = new Edge(new Node(1, 5, 6), new Node(2, 4, 1), null);
        Edge currEdge3 = new Edge(new Node(2, 4, 1), new Node(3, 5, 7), null);
        actual = routeController.calculateTurn(prevEdge3, currEdge3);
        expected = -1.8476102659946245;
        assertEquals(expected, actual, 0.0001);

        Edge prevEdge4 = new Edge(new Node(1, 0, 0), new Node(2, -10, 1), null);
        Edge currEdge4 = new Edge(new Node(2, -10, 1), new Node(3, -11, -9), null);
        actual = routeController.calculateTurn(prevEdge4, currEdge4);
        expected = 90;
        assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void setActionInstructionTest() {
        Node node1 = new Node(1, 1, 1);
        Node node2 = new Node(2, 4, 5);

        ArrayList<String> tags = new ArrayList<>();
        tags.add("highway");
        tags.add("motorway_link");
        Street prevStreet = new Street(tags, 80);
        Edge prevEdge = new Edge(node1, node2, prevStreet);
        tags.clear();

        tags.add("highway");
        tags.add("motorway");

        Street currStreet = new Street(tags, 80);
        Edge currEdge = new Edge(node1, node2, currStreet);

        routeController.setActionInstruction(prevEdge, currEdge, 1);
        assertEquals("Take the ramp onto the motorway", routeController.getLastActionInstruction());

        tags.clear();
        tags.add("highway");
        tags.add("primary");
        currStreet = new Street(tags, 80);
        currEdge = new Edge(node1, node2, currStreet);

        routeController.setActionInstruction(prevEdge, currEdge, 1);
        assertEquals("Take the off-ramp", routeController.getLastActionInstruction());

        prevEdge = new Edge(node1, node2, currStreet);
        routeController.setActionInstruction(prevEdge, currEdge, 2);
        assertEquals("Take exit number 2 in the roundabout", routeController.getLastActionInstruction());

        Edge prevEdge1 = new Edge(new Node(1, 5, 6), new Node(2, 9, 10), currStreet);
        Edge currEdge1 = new Edge(new Node(2, 9, 10), new Node(2, 12, 5), currStreet);
        routeController.setActionInstruction(prevEdge1, currEdge1, 0);
        assertEquals("Turn left", routeController.getLastActionInstruction());

        Edge prevEdge2 = new Edge(new Node(1, 5, 6), new Node(2, 9, 10), currStreet);
        Edge currEdge2 = new Edge(new Node(2, 9, 10), new Node(2, 7, 12), currStreet);
        routeController.setActionInstruction(prevEdge2, currEdge2, 0);
        assertEquals("Turn right", routeController.getLastActionInstruction());

//        routeController = new RouteController(new MapCanvas());
        routeController.clearRoute();

        tags.add("junction");
        tags.add("roundabout");
        currStreet = new Street(tags, 50);

        Edge prevEdge3 = new Edge(new Node(1, 5, 6), new Node(2, 9, 10), currStreet);
        Edge currEdge3 = new Edge(new Node(2, 9, 10), new Node(2, 7, 12), currStreet);
        routeController.setActionInstruction(prevEdge3, currEdge3, 0);
        Assertions.assertNull(routeController.getLastActionInstruction());

        Edge prevEdge4 = new Edge(new Node(1, 5, 6), new Node(2, 9, 10), currStreet);
        Edge currEdge4 = new Edge(new Node(2, 9, 10), new Node(2, 12, 5), currStreet);
        routeController.setActionInstruction(prevEdge4, currEdge4, 0);
        Assertions.assertNull(routeController.getLastActionInstruction());

    }

    @Test
    public void timeStringTest() {
        String expected;
        String actual;

        routeController.setRouteTime(900.0);
        expected = 15 + " min";
        actual = routeController.timeString();
        assertEquals(expected, actual);

        routeController.setRouteTime(9000.0);
        expected = 2 + " h " + 30 + " min";
        actual = routeController.timeString();
        assertEquals(expected, actual);
    }

    @Test
    public void addInstructionTest() {
        routeController.setLastInstructionNode(new Node(1, 2, 3));
        Edge edge = new Edge(new Node(1, 5, 6), new Node(2, 9, 10), null);
        routeController.addInstruction("test street", 1500, edge);
        String expected = "Follow test street for 1.5 km";
        String actual = routeController.getInstructions().get(0).getInstruction();
        assertEquals(expected, actual);

        routeController.addInstruction("test street", 15000, edge);
        expected = "Follow test street for 15 km";
        actual = routeController.getInstructions().get(1).getInstruction();
        assertEquals(expected, actual);
    }

    @Test
    public void distanceStringTest() {
        String expected;
        String actual;

        routeController.setRouteDistance(101.1);
        expected = 101 + " m";
        actual = routeController.distanceString();
        assertEquals(expected, actual);

        routeController.setRouteDistance(1011.1);
        expected = 1.01 + " km";
        actual = routeController.distanceString();
        assertEquals(expected, actual);

        routeController.setRouteDistance(101234.56);
        expected = 101 + " km";
        actual = routeController.distanceString();
        assertEquals(expected, actual);
    }

    @Test
    public void singleDirectRouteTest() {
        ArrayList<Edge> route = new ArrayList<>();

        Node node0 = new Node(0, 0, 0);
        Node node1 = new Node(1, 1, 1);
        Node node2 = new Node(2, 2, 2);
        Node node3 = new Node(3, 3, 3);
        Node node4 = new Node(4, 4, 4);


        route.add(new Edge(node1, node0, null));
        route.add(new Edge(node1, node2, null));
        route.add(new Edge(node3, node2, null));
        route.add(new Edge(node3, node4, null));

        ArrayList<Edge> actual = routeController.singleDirectRoute(route);

        for (int i = 0; i < 4; i++) {
            assertEquals(i, actual.get(i).getTailNode().getLat());
        }

        route.clear();

        route.add(new Edge(node1, node0, null));
        route.add(new Edge(node2, node1, null));
        route.add(new Edge(node3, node2, null));
        route.add(new Edge(node3, node4, null));

        actual = routeController.singleDirectRoute(route);

        for (int i = 0; i < 4; i++) {
            assertEquals(i, actual.get(i).getTailNode().getLat());
        }
    }

    @Test
    void setRouteTest() throws Exception {
        Model model = new Model(new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/smallMapStmaz.osm")));
        MapCanvas canvas = new MapCanvas();
        canvas.getMapState().setModelWithoutReset(model);
        routeController = new RouteController(model, canvas);
        routeController.setDijkstra(11654496977L, 1677959389L, "Car", true, true, true);
        routeController.setRoute(true);

        var actualRoute = (ArrayList<Edge>) routeController.getRoute();
        var actualInstructions = routeController.getInstructions();
        var actualDijkstra = routeController.getDijkstra().getAllEdgeTo();
        var actualDrawableRoute = routeController.getRouteModel().getDrawableRoute();

        assertEquals(34, actualRoute.size());
        assertEquals(3, actualInstructions.size());
        assertEquals(92, actualDijkstra.size());
        assertEquals(70, actualDrawableRoute.length);
    }

    @Test
    void clearRouteTest() {
        try {
            Model model = new Model(new OSMReader(getClass().getClassLoader().getResourceAsStream("bong/smallMapStmaz.osm")));
            MapCanvas canvas = new MapCanvas();
            canvas.getMapState().setModelWithoutReset(model);
            routeController = new RouteController(model, canvas);
            routeController.setDijkstra(11654496977L, 1677959389L, "Car", true, true, true);
            routeController.setRoute(true);
            routeController.clearRoute();

            assertNull(routeController.getRoute());
            assertEquals(List.of(), routeController.getInstructions());
            assertNull(routeController.getDijkstra());
            assertNull(routeController.getRouteModel().getDrawableRoute());
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    void generateRouteInfoTest() {
        Graph graph = new Graph();

        ArrayList<Edge> route = new ArrayList<>();

        Node node0 = new Node(0, 0, 0);
        Node node1 = new Node(1, 1, 1);
        Node node2 = new Node(2, 2, 2);
        Node node3 = new Node(3, 3, 3);
        Node node4 = new Node(4, 4, 4);
        Node node5 = new Node(5, 3, 4);
        Node node6 = new Node(6, 5, 5);

        ArrayList<String> tags = new ArrayList<>();
        tags.add("highway");
        tags.add("primary");
        tags.add("junction");
        tags.add("roundabout");
        Street street = new Street(tags, 50);

        Edge edge0 = new Edge(node0, node1, street);
        Edge edge1 = new Edge(node1, node2, street);
        Edge edge2 = new Edge(node2, node3, street);
        Edge edge3 = new Edge(node3, node4, street);
        Edge edge4 = new Edge(node2, node5, street);

        tags.add("highway");
        tags.add("primary");
        street = new Street(tags, 50);

        Edge edge5 = new Edge(node4, node6, street);

        graph.addEdge(edge0);
        graph.addEdge(edge1);
        graph.addEdge(edge2);
        graph.addEdge(edge3);
        graph.addEdge(edge4);
        graph.addEdge(edge5);
        route.add(edge0);
        route.add(edge1);
        route.add(edge2);
        route.add(edge3);
        route.add(edge5);

        routeController.generateRouteInfo(route, "Car", graph);
        String actual = routeController.getInstructions().get(1).getInstruction();

        assertEquals("Take exit number 1 in the roundabout and follow road for 1 m", actual);
    }

}

package bong.controllers;

import bong.canvas.*;
import bong.routeFinding.Instruction;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DevController {
    Stage stage;
    MapCanvas canvas;
    RouteController routeController;
    List<Type> typesToBeDrawn = Arrays.asList(Type.getTypes());

    public DevController(Stage devStage, MapCanvas canvas, RouteController routeController) {
        this.stage = devStage;
        this.canvas = canvas;
        this.routeController = routeController;
    }

    @FXML private Button zoomIn;
    @FXML private Button zoomOut;
    @FXML private FlowPane filterTypes;
    @FXML private Button selectall;
    @FXML private Button deselectall;
    @FXML private CheckBox smartTraceToggle;
    @FXML private CheckBox colorToggle;
    @FXML private CheckBox citiesToggle;
    @FXML private CheckBox dependentDrawToggle;
    @FXML private TextField startPoint;
    @FXML private Button showDijkstra;
    @FXML private TextField endPoint;
    @FXML private Button findRoute;
    @FXML private ComboBox<String> vehicle;
    @FXML private Button clearRoute;
    @FXML private CheckBox shortestRoute;
    @FXML private Button routeDescription;
    @FXML private Button printPOI;
    @FXML private CheckBox fullscreenRange;
    @FXML private CheckBox drawBoundingBox;
    @FXML private CheckBox showClosestNode;
    @FXML private CheckBox drawBound;
    @FXML private CheckBox drawPrettyCitynames;
    @FXML private CheckBox showFoundRoadNode;
    @FXML private CheckBox useBidirectional;
    @FXML private CheckBox useAStar;

    @FXML
    public void initialize() {

        zoomIn.setOnAction(e -> canvas.getMapRenderer().zoom(2, canvas.getWidth() / 2, canvas.getHeight() / 2, canvas));

        zoomOut.setOnAction(e -> canvas.getMapRenderer().zoom(0.5, canvas.getWidth() / 2, canvas.getHeight() / 2, canvas));

        for (Type type : Type.getTypes()) {
            CheckBox c = new CheckBox(type.name());
            c.setUserData(type);
            c.setSelected(true);
            c.setOnAction(e -> updateTypesToBeDrawn());
            filterTypes.getChildren().add(c);
        }

        selectall.setOnAction(e -> {
            for (Node node : filterTypes.getChildren()) {
                CheckBox check = (CheckBox) node;
                check.setSelected(true);
            }
            canvas.getMapState().setTypesToBeDrawn(Arrays.asList(Type.getTypes()), canvas);
        });

        deselectall.setOnAction(e -> {
            for (Node node : filterTypes.getChildren()) {
                CheckBox check = (CheckBox) node;
                check.setSelected(false);
            }
            canvas.getMapState().setTypesToBeDrawn(new ArrayList<>(), canvas);
        });

        smartTraceToggle.setSelected(true);
        smartTraceToggle.setOnAction(e -> canvas.getMapRenderer().setTraceType(smartTraceToggle.isSelected(), canvas));

        colorToggle.setSelected(true);
        colorToggle.setOnAction(e -> canvas.getMapRenderer().setUseRegularColors(colorToggle.isSelected(), canvas));

        citiesToggle.setSelected(true);
        citiesToggle.setOnAction(e -> canvas.getMapRenderer().setShowCities(citiesToggle.isSelected(), canvas));

        dependentDrawToggle.setSelected(true);
        dependentDrawToggle.setOnAction(e -> canvas.getMapRenderer().setUseDependentDraw(dependentDrawToggle.isSelected(), canvas));

        vehicle.getItems().addAll("Walk", "Bicycle", "Car");
        vehicle.getSelectionModel().selectLast();

        showDijkstra.setOnAction(e -> canvas.getMapRenderer().showDijkstraTree(routeController.getDijkstra()));

        findRoute.setOnAction(e -> {
            try {
                routeController.setDijkstra(Long.parseLong(startPoint.getText()), Long.parseLong(endPoint.getText()), vehicle.getValue(), shortestRoute.isSelected(), useBidirectional.isSelected(), useAStar.isSelected());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        clearRoute.setOnAction(e -> routeController.clearRoute());

        shortestRoute.setSelected(true);

        routeDescription.setOnAction(e -> {
            for (Instruction instruction : routeController.getInstructions()) {
                System.out.println(instruction.getInstruction());
            }
        });

        printPOI.setOnAction(e -> {
            for (PointOfInterest poi : PointsOfInterestController.getPointsOfInterest()) {
                System.out.println(poi.toString());
            }
        });

        fullscreenRange.selectedProperty().set(canvas.getMapState().getRenderFullScreen());
        fullscreenRange.setOnAction(e -> {
            canvas.getMapState().setRenderFullScreen(fullscreenRange.isSelected());
            canvas.getMapRenderer().repaint(canvas);
        });

        drawBoundingBox.selectedProperty().set(MapRenderer.drawBoundingBox);
        drawBoundingBox.setOnAction(e -> {
            MapRenderer.drawBoundingBox = drawBoundingBox.isSelected();
            canvas.getMapRenderer().repaint(canvas);
        });

        showClosestNode.setSelected(false);
        showClosestNode.setOnAction(e -> canvas.getMapMouseInteraction().setShowStreetNodeCloseToMouse(showClosestNode.isSelected()));

        drawBound.setSelected(canvas.getMapRenderer().getDrawBound());
        drawBound.setOnAction(e -> canvas.getMapRenderer().setDrawBound(drawBound.isSelected()));
        
        drawPrettyCitynames.setSelected(City.getDrawPrettyCitynames());
        drawPrettyCitynames.setOnAction(e -> City.setDrawPrettyCitynames(drawPrettyCitynames.isSelected()));

        showFoundRoadNode.setSelected(canvas.getMapState().getShowRoadNodes());
        showFoundRoadNode.setOnAction(e -> canvas.getMapState().setShowRoadNodes(showFoundRoadNode.isSelected()));

        useBidirectional.setSelected(true);
        useAStar.setSelected(true);
    }

    private void updateTypesToBeDrawn() {
        typesToBeDrawn = new ArrayList<>();
        for (Node node : filterTypes.getChildren()) {
            CheckBox check = (CheckBox) node;
            if (check.isSelected()) typesToBeDrawn.add((Type) check.getUserData());
        }
        canvas.getMapState().setTypesToBeDrawn(typesToBeDrawn, canvas);
    }
}
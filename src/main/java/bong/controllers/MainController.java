package bong.controllers;

import bong.OSMReader.MercatorProjector;
import bong.OSMReader.Model;
import bong.OSMReader.Node;
import bong.OSMReader.OSMReader;
import bong.addressparser.Address;
import bong.canvas.*;
import bong.exceptions.ApplicationException;
import bong.exceptions.FileTypeNotSupportedException;
import bong.routeFinding.Instruction;
import bong.util.ResourceLoader;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainController {
    private Stage stage;
    private ResourceLoader resourceLoader;
    private Model model;
    private Point2D lastMouse;
    private ArrayList<Address> tempBest = new ArrayList<>();
    private boolean hasBeenDragged = false;
    private Address destinationAddress;
    private Address startAddress;
    private Address currentAddress;
    private Point2D destinationPoint;
    private Point2D startPoint;
    private Point2D currentPoint;
    PointsOfInterestController poiController;
    SearchController searchController;
    RouteController routeController;

    private ToggleGroup vehicleGroup = new ToggleGroup();
    @FXML private RadioButton carButton;
    @FXML private RadioButton bikeButton;
    @FXML private RadioButton walkButton;

    private ToggleGroup shortFastGroup = new ToggleGroup();
    @FXML private RadioButton shortButton;
    @FXML private RadioButton fastButton;

    private MapCanvas canvas;
    private boolean shouldPan = true;
    private boolean showStreetOnHover = false;

    public MainController(Stage primaryStage, ResourceLoader resourceLoader) {
        this.stage = primaryStage;
        this.resourceLoader = resourceLoader;
        new FileController();
        this.poiController = new PointsOfInterestController();
        this.searchController = new SearchController();
    }

    public void setMapBinaryFromPath(String mapName) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("bong/" + mapName + ".bin");
            setModelFromBinary(is);
        } catch (Exception e) {
            AlertController.showError("An error occurred", "Failed to load map of " + mapName, e);
        }
    } 
    
    public void setDefaultMap(){
        setMapBinaryFromPath("copenhagen");
    }

    @FXML private VBox welcomeOverlay;
    @FXML private VBox mainView;
    @FXML private Button welcomeDenmark;
    @FXML private Button welcomeCopenhagen;
    @FXML private Button welcomeCustom;

    @FXML private StackPane stackPane;
    @FXML MapCanvasWrapper mapCanvasWrapper;
    @FXML private MenuBar menu;
    @FXML private MenuItem loadClick;
    @FXML private MenuItem loadDefaultMap;
    @FXML private MenuItem loadDenmark;
    @FXML private MenuItem saveAs;
    @FXML private MenuItem devtools;
    @FXML private MenuItem about;
    @FXML private MenuItem help;
    @FXML private TextField searchField;
    @FXML private VBox suggestionsContainer;
    @FXML private Menu myPoints;
    @FXML private HBox pinInfo;
    @FXML private Label pointAddress;
    @FXML private Label pointCoords;
    @FXML private Button POIButton;
    @FXML private Button setAsDestination;
    @FXML private Button setAsStart;
    @FXML private VBox routeInfo;
    @FXML private Label routeDistance;
    @FXML private Label routeTime;
    @FXML private VBox directions;
    @FXML private Menu view;
    @FXML private CheckMenuItem publicTransport;
    @FXML private CheckMenuItem darkMode;
    @FXML private CheckMenuItem hoverToShowStreet;
    @FXML private MenuItem zoomToArea;
    @FXML private Button findRoute;
    @FXML private VBox directionsInfo;
    @FXML private Label startLabel;
    @FXML private Label destinationLabel;
    @FXML private HBox vehicleSelection;
    @FXML private HBox shortestFastestSelection;
    @FXML private Label noRouteFound;
    @FXML private Button cancelRoute;
    @FXML private Button pinInfoClose;
    @FXML private Button swap;
    
    @FXML
    public void initialize() {

        mainView.setDisable(true);
        mainView.setFocusTraversable(false);
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, this::onWindowShown);

        loadClick.setOnAction(e -> loadFileOnClick());

        canvas = mapCanvasWrapper.mapCanvas;
        routeController = new RouteController(model, canvas);

        canvas.setOnMousePressed(this::onCanvasMousePressed);
        canvas.setOnMouseDragged(this::onCanvasMouseDragged);
        canvas.setOnMouseReleased(this::onCanvasMouseReleased);
        canvas.setOnScroll(this::onCanvasScroll);

        loadDefaultMap.setOnAction(e -> setDefaultMap());

        loadDenmark.setOnAction(e -> setMapBinaryFromPath("denmark"));

        initWelcomeOverlay();

        saveAs.setOnAction(this::saveFileOnClick);

        devtools.setOnAction(this::onDevToolsAction);

        publicTransport.setSelected(true);
        publicTransport.setOnAction(e -> updateShowPublicTransport(publicTransport.isSelected()));

        darkMode.setSelected(false);
        darkMode.setOnAction(this::onDarkModeAction);

        hoverToShowStreet.setSelected(showStreetOnHover);
        hoverToShowStreet.setOnAction(this::onHoverToShowStreetAction);

        zoomToArea.setOnAction(e -> shouldPan = false);

        about.setOnAction(e -> openAbout());

        help.setOnAction(e -> openHelp());

        setAsDestination.setTooltip(setupTooltip("Set as destination"));
        setAsDestination.setOnAction(this::onSetAsDestinationAction);

        setAsStart.setTooltip(setupTooltip("Set as start"));
        setAsStart.setOnAction(this::onSetAsStartAction);

        pinInfoClose.setOnAction(this::onPinInfoCloseAction);

        findRoute.setOnAction(this::onFindRouteAction);

        canvas.setOnMouseMoved(this::onCanvasMouseMoved);

        swap.setOnAction(e -> swapStartAndDestination());

        setRouteOptionButtons();

        searchField.focusedProperty().addListener(this::onSearchFieldFocusedChanged);
        searchField.textProperty().addListener(this::onSearchFieldTextChanged);
        searchField.addEventFilter(KeyEvent.KEY_PRESSED, this::onSearchFieldKeyPressed);
        searchField.setOnAction(this::onSearchFieldAction);
    }

    void initWelcomeOverlay() {
        welcomeDenmark.setOnAction(e -> {
            setMapBinaryFromPath("denmark");
            closeWelcomeOverlay();
        });

        welcomeCopenhagen.setOnAction(e -> closeWelcomeOverlay());

        welcomeCustom.setOnAction(e -> {
            if (loadFileOnClick()) {
                closeWelcomeOverlay();
            }
        });
    }

    void onDevToolsAction(ActionEvent event) {
        Optional<ButtonType> result = AlertController.showConfirmation("Open dev tools?",
                "Dev tools are only supposed to be used by developers or advanced users");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            openDevTools();
        }
    }

    void onDarkModeAction(ActionEvent event) {
        canvas.getMapRenderer().setUseRegularColors(!darkMode.isSelected(), canvas);
    }

    void onHoverToShowStreetAction(ActionEvent event) {
        showStreetOnHover = hoverToShowStreet.isSelected();
        canvas.getMapRenderer().repaint(canvas);
    }

    void onSetAsDestinationAction(ActionEvent event) {
        routeController.clearRoute();
        destinationAddress = currentAddress;
        destinationPoint = currentPoint;
        canvas.getMapRouteManager().setRouteDestination(destinationPoint, canvas);
        showDirectionsMenu();
    }

    void onSetAsStartAction(ActionEvent event) {
        routeController.clearRoute();
        startAddress = currentAddress;
        startPoint = currentPoint;
        canvas.getMapRouteManager().setRouteOrigin(startPoint, canvas);
        showDirectionsMenu();
    }

    void onPinInfoCloseAction(ActionEvent e) {
        canvas.getMapPinManager().nullPin(canvas);
        hidePinInfo();
    }

    void onFindRouteAction(ActionEvent event) {
        try {
            findRouteFromGivenInputs();
            showDirectionsMenu();
        } catch (Exception ex) {
            routeInfo.setVisible(false);
            routeInfo.setManaged(false);
            noRouteFound.setVisible(true);
            noRouteFound.setManaged(true);
            routeController.clearRoute();
            ex.printStackTrace();
        }
    }

    void onCanvasMouseMoved(MouseEvent e) {
        if (showStreetOnHover) {
            canvas.getMapMouseInteraction().showStreetNearMouse(model, e, canvas);
        }
    }

    void onSearchFieldFocusedChanged(ObservableValue<?> obs, Boolean oldVal, Boolean newVal) {
        if (newVal) {
            searchField.setText(searchController.getCurrentQuery());
        }
    }

    void onSearchFieldTextChanged(ObservableValue<?> obs, String oldVal, String newVal) {
        hidePinInfo();
        if (searchField.isFocused()) {
            setCurrentQuery(searchField.getText().trim());
        }
        if (searchField.getText().isEmpty()) {
            suggestionsContainer.getChildren().clear();
        }
        canvas.getMapPinManager().nullPin(canvas);
    }

    void onSearchFieldKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB) {
            event.consume();
        }
        if (event.getCode() == KeyCode.DOWN) {
            if(!suggestionsContainer.getChildren().isEmpty()) {
                suggestionsContainer.getChildren().getFirst().requestFocus();
            }
            event.consume();
        }
    }

    void onSearchFieldAction(ActionEvent e) {
        if(!suggestionsContainer.getChildren().isEmpty()) {
            SuggestionButton b = (SuggestionButton) suggestionsContainer.getChildren().getFirst();
            Address a = b.getAddress();
            goToAddress(a);
        }
    }

    void onCanvasScroll(ScrollEvent e) {
        double factor = Math.pow(1.004,e.getDeltaY());
        canvas.getMapRenderer().zoom(factor,e.getX(),e.getY(), canvas);
    }

    void onCanvasMouseReleased(MouseEvent e) {
        if (!hasBeenDragged && shouldPan) {
            placePin();
        }
        if (!shouldPan) {
            Point2D end = new Point2D(e.getX(), e.getY());
            zoomToArea(end);
        }

        shouldPan = true;
        hasBeenDragged = false;
        canvas.getMapRenderer().setDraggedSquare(null, canvas);
    }

    void onCanvasMouseDragged(MouseEvent e) {
        hasBeenDragged = true;

        if (shouldPan) {
            canvas.getMapRenderer().pan(e.getX() - lastMouse.getX(), e.getY() - lastMouse.getY(), canvas);
            lastMouse = new Point2D(e.getX(), e.getY());
        } else {
            setLinePathForDrawedSquare(e);
        }
    }

    void onCanvasMousePressed(MouseEvent e) {
        lastMouse = new Point2D(e.getX(), e.getY());
    }

    void onWindowShown(WindowEvent windowEvent) {
        setDefaultMap();

        poiController.loadPointsOfInterest();
        for (PointOfInterest poi : PointsOfInterestController.getPointsOfInterest()) {
            addItemToMyPoints(poi);
        }
    }

    private Tooltip setupTooltip(String message){
        Tooltip tip = new Tooltip(message);
        tip.setShowDelay(Duration.ZERO);
        return tip;
    }

    private void closeWelcomeOverlay() {
        mainView.setDisable(false);
        mainView.setFocusTraversable(true);
        welcomeOverlay.setVisible(false);
    }

    public void setCurrentQuery(String newQuery){
        searchController.setCurrentQuery(newQuery);
        tempBest = searchController.getBestMatches(newQuery, model.getAddresses(), 5);
        updateSuggestionsContainer();
    }

    public void updateSuggestionsContainer(){
        ArrayList<Address> best = tempBest;
        ArrayList<SuggestionButton> bs = new ArrayList<>();
        for (Address address : best) {
            String addressString = address.toString();

                SuggestionButton b = setUpSuggestionButton(address, addressString);
                bs.add(b);
        }
        suggestionsContainer.getChildren().clear();
        for (SuggestionButton b : bs) suggestionsContainer.getChildren().add(b);
    }

    private SuggestionButton setUpSuggestionButton(Address address, String addressString) {
        SuggestionButton b = new SuggestionButton(address);
        b.setOnAction(e -> goToAddress(b.getAddress()));
        b.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                setCurrentQuery(((SuggestionButton) event.getSource()).getAddress().toString());
                searchField.requestFocus();
                searchField.positionCaret(searchField.getText().length());
                event.consume();
            }
            if (event.getCode() == KeyCode.A && event.isControlDown()) {
                searchField.requestFocus();
            }
        });
        b.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                Address a = b.getAddress();
                searchField.setText(a.toString());
                peekAddress(a);
            }
        });
        return b;
    }

    public void goToAddress(Address a) {
        setCurrentQuery(a.toString());
        searchField.setText(a.toString());
        searchField.positionCaret(searchField.getText().length());
        canvas.getMapRenderer().zoomToPoint(1, a.getLon(),  a.getLat(), canvas);
        canvas.getMapPinManager().setPin(a.getLon(), a.getLat(), canvas);
        suggestionsContainer.getChildren().clear();
        showPinMenu();
    }

    private void peekAddress(Address a) {
        searchField.setText(a.toString());
        canvas.getMapRenderer().zoomToPoint(1, a.getLon(),  a.getLat(), canvas);
        canvas.getMapPinManager().setPin(a.getLon(), a.getLat(), canvas);
        showPinMenu();
    }

    private void setRouteOptionButtons() {
        carButton.setToggleGroup(vehicleGroup);
        carButton.setUserData("Car");
        bikeButton.setToggleGroup(vehicleGroup);
        bikeButton.setUserData("Bicycle");
        walkButton.setToggleGroup(vehicleGroup);
        walkButton.setUserData("Walk");
        carButton.setSelected(true);

        bikeButton.setOnAction(e -> disableShortFastChoice());
        walkButton.setOnAction(e -> disableShortFastChoice());
        carButton.setOnAction(e -> {
            shortButton.setDisable(false);
            fastButton.setDisable(false);
        });

        shortButton.setToggleGroup(shortFastGroup);
        fastButton.setToggleGroup(shortFastGroup);
        fastButton.setSelected(true);

        cancelRoute.setOnAction(e -> {
            routeController.clearRoute();
            startAddress = null;
            destinationAddress = null;
            destinationPoint = null;
            startPoint = null;
            canvas.getMapRouteManager().clearOriginDestination(canvas);
            directionsInfo.setVisible(false);
        });
    }

    private void findRouteFromGivenInputs() throws Exception {
        RadioButton selectedVehicleButton = (RadioButton) vehicleGroup.getSelectedToggle();
        String vehicle = (String) selectedVehicleButton.getUserData();
        RadioButton selectedShortFastButton = (RadioButton) shortFastGroup.getSelectedToggle();
        boolean shortestRoute = selectedShortFastButton.getText().equals("Shortest");

        Node startNode = ((Node) model.getRoadKDTree().nearestNeighborForEdges(startPoint, vehicle));
        Node destinationNode = ((Node) model.getRoadKDTree().nearestNeighborForEdges(destinationPoint, vehicle));
        canvas.getMapRouteManager().setStartDestPoint(startNode, destinationNode);

        long startRoadId = startNode.getAsLong();
        long destinationRoadId = destinationNode.getAsLong();

        routeController.setDijkstra(startRoadId, destinationRoadId, vehicle, shortestRoute, true, true);
    }

    private void openHelp() {
        try {
            Stage helpStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(resourceLoader.getViewResource("Help.fxml"));
            Parent root = fxmlLoader.load();
            helpStage.setTitle("Help");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(resourceLoader.getViewResource("style.css").toExternalForm());
            helpStage.getIcons().add(new Image(resourceLoader.getViewResourceAsStream("bongIcon.png")));
            helpStage.setScene(scene);
            helpStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openAbout() {
        try {
            Stage aboutStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(resourceLoader.getViewResource("about.fxml"));
            Parent root = fxmlLoader.load();
            aboutStage.setTitle("About");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(resourceLoader.getViewResource("style.css").toExternalForm());
            aboutStage.getIcons().add(new Image(resourceLoader.getViewResourceAsStream("bongIcon.png")));
            aboutStage.setScene(scene);
            aboutStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openDevTools() {
        try {
            Stage devStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(resourceLoader.getViewResource("devview.fxml"));
            DevController devController = new DevController(devStage, canvas, routeController);
            fxmlLoader.setController(devController);
            Parent root = fxmlLoader.load();
            devStage.setTitle("dev tools");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(resourceLoader.getViewResource("style.css").toExternalForm());
            devStage.getIcons().add(new Image(resourceLoader.getViewResourceAsStream("bongIcon.png")));
            devStage.setScene(scene);
            devStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void placePin() {
        try {
            Point2D point2D = canvas.getMapRenderer().getTrans().inverseTransform(lastMouse.getX(), lastMouse.getY());
            canvas.getMapPinManager().setPin((float) point2D.getX(), (float) point2D.getY(), canvas);
            showPinMenu();
        } catch (NonInvertibleTransformException ex) {
            ex.printStackTrace();
        }
    }

    private void setLinePathForDrawedSquare(MouseEvent e) {
        try {
            Point2D corner0 = canvas.getMapRenderer().getTrans().inverseTransform(lastMouse.getX(), lastMouse.getY());
            Point2D corner1 = canvas.getMapRenderer().getTrans().inverseTransform(lastMouse.getX(), e.getY());
            Point2D corner2 = canvas.getMapRenderer().getTrans().inverseTransform(e.getX(), e.getY());
            Point2D corner3 = canvas.getMapRenderer().getTrans().inverseTransform(e.getX(), lastMouse.getY());

            float[] floats = {
                    (float) corner0.getX(), (float) corner0.getY(),
                    (float) corner1.getX(), (float) corner1.getY(),
                    (float) corner2.getX(), (float) corner2.getY(),
                    (float) corner3.getX(), (float) corner3.getY(),
                    (float) corner0.getX(), (float) corner0.getY(),
            };
            LinePath linePath = new LinePath(floats);
            canvas.getMapRenderer().setDraggedSquare(linePath, canvas);
        } catch (NonInvertibleTransformException ex) {
            ex.printStackTrace();
        }
    }

    private void disableShortFastChoice() {
        shortButton.setSelected(true);
        fastButton.setDisable(true);
        shortButton.setDisable(true);
    }

    private void updateShowPublicTransport(boolean showPublicTransport) {
        ArrayList<Type> typesToBeDrawn = new ArrayList<>();
        if (showPublicTransport) {
            typesToBeDrawn.addAll(Arrays.asList(Type.getTypes()));
        } else {
            for (Type type : Type.getTypes()) {
                if (type != Type.RAILWAY) {
                    typesToBeDrawn.add(type);
                }
            }
        }

        canvas.getMapState().setTypesToBeDrawn(typesToBeDrawn, canvas);
    }

    private void addItemToMyPoints(PointOfInterest poi) {
        MenuItem item = new MenuItem(poi.getName());
        item.setOnAction(a -> {
            canvas.getMapPinManager().setPin(poi.getLon(), poi.getLat(), canvas);
            canvas.getMapRenderer().zoomToPoint(1, poi.getLon(), poi.getLat(), canvas);
            showPinMenu();
        });
        myPoints.getItems().add(item);
    }

    private void zoomToArea(Point2D end) {
        Point2D inversedStart = null;
        Point2D inversedEnd = null;
        try {
            inversedStart = canvas.getMapRenderer().getTrans().inverseTransform(lastMouse.getX(), lastMouse.getY());
            inversedEnd = canvas.getMapRenderer().getTrans().inverseTransform(end.getX(), end.getY());
        } catch (NonInvertibleTransformException e) {
            AlertController.showError("Unexpected error", "Could not zoom to area", e);
            return;
        }
        Point2D centerPoint = new Point2D((inversedEnd.getX() + inversedStart.getX()) / 2, (inversedEnd.getY() + inversedStart.getY()) / 2);

        var factor = getZoomFactor(end);
        canvas.getMapRenderer().zoomToPoint(factor, (float) centerPoint.getX(), (float) centerPoint.getY(), canvas);
    }

    double getZoomFactor(Point2D end) {
        double windowAspectRatio = canvas.getWidth() / canvas.getHeight();
        double markedAspectRatio = (end.getX() - lastMouse.getX()) / (end.getY() - lastMouse.getY());
        double factor;

        if (windowAspectRatio < markedAspectRatio) {
            factor = Math.abs((canvas.getWidth() / (end.getX() - lastMouse.getX())) * canvas.getMapRenderer().getTrans().getMxx());
        } else {
            factor = Math.abs((canvas.getHeight() / (end.getY() - lastMouse.getY()) * canvas.getMapRenderer().getTrans().getMxx()));
        }
        if (factor > 2.2) {
            factor = 2.2;
        }
        return factor;
    }

    public void setPOIButton() {
        AtomicBoolean POIExists = new AtomicBoolean(false);

        if (poiController.POIContains(canvas.getMapPinManager().getCurrentPin().getCenterX(), canvas.getMapPinManager().getCurrentPin().getCenterY())) {
            POIExists.set(true);

            POIButton.setTooltip(setupTooltip("Remove point of interest"));
            POIButton.getStyleClass().removeAll("POIButton-add");
            POIButton.getStyleClass().add("POIButton-remove");
        } else {
            POIExists.set(false);
            POIButton.setTooltip(setupTooltip("Add to points of interest"));
            POIButton.getStyleClass().removeAll("POIButton-remove");
            POIButton.getStyleClass().add("POIButton-add");
        }

        POIButton.setOnAction(e -> {
            if (!POIExists.get()) {
                showAddPointDialog(currentPoint);
                myPoints.getItems().clear();
                poiController.loadPointsOfInterest();
                for (PointOfInterest poi : PointsOfInterestController.getPointsOfInterest()) {
                    addItemToMyPoints(poi);
                }

                poiController.savePointsOfInterest();
                POIExists.set(true);
                setPOIButton();
            } else {
                poiController.removePOI(canvas.getMapPinManager().getCurrentPin().getCenterX(), canvas.getMapPinManager().getCurrentPin().getCenterY());
                myPoints.getItems().clear();
                for (PointOfInterest poi : PointsOfInterestController.getPointsOfInterest()) {
                    addItemToMyPoints(poi);
                }
                POIExists.set(false);
                setPOIButton();
            }
        });
    }

    public void showAddPointDialog(Point2D point) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Point of interest");
        dialog.setContentText("Save point of interest");
        dialog.setHeaderText("Enter the name of the point");
        dialog.setContentText("Name:");
        Optional<String> givenName = dialog.showAndWait();
        poiController.addPointOfInterest(point, givenName);
    }

    public void showPinMenu() {
        setPOIButton();
        Node unprojected = MercatorProjector.unproject(canvas.getMapPinManager().getCurrentPin().getCenterX(), canvas.getMapPinManager().getCurrentPin().getCenterY());
        pointCoords.setText(-unprojected.getLat() + "°N " + unprojected.getLon() + "°E");

        if (model != null && model.getAddressKDTree() != null && canvas.getMapPinManager().getCurrentPin() != null) {
            currentAddress = (Address) model.getAddressKDTree().nearestNeighbor(new Point2D(canvas.getMapPinManager().getCurrentPin().getCenterX(), canvas.getMapPinManager().getCurrentPin().getCenterY()));
        }
            currentPoint = new Point2D(canvas.getMapPinManager().getCurrentPin().getCenterX(), canvas.getMapPinManager().getCurrentPin().getCenterY());

        if (currentAddress == null) {
            pointAddress.setText("No nearby address");
        } else {
            double distance = distanceInMeters(canvas.getMapPinManager().getCurrentPin().getCenterX(), canvas.getMapPinManager().getCurrentPin().getCenterY(), currentAddress.getLon(), currentAddress.getLat());
            if (distance > 80) {
                pointAddress.setText("No nearby address");
            } else {
                pointAddress.setText(currentAddress.toString());
            }
        }


        pinInfo.setTranslateY(10);
        pinInfo.setVisible(true);
    }

    private double dist(Point2D p1, Point2D p2){
        var dx = p2.getX() - p1.getX();
        var dy = p2.getY() - p1.getY();
        return Math.sqrt(dx * dx + dy * dy) * 0.56;
    }

    public void showDirectionsMenu() {
        noRouteFound.setVisible(false);
        noRouteFound.setManaged(false);

        setStartOrDestinationLabel(startAddress, startPoint, startLabel);

        setStartOrDestinationLabel(destinationAddress, destinationPoint, destinationLabel);

        findRoute.setDisable(startAddress == null || destinationAddress == null);

        List<Instruction> instructions = routeController.getInstructions();
        if (!instructions.isEmpty()) {
            directions.getChildren().clear();
            for (Instruction instruction : instructions) {
                Button button = new Button(instruction.getInstruction());
                button.getStyleClass().add("instruction");
                button.setOnAction(e -> canvas.getMapRenderer().zoomToNode(instruction.getNode(), canvas));
                directions.getChildren().add(button);
            }
            routeDistance.setText(routeController.distanceString());
            routeTime.setText(routeController.timeString());
        }

        if (routeController.getRoute() != null) {
            routeInfo.setVisible(true);
            routeInfo.setManaged(true);
        } else {
            routeInfo.setVisible(false);
            routeInfo.setManaged(false);
        }

        directionsInfo.setVisible(true);
    }

    private void setStartOrDestinationLabel(Address address, Point2D point, Label label) {
        if (address != null) {
            if (dist(point, address.getCentroid()) > 80) {
                Node unprojected = MercatorProjector.unproject(address.getCentroid().getX(), address.getCentroid().getY());
                label.setText(-unprojected.getLat() + "°N " + unprojected.getLon() + "°E");
            } else {
                label.setText(address.toString());
            }
        } else {
            label.setText("Not set");
        }
    }

    private double distanceInMeters(float pinX, float pinY, float addressX, float addressY) {
        double meterMultiplier = - (MercatorProjector.unproject(pinX, pinY).getLat()) / 100;
        double distance = Math.sqrt(Math.pow(pinX - addressX, 2) + Math.pow(pinY - addressY, 2));
        return distance * meterMultiplier;
    }

    public void hidePinInfo(){
        pinInfo.setVisible(false);
    }

    public void saveFileOnClick(ActionEvent e){
        try {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Binary file (*.bin)", "*.bin");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setInitialFileName("myMap");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                FileController.saveBinary(file, model);
                AlertController.showInfo("Saved successfully");
            }
        } catch (Exception ex) {
            AlertController.showError("Something unexpected happened, please try again", null, ex);
        }
    }

    public boolean loadFileOnClick() {
        try {
            List<String> acceptedFileTypes = new ArrayList<>();
            acceptedFileTypes.add("*.bin");
            acceptedFileTypes.add("*.osm");
            acceptedFileTypes.add("*.zip");

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Binary, OSM or ZIP file", acceptedFileTypes);
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setInitialFileName("myMap");
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                loadFile(file);
                return true;
            } 
        } catch (FileTypeNotSupportedException ex) {
            AlertController.showError("File type not supported",
                    "File type not supported: " + ex.getFileType(), ex);
        } catch (ApplicationException ex) {
            AlertController.showError(ex);
        } catch (Exception ex) {
            AlertController.showError("Unexpected error",
                    "Something unexpected happened, please try again", ex);
        }
        return false;
    }

    public void loadFile(File file) throws Exception {
        FileInputStream is = new FileInputStream(file);
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        switch (fileExtension) {
            case ".bin":
                setModelFromBinary(is);
                break;
            case ".osm":
                canvas.getMapState().setTypesToBeDrawn(new ArrayList<>(), canvas);

                OSMReader reader = new OSMReader(is);
                setModel(new Model(reader));
                reader.destroy();

                ArrayList<Type> list = new ArrayList<>(Arrays.asList(Type.getTypes()));
                canvas.getMapState().setTypesToBeDrawn(list, canvas);
                break;
            case ".zip":
                loadFile(FileController.loadZip(file));
                break;
            default:
                is.close();
                throw new FileTypeNotSupportedException(fileExtension);
        }
        is.close();
    }

    private void setModelFromBinary(InputStream is) throws IOException, ClassNotFoundException {
        setModel((Model) FileController.loadBinary(is));
    }

    private void setModel(Model model) {
        this.model = model;
        mapCanvasWrapper.mapCanvas.getMapState().setModel(model, mapCanvasWrapper.mapCanvas);
        routeController.setModel(model);
    }

    private void swapStartAndDestination() {
        Address tempAddress = startAddress;
        Point2D tempPoint = startPoint;
        startAddress = destinationAddress;
        startPoint = destinationPoint;
        destinationAddress = tempAddress;
        destinationPoint = tempPoint;
        canvas.getMapRouteManager().setRouteOrigin(startPoint, canvas);
        canvas.getMapRouteManager().setRouteDestination(destinationPoint, canvas);
        showDirectionsMenu();
        canvas.getMapRenderer().repaint(canvas);
    }

    public MapCanvas getCanvas(){
        return canvas;
    }

}

package bong.canvas;

import bong.OSMReader.Model;
import bong.model.RouteModel;

import java.util.List;

public class MapState {
    private Model model;
    private RouteModel routeModel;
    private List<Type> typesToBeDrawn;
    private boolean showRoadNodes;
    private boolean renderFullScreen;

    public MapState(Model model, List<Type> typesToBeDrawn, boolean showRoadNodes, boolean renderFullScreen) {
        this.model = model;
        this.typesToBeDrawn = typesToBeDrawn;
        this.showRoadNodes = showRoadNodes;
        this.renderFullScreen = renderFullScreen;
    }

    public Model getModel() {
        return model;
    }

    public RouteModel getRouteModel() {
        return routeModel;
    }

    public boolean getShowRoadNodes() {
        return showRoadNodes;
    }


    public boolean getRenderFullScreen() {
        return renderFullScreen;
    }

    public void setTypesToBeDrawn(List<Type> typesToBeDrawn, MapCanvasInterface mapCanvas) {
        this.typesToBeDrawn = typesToBeDrawn;
        mapCanvas.getMapRenderer().repaint(mapCanvas);
    }

    public List<Type> getTypesToBeDrawn() {
        return this.typesToBeDrawn;
    }

    public void setShowRoadNodes(boolean showRoadNodes) {
        this.showRoadNodes = showRoadNodes;
    }

    public void setModel(Model model, MapCanvasInterface mapCanvas) {
        this.model = model;
        mapCanvas.getMapRenderer().resetView(this.model, mapCanvas);
    }

    public void setRouteModel(RouteModel routeModel) {
        this.routeModel = routeModel;
    }

    public void setRenderFullScreen(boolean renderFullScreen) {
        this.renderFullScreen = renderFullScreen;
    }

    public void setModelWithoutReset(Model model) {
        this.model = model;
    }



}
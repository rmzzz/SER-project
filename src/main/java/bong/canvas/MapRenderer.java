package bong.canvas;

import bong.OSMReader.Bound;
import bong.OSMReader.KDTree;
import bong.OSMReader.Model;
import bong.OSMReader.Node;
import bong.routeFinding.Dijkstra;
import bong.routeFinding.Edge;
import bong.routeFinding.Instruction;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.Map;

public class MapRenderer {
    private Drawer gc;
    private Affine trans;
    private boolean useRegularColors = true;
    private boolean smartTrace = true;
    private boolean drawBound = false;
    private boolean showCities = true;
    private Range renderRange;
    private ScaleBar scaleBar;
    private LinePath draggedSquare;
    private double pixelwidth;
    private boolean useDependentDraw = true;
    public static boolean drawBoundingBox;

    public MapRenderer(Drawer gc, Affine trans) {
        this.gc = gc;
        this.trans = trans;
        this.scaleBar = new ScaleBar();
    }

    public void drawNode(Node node) {
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(20);
        new LinePath(node, node).draw(gc, this.pixelwidth, false);
    }

    public void drawEdge(Edge edge) {
        Paint prevStroke = gc.getStroke();
        gc.setStroke(Color.RED);
        new LinePath(edge.getTailNode(), edge.getHeadNode()).draw(gc, this.pixelwidth, false);
        gc.setStroke(prevStroke);
    }

    public void repaint(MapCanvasInterface mapCanvas) {
        MapState mapState = mapCanvas.getMapState();
        gc.setTransform(new Affine());
        setWaterBackground(mapCanvas);

        gc.setTransform(trans);
        double pw = 1 / Math.sqrt(Math.abs(trans.determinant()));
        this.setPixelwidth(pw);
        gc.setFillRule(FillRule.EVEN_ODD);

        updateSearchRange(mapCanvas);

        if (mapState.getModel() != null) {
            drawTypes(mapState);
            drawRouteModel(mapState);
            drawModelBoundIfRequired(mapState);
            drawPinAndRoute(mapCanvas);
            drawCitiesIfRequired(mapState);
        }

        scaleBar.updateScaleBar(mapCanvas);
        gc.setStroke(useRegularColors ? Color.BLACK : Color.WHITE);
        scaleBar.draw(gc, this.pixelwidth, false);
        gc.setStroke(Color.BLACK);

        drawDragSquare();
        drawRoadNodes(mapCanvas);

        if (!mapState.getRenderFullScreen()) {
            drawRange(renderRange, this.pixelwidth);
        }
    }

    private void setWaterBackground(MapCanvasInterface mapCanvas) {
        if (useRegularColors) {
            gc.setFill(Type.WATER.getColor());
        } else {
            gc.setFill(Type.WATER.getAlternateColor());
        }
        gc.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
    }

    private void drawTypes(MapState mapState) {
        for (Type type : mapState.getTypesToBeDrawn()) {
            if (type != Type.UNKNOWN) {
                if (useDependentDraw) {
                    if (type.getMinMxx() < trans.getMxx() && trans.getMxx() < type.getMaxMxx()) {
                        paintDrawablesOfType(type, useRegularColors, mapState.getModel());
                    }
                } else {
                    paintDrawablesOfType(type, useRegularColors, mapState.getModel());
                }
            }
        }
    }

    private void drawRouteModel(MapState mapState) {
        var routeModel = mapState.getRouteModel();
        if (routeModel != null && routeModel.hasRoute()) {
            gc.setStroke(Color.valueOf("#69c7ff"));
            gc.setLineWidth(this.pixelwidth * 3);
            var route = routeModel.getDrawableRoute();
            if (route != null) {
                LinePath drawableRoute = new LinePath(route);
                drawableRoute.draw(gc, this.pixelwidth, smartTrace);
            }
            for (Instruction instruction : routeModel.getInstructions()) {
                instruction.getIndicator().draw(gc, this.pixelwidth);
            }
        }
    }

    private void drawModelBoundIfRequired(MapState mapState) {
        if (drawBound) {
            drawModelBound(mapState.getModel().getBound(), Color.BLACK, this.pixelwidth);
        }
    }

    private void drawPinAndRoute(MapCanvasInterface mapCanvas) {
        if (mapCanvas.getMapRouteManager().getCurrentRouteOrigin() != null) {
            mapCanvas.getMapRouteManager().getCurrentRouteOrigin().draw(gc, this.pixelwidth);
        }
        if (mapCanvas.getMapRouteManager().getCurrentRouteDestination() != null) {
            mapCanvas.getMapRouteManager().getCurrentRouteDestination().draw(gc, this.pixelwidth);
        }
        if (mapCanvas.getMapPinManager().getCurrentPin() != null) {
            mapCanvas.getMapPinManager().getCurrentPin().draw(gc, this.pixelwidth);
        }
    }

    private void drawCitiesIfRequired(MapState mapState) {
        if (showCities) {
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(this.pixelwidth * 2);

            setCityDrawingColors();

            gc.setTextAlign(TextAlignment.CENTER);
            for (CanvasElement element : mapState.getModel().getCitiesKdTree().rangeSearch(renderRange)) {
                element.draw(gc, this.pixelwidth, smartTrace);
            }
        }
    }

    private void setCityDrawingColors() {
        if (useRegularColors) {
            gc.setFill(Color.valueOf("#555555"));
            gc.setStroke(Color.WHITE);
        } else {
            gc.setFill(Color.WHITE);
            gc.setStroke(Color.valueOf("#555555"));
        }
    }

    private void drawDragSquare() {
        if (draggedSquare != null) {
            draggedSquare.draw(gc, this.pixelwidth, false);
        }
    }

    private void drawRoadNodes(MapCanvasInterface mapCanvas) {
        if (mapCanvas.getMapState().getShowRoadNodes()) {
            drawNode(mapCanvas.getMapRouteManager().getStartNode());
            drawNode(mapCanvas.getMapRouteManager().getDestinationNode());
        }
    }

    private void paintDrawablesOfType(Type type, boolean useRegularColors, Model model) {
        KDTree kdTree = model.getKDTreeByType(type);
        gc.setStroke(Color.TRANSPARENT);
        gc.setFill(Color.TRANSPARENT);
        if (kdTree != null) {
            setFillAndStroke(type, useRegularColors);

            for (CanvasElement element : kdTree.rangeSearch(renderRange)) {
                element.draw(gc, 1 / this.pixelwidth, smartTrace);
                if (type.shouldHaveFill()) gc.fill();

                if (drawBoundingBox) {
                    drawRange(element.getBoundingBox(), this.pixelwidth / 2);
                }
            }
        }
    }

    private void drawModelBound(Bound bound, Color color, double scale) {
        float minLon = bound.getMinLon();
        float minLat = bound.getMinLat();
        float maxLon = bound.getMaxLon();
        float maxLat = bound.getMaxLat();
        gc.setStroke(color);
        gc.beginPath();
        gc.setLineWidth(scale);
        gc.moveTo(minLon, minLat);
        gc.lineTo(minLon, maxLat);
        gc.lineTo(maxLon, maxLat);
        gc.lineTo(maxLon, minLat);
        gc.lineTo(minLon, minLat);
        gc.stroke();
    }

    private void drawRange(Range range, double lineWidth) {
        float minX = range.getMinX();
        float minY = range.getMinY();
        float maxX = range.getMaxX();
        float maxY = range.getMaxY();
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(lineWidth);
        gc.strokeRect(minX, minY, maxX - minX, maxY - minY);
        gc.stroke();
    }

    private void setFillAndStroke(Type type, boolean useRegularColors) {
        gc.setLineWidth(type.getWidth() * this.pixelwidth);
        if (useRegularColors) {
            if (type.shouldHaveFill()) gc.setFill(type.getColor());
            if (type.shouldHaveStroke()) gc.setStroke(type.getColor());
        } else {
            if (type.shouldHaveFill()) gc.setFill(type.getAlternateColor());
            if (type.shouldHaveStroke()) gc.setStroke(type.getAlternateColor());
        }
    }

    public void updateSearchRange(MapCanvasInterface mapCanvas) {
        float w = (float) mapCanvas.getWidth();
        float h = (float) mapCanvas.getHeight();
        if (mapCanvas.getMapState().getRenderFullScreen()) {
            renderRange = new Range(
                    (float) ((-trans.getTx()) * this.pixelwidth),
                    (float) ((-trans.getTy()) * this.pixelwidth),
                    (float) ((-trans.getTx() + w) * this.pixelwidth),
                    (float) ((-trans.getTy() + h) * this.pixelwidth)
            );
        } else {
            renderRange = new Range(
                    (float) ((-trans.getTx() + w / 2 - 100) * this.pixelwidth),
                    (float) ((-trans.getTy() + h / 2 - 100) * this.pixelwidth),
                    (float) ((-trans.getTx() + w / 2 + 100) * this.pixelwidth),
                    (float) ((-trans.getTy() + h / 2 + 100) * this.pixelwidth)
            );
        }
    }

    public void showDijkstraTree(Dijkstra dijkstra) {
        if (dijkstra != null) {
            for (Map.Entry<Long, Edge> entry : dijkstra.getAllEdgeTo().entrySet()) {
                new LinePath(entry.getValue().getTailNode(), entry.getValue().getHeadNode()).draw(gc, 1, false);
            }
        }
    }

    public void drawStreetName(String text, MapCanvasInterface mapCanvas) {
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setLineWidth(1 * this.pixelwidth);
        Point2D placement = this.getModelCoordinates(50, mapCanvas.getHeight() - 50 + 13);
        gc.strokeText(text, placement.getX(), placement.getY());
        gc.fillText(text, placement.getX(), placement.getY());
    }

    public void resetView(Model model, MapCanvasInterface mapCanvas) {
        trans.setToIdentity();
        Bound b = model.getBound();
        pan(-(b.getMaxLon() + b.getMinLon()) / 2, -(b.getMaxLat() + b.getMinLat()) / 2, mapCanvas);
        pan(mapCanvas.getWidth() / 2, mapCanvas.getHeight() / 2, mapCanvas);

        float boundHeight = b.getMaxLat() - b.getMinLat();
        float boundWidth = b.getMaxLon() - b.getMinLon();
        float bound;
        float canvasScale;
        if (boundHeight > boundWidth) {
            bound = boundHeight;
            canvasScale = (float) mapCanvas.getHeight();
        } else {
            bound = boundWidth;
            canvasScale = (float) mapCanvas.getWidth();
        }
        float factor = canvasScale / bound;
        zoom(factor, mapCanvas.getWidth() / 2, mapCanvas.getHeight() / 2, mapCanvas);
    }

    public void pan(double dx, double dy, MapCanvasInterface mapCanvas) {
        trans.prependTranslation(dx, dy);
        repaint(mapCanvas);
    }

    public void zoom(double factor, double x, double y, MapCanvasInterface mapCanvas) {
        if (shouldZoom(factor)) {
            trans.prependScale(factor, factor, x, y);
            repaint(mapCanvas);
        }
    }

    public boolean shouldZoom(double factor) {
        if (factor > 1) {
            return trans.getMxx() < 2.2;
        } else {
            return trans.getMxx() > 0.0005;
        }
    }

    public Range getRenderRange() {
        return renderRange;
    }

    public ScaleBar getScaleBar() {
        return scaleBar;
    }

    private void setPixelwidth(double pixelwidth) {
        this.pixelwidth = pixelwidth;
    }

    public void setDrawBound(boolean drawBound) {
        this.drawBound = drawBound;
    }

    public void setDraggedSquare(LinePath linePath, MapCanvasInterface mapCanvas) {
        draggedSquare = linePath;
        repaint(mapCanvas);
    }

    public boolean getDrawBound() {
        return drawBound;
    }

    public void setUseDependentDraw(boolean shouldUseDependentDraw, MapCanvasInterface mapCanvas) {
        useDependentDraw = shouldUseDependentDraw;
        repaint(mapCanvas);
    }

    public void setShowCities(boolean showCities, MapCanvasInterface mapCanvas) {
        this.showCities = showCities;
        repaint(mapCanvas);
    }

    public void setTraceType(boolean shouldSmartTrace, MapCanvasInterface mapCanvas) {
        smartTrace = shouldSmartTrace;
        repaint(mapCanvas);
    }

    public Affine getTrans() {
        return trans;
    }

    public void setUseRegularColors(boolean shouldUseRegularColors, MapCanvasInterface mapCanvas) {
        useRegularColors = shouldUseRegularColors;
        repaint(mapCanvas);
    }

    public Point2D getModelCoordinates(double x, double y) {
        try {
            return trans.inverseTransform(x, y);
        } catch (NonInvertibleTransformException e) {
            return null;
        }
    }

    public void zoomToNode(Node node, MapCanvasInterface mapCanvas) {
        zoomToPoint(1, node.getLon(), node.getLat(), mapCanvas);
    }

    public void zoomToPoint(double factor, float lon, float lat, MapCanvasInterface mapCanvas) {
        trans.setToIdentity();
        pan(-lon, -lat, mapCanvas);
        zoom(factor, 0, 0, mapCanvas);
        pan(mapCanvas.getWidth() / 2, mapCanvas.getHeight() / 2, mapCanvas);
        repaint(mapCanvas);
    }

    public LinePath getDraggedSquare() {
        return this.draggedSquare;
    }

    public boolean getShowCities() {
        return this.showCities;
    }
}

package bong.canvas;

import javafx.scene.layout.Pane;

public class MapCanvasWrapper extends Pane {

    public final MapCanvas mapCanvas;

    public MapCanvasWrapper(){
        mapCanvas = new MapCanvas();
        getChildren().add(mapCanvas);

        heightProperty().addListener((obs, oldVal, newVal) -> {
            mapCanvas.setHeight((double) newVal);
            mapCanvas.getMapRenderer().repaint(mapCanvas);
        });

        widthProperty().addListener((obs, oldVal, newVal) -> {
            mapCanvas.setWidth((double) newVal);
            mapCanvas.getMapRenderer().repaint(mapCanvas);
        });

    }
}

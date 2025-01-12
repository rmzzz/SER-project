package bong.controllers;

import bong.canvas.MapCanvas;
import bong.canvas.MapRenderer;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Affine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainControllerUnitTest {

    MainController mainController;
    MapCanvas mapCanvas;
    MapRenderer mapRenderer;

    @BeforeEach
    void setUp() {
        mainController = spy(new MainController(null, null));
        mapCanvas = mock(MapCanvas.class);
        mapRenderer = mock(MapRenderer.class);

        mainController.canvas = mapCanvas;
        when(mapCanvas.getMapRenderer()).thenReturn(mapRenderer);
    }

    @Test
    void getZoomFactorBigValue() {
        Point2D point = new Point2D(10, 10);
        mainController.lastMouse  = new Point2D(1, 1);
        when(mapCanvas.getWidth()).thenReturn(640.0);
        when(mapCanvas.getHeight()).thenReturn(480.0);
        Affine affine = new Affine();
        when(mapRenderer.getTrans()).thenReturn(affine);

        double res = mainController.getZoomFactor(point);

        assertEquals(2.2, res);
    }

    @Test
    void getZoomFactorOne() {
        Point2D point = new Point2D(100, 100);
        mainController.lastMouse  = new Point2D(0, 0);
        when(mapCanvas.getWidth()).thenReturn(100.0);
        when(mapCanvas.getHeight()).thenReturn(100.0);
        Affine affine = new Affine();
        when(mapRenderer.getTrans()).thenReturn(affine);

        double res = mainController.getZoomFactor(point);

        assertEquals(1.0, res);
    }
}
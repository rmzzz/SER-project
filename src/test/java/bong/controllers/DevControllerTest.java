package bong.controllers;

import bong.canvas.MapCanvas;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Disabled("JFX Platform.startup prevents running on GitHub Actions")
@Timeout(10)
class DevControllerTest {
    Stage primaryStageMock;
    MapCanvas canvas;
    RouteController routeController;

    @BeforeEach
    void setUp() {
        primaryStageMock = mock(Stage.class);
        canvas = new MapCanvas();
        routeController = new RouteController(null, canvas);
    }

    @Test
    void devControllerSetUp() {
        DevController devController = new DevController(primaryStageMock, canvas, routeController);
        assertNotNull(devController);
    }


}

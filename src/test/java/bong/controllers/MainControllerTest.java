package bong.controllers;

import bong.AppResourceLoader;
import bong.canvas.MapCanvasWrapper;
import bong.util.ResourceLoader;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.MockMakers;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Disabled("JFX Platform.startup prevents running on GitHub Actions")
@Timeout(10)
class MainControllerTest {
    MainController mainController;
    Stage primaryStageMock;
    ResourceLoader resourceLoader;
    Map<String, Object> fxmlMocks;

    TextField searchField;

    @BeforeAll @Timeout(10)
    static void init() {
        Platform.startup(() -> {});
    }

    @AfterAll
    static void destroy() {
        Platform.exit();
    }

    @BeforeEach
    void setUp() {
        primaryStageMock = mock(Stage.class);
        resourceLoader = new AppResourceLoader();
        mainController = new MainController(primaryStageMock, resourceLoader);
        mainController.mapCanvasWrapper = new MapCanvasWrapper();
        fxmlMocks = mockFXMLFields(mainController, VBox.class, TextField.class);
        searchField = (TextField) fxmlMocks.get("searchField");
        ReadOnlyBooleanProperty focusedProperty = mock(ReadOnlyBooleanProperty.class);
        when(searchField.focusedProperty()).thenReturn(focusedProperty);
        StringProperty textProperty = mock(StringProperty.class);
        when(searchField.textProperty()).thenReturn(textProperty);
    }

    static Map<String, Object> mockFXMLFields(Object controller, Class<?>... mockTypes) {

        Class<?> cls = controller.getClass();
        return Stream.of(cls.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(FXML.class))
                .filter(f -> getField(controller, f) == null)
                //.filter(f -> Stream.of(f.getType()).flatMap(t -> Arrays.stream(mockTypes).filter(m -> m.isAssignableFrom(t))).anyMatch(obj -> true))
                .map(f -> {
                    try {
                        f.setAccessible(true);
                        Object m = mock(f.getType(), withSettings().mockMaker(MockMakers.INLINE));
                        f.set(controller, m);
                        return f;
                    } catch (IllegalAccessException e) {
                        fail(e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Field::getName, f -> getField(controller, f)));
    }

    static Object getField(Object container, Field field) {
        try {
            field.setAccessible(true);
            return field.get(container);
        } catch (IllegalAccessException e) {
            fail(e);
            return null;
        }
    }

    @Test
    void initialize() {
        mainController.initialize();

        assertNotNull(mainController.routeController);
        assertNotNull(mainController.searchController);
        assertNotNull(mainController.poiController);
        assertNotNull(mainController.getCanvas());
    }
}
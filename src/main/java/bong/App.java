/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package bong;

import java.net.URL;

import bong.controllers.MainController;
import bong.util.ResourceLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
    static Stage primaryStage;

    final ResourceLoader resourceLoader = new AppResourceLoader();

    @Override
    public void start(Stage primaryStage) throws Exception {
        App.primaryStage = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader(resourceLoader.getViewResource("main.fxml"));
        MainController mainController = new MainController(primaryStage, resourceLoader);
        fxmlLoader.setController(mainController);
        primaryStage.setTitle("Bong Maps");

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        URL style = resourceLoader.getViewResource("style.css");
        scene.getStylesheets().add(style.toExternalForm());

        Image icon = new Image(resourceLoader.getViewResourceAsStream("bongIcon.png"));
        primaryStage.getIcons().add(icon);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        App.launch(args);
    }


}

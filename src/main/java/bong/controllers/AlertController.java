package bong.controllers;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertController {
    public static void showError(String headerText, String contentText, Throwable error) {
        System.err.printf("ERROR: %s - %s: %s", headerText, contentText, error.getMessage());
        error.printStackTrace();
        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            if (headerText != null) {
                alert.setHeaderText(headerText);
            }
            if (contentText != null) {
                alert.setContentText(contentText);
            }
            alert.showAndWait();
        }
    }

    public static void showInfo(String text) {
        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(text);
            alert.showAndWait();
        } else {
            System.out.println("INFO: " + text);
        }
    }

    public static Optional<ButtonType> showConfirmation(String headerText, String contentText) {
        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);
            return alert.showAndWait();
        }
        System.out.println("WARNING: Could not show confirmation: " + headerText + " - " + contentText);
        return Optional.empty();
    }
}

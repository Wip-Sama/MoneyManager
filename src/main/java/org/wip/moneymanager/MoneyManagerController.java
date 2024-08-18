package org.wip.moneymanager;

import javafx.animation.FadeTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.wip.moneymanager.components.Switch;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.Theme;

import java.io.IOException;

public class MoneyManagerController {
    @FXML
    private ToggleButton accounts;

    @FXML
    private ToggleButton settings;

    @FXML
    private ToggleButton statistics;

    @FXML
    private ToggleButton transactions;

    @FXML
    private BorderPane change_pane;

    @FXML
    private ProgressBar busy_indicator;

    private Integer theme = 1;

    public void initialize() {
        busy_indicator.setVisible(false);
        Data.busyProperty().addListener((_, _, newValue) -> {
            if (newValue.intValue() > 0) {
                show_busy_indicator();
            } else {
                hide_busy_indicator();
            }
        });
        accounts.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                FXMLLoader fxmlLoader = new FXMLLoader(MoneyManager.class.getResource("pages/Accounts.fxml"));

                try {
                    Parent root = fxmlLoader.load();
                    change_pane.setCenter(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        settings.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                FXMLLoader fxmlLoader = new FXMLLoader(MoneyManager.class.getResource("pages/Settings.fxml"));
                try {
                    Parent root = fxmlLoader.load();
                    change_pane.setCenter(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Data.theme.addListener((_, _, newValue) -> {
            Scene scene = accounts.getScene();
            switch (newValue) {
                case DARK, SYSTEM:
                    scene.getStylesheets().remove("style-light.css");
                    scene.getStylesheets().add("style-dark.css");
                    break;
                case LIGHT:
                    scene.getStylesheets().remove("style-dark.css");
                    scene.getStylesheets().add("style-light.css");
                    break;
            }
        });
    }

    public void show_busy_indicator() {
        busy_indicator.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), busy_indicator);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void hide_busy_indicator() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), busy_indicator);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> busy_indicator.setVisible(false));
        fadeOut.play();
    }

}
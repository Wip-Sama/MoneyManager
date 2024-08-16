package org.wip.moneymanager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

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

    private Integer theme = 1;

    public void initialize() {
        System.out.println("MoneyManagerController initialized");
        accounts.onActionProperty().addListener((_, _, newValue) -> {
            System.out.println(newValue);
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
        statistics.selectedProperty().addListener((_, _, newValue) -> {
            try {
                onHelloButtonClick();
                System.out.println("Hello "+theme);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    protected void onHelloButtonClick() throws IOException {
        Scene scene = accounts.getScene();
        if (theme % 2 == 0) {
            scene.getStylesheets().remove("style-light.css");
            scene.getStylesheets().add("style-dark.css");
            theme = 1;
        } else {
            scene.getStylesheets().remove("style-dark.css");
            scene.getStylesheets().add("style-light.css");
            theme = 0;
        }
    }
}
package org.wip.moneymanager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

import org.wip.moneymanager.components.ColorPickerButton;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private ToggleButton accounts;

    @FXML
    private BorderPane change_pane;

    private Integer theme = 1;

    public void initialize() {
        System.out.println("HelloController initialized");
        accounts.onActionProperty().addListener((_, _, newValue) -> {
            System.out.println(newValue);
        });
        accounts.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                FXMLLoader fxmlLoader = new FXMLLoader(MoneyManager.class.getResource("pages/accounts.fxml"));

                try {
                    Parent root = fxmlLoader.load();
                    change_pane.setCenter(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    protected void onHelloButtonClick() throws IOException {
        /*
        FXMLLoader fxmlLoader = new FXMLLoader(MoneyManager.class.getResource("components/colorpickerpopup.fxml"));
        Parent colorPickerParent = fxmlLoader.load();
        Popup popup = new Popup();
        popup.getContent().add(colorPickerParent);
        popup.show(welcomeText.getScene().getWindow());*/

        welcomeText.setText("Welcome to JavaFX Application! "+theme);
        Scene scene = welcomeText.getScene();
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
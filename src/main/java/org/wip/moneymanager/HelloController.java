package org.wip.moneymanager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

import org.wip.moneymanager.components.ColorPickerButton;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    private Integer theme = 1;

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
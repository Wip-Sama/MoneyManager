package org.wip.moneymanager;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    private Integer theme = 1;

    @FXML
    protected void onHelloButtonClick() {
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
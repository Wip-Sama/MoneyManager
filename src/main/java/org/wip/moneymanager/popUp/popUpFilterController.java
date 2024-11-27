package org.wip.moneymanager.popUp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.wip.moneymanager.model.Data;

import java.io.IOException;

public class popUpFilterController extends AnchorPane {
    @FXML
    private Button buttonFilter;

    @FXML
    private Button cancelPopUp;

    private double xOffset = 0;
    private double yOffset = 0;
    private final Popup popup = new Popup();
    private final Window ownerWindow;

    public popUpFilterController(Window window) throws IOException {
        this.ownerWindow = window;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/popUp/popUpFilter.fxml"));
        fxmlLoader.setController(this); // Imposta il controller
        Parent loaded = fxmlLoader.load(); // Carica il file FXML

        Scene popupScene = new Scene(loaded);
        popupScene.getRoot().setStyle("-fu-accent: " + Data.dbUser.accentProperty().get().getHex() + ";");

        popup.getContent().add(loaded); // Aggiunge il nodo radice al popup
        window.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, _ -> hide());
    }


    @FXML
    private void initialize() {
        // Pulsante per chiudere il popup
        cancelPopUp.setOnAction(e -> hide());

        // Drag del popup
        this.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        this.setOnMouseDragged(event -> {
            popup.setX(event.getScreenX() - xOffset);
            popup.setY(event.getScreenY() - yOffset);
        });
    }

    private void hide() {
        popup.hide();
    }

    public void show() {
        popup.show(ownerWindow);
    }
}

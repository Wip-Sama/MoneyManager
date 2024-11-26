package org.wip.moneymanager.popUp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.pages.Accounts;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AddNewTagController extends BorderPane {

    @FXML
    private Button AddTagButton;

    @FXML
    private Button CancelButton;

    @FXML
    private ChoiceBox<String> ChoiceColor;

    @FXML
    private Label ColorLabel;

    @FXML
    private Label ErrorLabel;

    @FXML
    private Label TagNameLabel;

    @FXML
    private TextField nameField;

    @FXML
    private BorderPane popUpAddTags;

    @FXML
    private ToggleButton tagEdit;

    @FXML
    private Label tagPreviewLabel;

    private double xOffset = 0;
    private double yOffset = 0;
    private Accounts accountsPage;
    private final Popup popup = new Popup();
    private final Window ownerWindow;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    public AddNewTagController(Window window) throws IOException {
        Data.esm.register(executorService);
        this.accountsPage = accountsPage;
        this.ownerWindow = window;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/popUp/addNewTag.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        Parent loaded = fxmlLoader.load();

        Scene popupScene = new Scene(loaded);
        popupScene.getRoot().setStyle("-fu-accent: " + Data.dbUser.accentProperty().get().getHex() + ";");

        popup.getContent().add(loaded);
        window.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, _ -> hide());
    }

    @FXML
    public void initialize() {
        popUpAddTags.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        popUpAddTags.setOnMouseDragged(event -> {
            popup.setX(event.getScreenX() - xOffset);
            popup.setY(event.getScreenY() - yOffset);
        });

        CancelButton.setOnAction(event -> {
            clearFields();
            hide();
        });

        AddTagButton.setOnAction(event -> {
            if (validateFields()) {
                addTag();
            }
        });




    }

    private void addTag() {

    }

    private boolean validateFields() {
        return true;
    }

    private void clearFields() {
    }

    public void show() {
        popup.show(ownerWindow);
    }

    public void hide() {
        popup.hide();
    }

}

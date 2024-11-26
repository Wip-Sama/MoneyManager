package org.wip.moneymanager.popUp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.wip.moneymanager.model.Data;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ChoiceBox;
import org.wip.moneymanager.components.BalanceEditor;

import org.wip.moneymanager.pages.Accounts;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class transactionPopupController extends BorderPane {

    @FXML
    private ToggleButton incomeButton;
    @FXML
    private ToggleButton expenseButton;
    @FXML
    private ToggleButton transferButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private BorderPane BoderPanePopup;
    @FXML
    private DatePicker datePicker;
    @FXML
    private BalanceEditor balanceEditor;
    @FXML
    private ChoiceBox<String> categoryChoiceBox;
    @FXML
    private ChoiceBox<String> accountChoiceBox;


    private double xOffset = 0;
    private double yOffset = 0;
    private final Popup popup = new Popup();
    private final Window ownerWindow;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public transactionPopupController(Window window) throws IOException {
        Data.esm.register(executorService);
        this.ownerWindow = window;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/popUp/transactionPopUp.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        Parent loaded = fxmlLoader.load();

        Scene popupScene = new Scene(loaded);
        popupScene.getRoot().setStyle("-fu-accent: " + Data.dbUser.accentProperty().get().getHex() + ";");

        popup.getContent().add(loaded);
        window.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, _ -> hide());
    }

    @FXML
    private void initialize() {
        cancelButton.setOnAction(e -> hide());

        BoderPanePopup.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        BoderPanePopup.setOnMouseDragged(event -> {
            popup.setX(event.getScreenX() - xOffset);
            popup.setY(event.getScreenY() - yOffset);
        });


        ToggleGroup toggleGroup = new ToggleGroup();
        incomeButton.setToggleGroup(toggleGroup);
        expenseButton.setToggleGroup(toggleGroup);
        transferButton.setToggleGroup(toggleGroup);

        incomeButton.setOnAction(e -> {
            System.out.println("Income button clicked");
        });

        expenseButton.setOnAction(e -> {
            System.out.println("Expense button clicked");
        });

        transferButton.setOnAction(e -> {
            System.out.println("Transfer button clicked");
        });
    }

    private void hide() {
        popup.hide();
    }


    public void show() {
        popup.show(ownerWindow);
    }
}

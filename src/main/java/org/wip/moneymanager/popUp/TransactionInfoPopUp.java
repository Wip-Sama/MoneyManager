package org.wip.moneymanager.popUp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import org.wip.moneymanager.components.BalanceEditor;
import org.wip.moneymanager.components.CategorySelector;
import org.wip.moneymanager.components.SingleTransactionController;
import org.wip.moneymanager.components.TagSelector;
import org.wip.moneymanager.model.DBObjects.dbTransaction;
import org.wip.moneymanager.model.Data;


import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionInfoPopUp extends BorderPane {
    @FXML
    private BorderPane BoderPanePopup;

    @FXML
    private Label account;

    @FXML
    private Button buttonExit;

    @FXML
    private ComboBox<String> accountsComboBox;

    @FXML
    private Label amountLabel;

    @FXML
    private BalanceEditor balanceCounter;

    @FXML
    private Label category;

    @FXML
    private CategorySelector categorySelectorTwo;

    @FXML
    private Label date;

    @FXML
    private DatePicker datesPicker;

    @FXML
    private Button deleteButton;

    @FXML
    private Button discardButton;

    @FXML
    private Button editButton;

    @FXML
    private Button expenseButton;

    @FXML
    private Button incomeButton;

    @FXML
    private Label labelTransaction;

    @FXML
    private TextArea notesAgg;

    @FXML
    private Button saveEditButton;

    @FXML
    private ComboBox<String> secondAccountComboBox;

    @FXML
    private TagSelector tagPane;

    @FXML
    private Label tagsLabel;

    @FXML
    private Button transferButton;

    private final CustomMenuItem customMenuItem;
    private final ContextMenu contextMenu = new ContextMenu();
    private final Window node;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Runnable onCloseCallback;
    private final SingleTransactionController controller;
    private dbTransaction myTransaction;

    public TransactionInfoPopUp(Window window, SingleTransactionController fatherTransactions) throws IOException {
        this.node = window;
        this.controller = fatherTransactions;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/popUp/infoTransaction.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        Parent loaded = fxmlLoader.load();

        customMenuItem = new CustomMenuItem(loaded);
        customMenuItem.getStyleClass().add("tag-filter-menu-item");
        customMenuItem.hideOnClickProperty().set(false);
        contextMenu.getItems().add(customMenuItem);

        window.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, _ -> hide());
    }

    @FXML
    private void initialize() {
        Data.esm.register(executorService);
        buttonExit.setOnAction(event -> close());
        myTransaction = controller.getTransaction();

        if (myTransaction.type() == 0) {
            expenseButton.setDisable(true);
            transferButton.setDisable(true);
            incomeButton.setStyle("-fx-border-color: green;" + "-fx-border-radius: 6");
            secondAccountComboBox.setVisible(false);
            secondAccountComboBox.setManaged(false);
            category.setText("Category");
        } else if (myTransaction.type() == 1) {
            incomeButton.setDisable(true);
            transferButton.setDisable(true);
            expenseButton.setStyle("-fx-border-color: red;" + "-fx-border-radius: 6");
            secondAccountComboBox.setVisible(false);
            secondAccountComboBox.setManaged(false);
            category.setText("Category");
        }
        else{
            expenseButton.setDisable(true);
            incomeButton.setDisable(true);
            transferButton.setStyle("-fx-border-color: blue;" + "-fx-border-radius: 6");
            categorySelectorTwo.setVisible(false);
            categorySelectorTwo.setManaged(false);
            category.setText("Second Account");

        }
    }


    private void hide() {
        controller.removeBlurChild();
        contextMenu.hide();
    }

    public void toggle(double x, double y) {
        if (contextMenu.isShowing()) {
            hide();
        } else {
            show(x, y);
        }
    }

    public void show(double x, double y) {
        contextMenu.show(node, x, y);
    }

    public void close() {
        if (onCloseCallback != null) {
            onCloseCallback.run();
        }
        hide();
    }

}

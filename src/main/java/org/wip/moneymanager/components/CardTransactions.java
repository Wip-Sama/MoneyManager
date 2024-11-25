package org.wip.moneymanager.components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class CardTransactions extends VBox {

    @FXML
    private VBox cardTransaction;

    @FXML
    private Label transactionDay;

    @FXML
    private Label transactionIn;

    @FXML
    private Label transactionOut;

    @FXML
    private Label account1;

    @FXML
    private Label accountTwo;

    @FXML
    private Label amount;

    @FXML
    private HBox arrowTransaction;

    @FXML
    private Label categTransactions;

    @FXML
    private ScrollBar scrollTags;

    @FXML
    private SVGPath starTransaction;

}

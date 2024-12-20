package org.wip.moneymanager.components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;

public class CardController{

    @FXML
    private HBox GridtagPane;

    @FXML
    private Label amount;

    @FXML
    private HBox arrowTransaction;

    @FXML
    private Label categTransactions;

    @FXML
    private Label recipient;

    @FXML
    private Label sender;

    @FXML
    private SVGPath starTransaction;

    @FXML
    private ScrollPane tagPane;

}

package org.wip.moneymanager.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.model.Data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CardTransactions extends AnchorPane {

    @FXML
    private VBox cardTransaction; // questa è la v box dove poi si generano le card

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


    private String date;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CardTransactions(String date) {
        this.date = date;
        try {
            FXMLLoader loader = new FXMLLoader(SceneHandler.class.getResource("/org/wip/moneymanager/components/cardTransactions.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void initialize() {
        Data.esm.register(executorService);
        transactionDay.setText(date);
    }

}




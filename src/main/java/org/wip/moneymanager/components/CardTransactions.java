package org.wip.moneymanager.components;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.model.DBObjects.dbTransaction;
import org.wip.moneymanager.model.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CardTransactions extends AnchorPane {

    @FXML
    private VBox cardTransaction; // questa è la v box dove poi si generano le card

    @FXML
    private Label transactionDay;

    @FXML
    private Label account1;

    @FXML
    private Label accountTwo;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Integer dateUnix;
    private String dateFormatted;

    public CardTransactions(Integer date) {
        this.dateUnix = date;
        LocalDate localDate = Instant.ofEpochSecond(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        this.dateFormatted = localDate.format(formatter);
        try {
            FXMLLoader loader = new FXMLLoader(SceneHandler.class.getResource("/org/wip/moneymanager/components/cardTransactions.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




    private void generaTransactions() {
        Task<List<dbTransaction>> generaCard = Data.userDatabase.fillCard(dateUnix);
        generaCard.setOnSucceeded(event -> {
            if (generaCard.getValue() != null) {
                List<dbTransaction> transaction = generaCard.getValue();


                for (dbTransaction timestamp : transaction) {
                    SingleTransactionController cardNode = new SingleTransactionController(timestamp);
                    cardTransaction.getChildren().add(cardNode);
                }
            }
        });

        executorService.submit(generaCard);
    }

    @FXML
    public void initialize() {

        Data.esm.register(executorService);
        generaTransactions();

        transactionDay.setText(dateFormatted);
    }

}




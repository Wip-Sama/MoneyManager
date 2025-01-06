package org.wip.moneymanager.components;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.model.DBObjects.TransactionByDate;
import org.wip.moneymanager.model.DBObjects.dbTransaction;
import org.wip.moneymanager.model.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CardTransactions extends AnchorPane {

    @FXML
    private VBox cardTransaction; // questa è la vbox dove poi si generano le card

    @FXML
    private Label transactionDay;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Integer dateUnix;
    private String dateFormatted;
    private TransactionByDate transactionByDate;

    // Costruttore con la data
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

    // Costruttore con TransactionByDate
    public CardTransactions(TransactionByDate transactionByDate) {
        this.transactionByDate = transactionByDate;
        this.dateUnix = transactionByDate.getDate(); // Recupera la data da TransactionByDate
        LocalDate localDate = Instant.ofEpochSecond(dateUnix)
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

        // Se abbiamo un TransactionByDate, carichiamo le transazioni usando gli ID
        if (transactionByDate != null) {
            generaTransactionsByIds(transactionByDate.getTransactionIds());
        }
    }

    // Metodo per generare le card in base a una lista di ID transazioni
    private void generaTransactionsByIds(List<Integer> transactionIds) {
        Task<List<dbTransaction>> generaCard = Data.userDatabase.getAllTransactions(transactionIds);
        generaCard.setOnSucceeded(event -> {
            if (generaCard.getValue() != null) {
                List<dbTransaction> transactions = generaCard.getValue();
                for (dbTransaction transaction : transactions) {
                    SingleTransactionController cardNode = new SingleTransactionController(transaction);
                    cardTransaction.getChildren().add(cardNode);
                }
            }
        });

        executorService.submit(generaCard);
    }

    // Metodo per generare le card in base alla data Unix (utilizzato quando solo la data è passata)
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

        // Se è presente un TransactionByDate, non chiamiamo `generaTransactions()`, ma usiamo `generaTransactionsByIds()`
        if (transactionByDate != null) {
            // Questo è già stato gestito nel costruttore quando transactionByDate è passato
        } else {
            // Se non c'è un TransactionByDate, carichiamo le transazioni solo per la data specifica
            generaTransactions();
        }

        // Imposta la data formattata per la card
        transactionDay.setText(dateFormatted);
    }
}

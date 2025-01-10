package org.wip.moneymanager.components;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.model.DBObjects.TransactionByDate;
import org.wip.moneymanager.model.DBObjects.dbTransaction;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.pages.Transactions;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CardTransactions extends AnchorPane {

    @FXML
    private Label account;

    @FXML
    private Label amount;

    @FXML
    private VBox cardTransaction;

    @FXML
    private Label categTransactions;

    @FXML
    private Label transactionDay;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Integer dateUnix;
    private final String dateFormatted;
    private final TransactionByDate transactionByDate;
    private List<dbTransaction> transactions = new ArrayList<>();
    private final Transactions transactionsPage;

    public CardTransactions(TransactionByDate transactionByDate, Transactions transactionsPage) {
        this.transactionByDate = transactionByDate;
        this.dateUnix = transactionByDate.getDate();
        this.dateFormatted = formatUnixDate(dateUnix);
        this.transactionsPage = transactionsPage;

        loadFXML();
        initialize();
        generaTransactionsByIds(transactionByDate.getTransactionIds());
    }
    public void removeVbox(dbTransaction myTransaction) {
        // Rimuovi la transazione dalla card
        cardTransaction.getChildren().removeIf(card -> {
            if (card instanceof SingleTransactionController) {
                SingleTransactionController cardTransaction = (SingleTransactionController) card;
                return cardTransaction.getTransaction().equals(myTransaction);
            }
            return false;
        });

        // Controlla se la cardTransaction è vuota
        if (cardTransaction.getChildren().isEmpty()) {
            VBox vboxParent = (VBox) cardTransaction.getParent();
            if (vboxParent != null) {
                vboxParent.getChildren().remove(cardTransaction); // Rimuovi la CardTransactions specifica
            }
        }
    }



    private String formatUnixDate(Integer unixDate) {
        LocalDate localDate = Instant.ofEpochSecond(unixDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return localDate.format(formatter);
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneHandler.class.getResource("/org/wip/moneymanager/components/cardTransactions.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML for CardTransactions", e);
        }
    }

    private void generaTransactionsByIds(List<Integer> transactionIds) {
        Task<List<dbTransaction>> task = Data.userDatabase.getAllTransactions(transactionIds);
        task.setOnSucceeded(event -> {
            List<dbTransaction> transactions = task.getValue();
            if (transactions != null) {
                transactions.forEach(transaction -> {
                    SingleTransactionController cardNode = new SingleTransactionController(transaction, transactionsPage, this);
                    cardTransaction.getChildren().add(cardNode);
                    this.transactions.add(transaction);
                });
            }
        });
        executorService.submit(task);
    }

    private void generaTransactions() {
        Task<List<dbTransaction>> task = Data.userDatabase.fillCard(dateUnix);
        task.setOnSucceeded(event -> {
            List<dbTransaction> transactions = task.getValue();
            if (transactions != null) {
                transactions.forEach(transaction -> {
                    SingleTransactionController cardNode = new SingleTransactionController(transaction, transactionsPage, this);
                    cardTransaction.getChildren().add(cardNode);
                    this.transactions.add(transaction);
                });
            }
        });
        executorService.submit(task);
    }

    @FXML
    private void initialize() {
        Data.esm.register(executorService);
        amount.textProperty().bind(Data.lsp.lsb("cardTransactions.amount"));
        account.textProperty().bind(Data.lsp.lsb("cardTransactions.account"));
        categTransactions.textProperty().bind(Data.lsp.lsb("cardTransactions.categTransactions"));


        transactionDay.setText(dateFormatted);

        if (transactionByDate == null) {
            generaTransactions();
        }
    }

    public boolean areFavorite() {
        for (dbTransaction t : transactions) {
            if (t.favorite() == 1) {
                removeTNotFavorite();
                return true;
            }
        }
        return false;
    }

    private void removeTNotFavorite() {
        cardTransaction.getChildren().removeIf(card -> {
            if (card instanceof SingleTransactionController) {
                SingleTransactionController cardTransaction = (SingleTransactionController) card;
                return !cardTransaction.isFavorite();
            }
            return false;});
    }


    public void restoreAllTransactions() {
        cardTransaction.getChildren().clear();
        transactions.forEach(transaction -> {
            SingleTransactionController cardNode = new SingleTransactionController(transaction, transactionsPage, this);
            cardTransaction.getChildren().add(cardNode);
        });
    }
}

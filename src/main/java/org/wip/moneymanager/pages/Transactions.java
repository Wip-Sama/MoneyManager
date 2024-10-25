package org.wip.moneymanager.pages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.wip.moneymanager.MoneyManager;
import org.wip.moneymanager.model.Data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Transactions extends BorderPane implements AutoCloseable {
    @FXML
    private Label page_title;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Transactions() {
        Data.esm.register(executorService);
        try {
            FXMLLoader loader = new FXMLLoader(MoneyManager.class.getResource("pages/transactions.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        page_title.textProperty().bind(Data.lsp.lsb("transactions"));
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}

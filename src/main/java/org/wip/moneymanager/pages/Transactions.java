package org.wip.moneymanager.pages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import org.wip.moneymanager.MoneyManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Transactions extends BorderPane implements AutoCloseable {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Transactions() {
        try {
            FXMLLoader loader = new FXMLLoader(MoneyManager.class.getResource("pages/transactions.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void close() {
        System.out.println("Closing Settings");
        executorService.shutdown();
    }
}

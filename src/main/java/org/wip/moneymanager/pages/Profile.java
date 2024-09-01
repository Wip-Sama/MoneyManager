package org.wip.moneymanager.pages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import org.wip.moneymanager.MoneyManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Profile extends BorderPane implements AutoCloseable {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    public Profile(Scene scene) {
        try {
            FXMLLoader loader = new FXMLLoader(MoneyManager.class.getResource("pages/profile.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        scene.setRoot(this);
    }
    @Override
    public void close() {
        System.out.println("Closing Settings");
        executorService.shutdown();
    }
}

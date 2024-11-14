// File: src/main/java/org/wip/moneymanager/pages/Credits.java
package org.wip.moneymanager.pages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.wip.moneymanager.View.MoneyManager;
import org.wip.moneymanager.model.Data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Credits extends BorderPane implements AutoCloseable {
    @FXML
    private ScrollPane scroller;
    @FXML
    private VBox credit_container;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Credits() {
        Data.esm.register(executorService);
        try {
            FXMLLoader loader = new FXMLLoader(MoneyManager.class.getResource("/org/wip/moneymanager/pages/credits.fxml"));
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

    private void update_available_with_without_scrollbars() {
        double width = scroller.getWidth();
        credit_container.setPrefWidth(width);
    }

    @FXML
    public void initialize() {
        scroller.widthProperty().addListener((_, _, _) -> update_available_with_without_scrollbars());
    }
}
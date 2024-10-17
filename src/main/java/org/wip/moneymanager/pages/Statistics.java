package org.wip.moneymanager.pages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.wip.moneymanager.MoneyManager;
import org.wip.moneymanager.model.Data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//
//import eu.hansolo.fx.charts.PlayfairChart;

public class Statistics extends BorderPane implements AutoCloseable {
    @FXML
    private Label page_title;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    public Statistics() {
        Data.esm.register(executorService);
        try {
            FXMLLoader loader = new FXMLLoader(MoneyManager.class.getResource("pages/statistics.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        page_title.textProperty().bind(Data.lsp.lsb("statistics"));

//        setCenter(new );
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}

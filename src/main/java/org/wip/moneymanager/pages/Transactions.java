package org.wip.moneymanager.pages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.model.Data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Transactions extends BorderPane implements AutoCloseable {
    @FXML
    private Button add_favourite;

    @FXML
    private ToggleButton expenseTransactionsButton;

    @FXML
    private Button filter;

    @FXML
    private ToggleButton incomeTransactionsButton;

    @FXML
    private Label label_in;

    @FXML
    private Label label_out;

    @FXML
    private Label label_tot;

    @FXML
    private Button new_transaction;

    @FXML
    private Label page_title;

    @FXML
    private ScrollPane scrollpane_transaction;

    @FXML
    private ToggleButton transfersTransactionsButton;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Transactions() {
        Data.esm.register(executorService);
        try {
            FXMLLoader loader = new FXMLLoader(SceneHandler.class.getResource("/org/wip/moneymanager/pages/transactions.fxml"));
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

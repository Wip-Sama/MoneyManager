package org.wip.moneymanager.pages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.components.DateTransactions;
import org.wip.moneymanager.model.Data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Transactions extends BorderPane implements AutoCloseable {
    @FXML
    private ToggleButton expenseTransactionsButton;

    @FXML
    private ToggleButton favouriteToggle;

    @FXML
    private Button filter;

    @FXML
    private ToggleButton incomeTransactionsButton;

    @FXML
    private Label labelExpense;

    @FXML
    private Label labelIncome;

    @FXML
    private Label labelTransfer;

    @FXML
    private Button newTransaction;

    @FXML
    private Label pageTitle;

    @FXML
    private ScrollPane scrollpaneTransaction;

    @FXML
    private ToggleButton transfersTransactionsButton;

    @FXML
    private DateTransactions datePickerTransactions;


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
        pageTitle.textProperty().bind(Data.lsp.lsb("transactions"));
    }

    @Override
    public void close() {
        executorService.shutdown();
    }


}

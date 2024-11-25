package org.wip.moneymanager.pages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.components.DateTransactions;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.popUp.AddNewAccountController;
import org.wip.moneymanager.popUp.transactionPopupController;

import java.io.IOException;
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

    protected Parent loaded;
    private transactionPopupController AddNewController;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Transactions() {
        Data.esm.register(executorService);
        try {
            FXMLLoader loader = new FXMLLoader(SceneHandler.class.getResource("/org/wip/moneymanager/pages/transactions.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loaded = loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        pageTitle.textProperty().bind(Data.lsp.lsb("transactions"));
        newTransaction.setOnAction(event -> {
            try {
                // Verifica se AddNewAccount è già inizializzato
                if (AddNewController == null) {
                    AddNewController = new transactionPopupController(loaded.getScene().getWindow());
                }
                AddNewController.show(); // Mostra il popup
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void close() {
        executorService.shutdown();
    }


}

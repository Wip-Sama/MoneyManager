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
import org.wip.moneymanager.popUp.popUpFilterController;
import org.wip.moneymanager.popUp.transactionPopupController;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Transactions extends BorderPane implements AutoCloseable {
    @FXML
    private ToggleButton calendar;

    @FXML
    private ToggleButton daily;

    @FXML
    private DateTransactions datePickerTransactions;

    @FXML
    private ToggleButton favouriteToggle;

    @FXML
    private Button filter;

    @FXML
    private Label labelExpense;

    @FXML
    private Label labelIncome;

    @FXML
    private Label labelTransfer;

    @FXML
    private ToggleButton monthly;

    @FXML
    private Button newTransaction;

    @FXML
    private Label pageTitle;

    @FXML
    private ScrollPane scrollpaneTransaction;

    protected Parent loaded;
    private transactionPopupController AddNewController;
    private popUpFilterController AddNewFilterController;


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

        // Listener per forzare la selezione singola
        daily.setOnAction(event -> {
            if (daily.isSelected()) {
                monthly.setSelected(false);
                calendar.setSelected(false);
                datePickerTransactions.setDateFormat(DateTransactions.DateFormatType.DAILY); // Imposta formato giornaliero
            }
        });

        monthly.setOnAction(event -> {
            if (monthly.isSelected()) {
                daily.setSelected(false);
                calendar.setSelected(false);
                datePickerTransactions.setDateFormat(DateTransactions.DateFormatType.MONTHLY); // Imposta formato mensile
            }
        });

        calendar.setOnAction(event -> {
            if (calendar.isSelected()) {
                monthly.setSelected(false);
                daily.setSelected(false);
                datePickerTransactions.setDateFormat(DateTransactions.DateFormatType.CALENDAR); // Imposta formato calendar
            }
        });

        // Configura il bottone "newTransaction"
        newTransaction.setOnAction(event -> {
            try {
                if (AddNewController == null) {
                    AddNewController = new transactionPopupController(loaded.getScene().getWindow());
                }
                AddNewController.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        filter.setOnAction(event -> {
            try {
                if (AddNewFilterController== null) {
                    AddNewFilterController= new popUpFilterController(loaded.getScene().getWindow());
                }
                AddNewFilterController.show();

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

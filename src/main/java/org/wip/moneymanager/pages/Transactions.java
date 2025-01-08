package org.wip.moneymanager.pages;

import javafx.animation.RotateTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.components.CardTransactions;
import org.wip.moneymanager.components.CategorySelector;
import org.wip.moneymanager.components.MultiDatePicker;
import org.wip.moneymanager.model.DBObjects.TransactionByDate;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.popUp.popUpFilterController;
import org.wip.moneymanager.popUp.transactionPopupController;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Transactions extends BorderPane implements AutoCloseable {

    @FXML
    private HBox HboxmultiDatePicker;

    @FXML
    private Button TransactionsRefreshButton;

    @FXML
    private ToggleButton favouriteToggle;

    @FXML
    private Button filter;

    @FXML
    private SVGPath iconRefresh;

    @FXML
    private Button newTransaction;

    @FXML
    private Label pageTitle;

    @FXML
    private ScrollPane scrollpaneTransaction;

    @FXML
    private VBox vboxCard;

    protected Parent loaded;
    private transactionPopupController AddNewController;
    private popUpFilterController AddNewFilterController;
    private String date;
    private List<CardTransactions> transactions = new ArrayList<>();


    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Transactions() {
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
        Data.esm.register(executorService);
        MultiDatePicker multiDatePicker = new MultiDatePicker(); // Crea una nuova istanza
        multiDatePicker.withRangeSelectionMode();  // Abilita la selezione di intervallo
        DatePicker rangePicker = multiDatePicker.getDatePicker();
        // Aggiungi il MultiDatePicker all'HBox
        HboxmultiDatePicker.getChildren().add(rangePicker);  // Aggiungi il componente all'HBox

        // Aggiungi un listener se vuoi reagire ai cambiamenti di data
        rangePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Gestisci la selezione della data (per esempio, stampa la data)
                System.out.println("Data selezionata: " + newValue);
            }
        });



        pageTitle.textProperty().bind(Data.lsp.lsb("transactions"));
        newTransaction.setOnAction(event -> open_popup());
        filter.setOnAction(event -> openPopUpFilter());
        generateDailyCard();


        TransactionsRefreshButton.setOnAction(event -> {
            RotateTransition rotateTransition = new RotateTransition();
            rotateTransition.setNode(iconRefresh);            // Nodo da animare
            rotateTransition.setDuration(Duration.seconds(2)); // Durata di un ciclo
            rotateTransition.setByAngle(360);            // Ruota di 360 gradi
            rotateTransition.setAutoReverse(false);      // Non invertire direzione
            rotateTransition.play();

            generateDailyCard();


        });
    }


    //nuovo metodo per aprire il popup, dopo il cambio a contest menu
    private void open_popup() {
        try {
            if (AddNewController == null) {
                AddNewController = new transactionPopupController(newTransaction.getScene().getWindow());
            }

            Bounds bounds = newTransaction.localToScreen(newTransaction.getBoundsInLocal());

            double popupWidth = 712.0;
            double x = bounds.getMaxX() - popupWidth;
            double y = bounds.getMaxY();

            AddNewController.toggle(x, y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openPopUpFilter() {
        try {
            if (AddNewFilterController == null) {
                AddNewFilterController = new popUpFilterController(filter.getScene().getWindow(), this);
            }

            Bounds bounds = filter.localToScreen(filter.getBoundsInLocal());

            double popupWidth = 410;
            double x = bounds.getMaxX() - popupWidth;
            double y = bounds.getMaxY();

            AddNewFilterController.toggle(x, y);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void generateDailyCard() {
        if (transactions != null) {
            transactions.clear();
        }

        Task<List<Integer>> generaCard = Data.userDatabase.getAllDaysOfTransaction();
        generaCard.setOnSucceeded(event -> {
            if (generaCard.getValue() != null) {
                List<Integer> daySelected = generaCard.getValue();

                // Remove duplicates using a Set
                Set<Integer> uniqueTimestamps = new HashSet<>(daySelected);

                // Create a list of unique timestamps
                List<Integer> uniqueTimestampsList = new ArrayList<>(uniqueTimestamps);

                vboxCard.getChildren().clear();
                // Iterate through unique timestamps and create CardTransactions
                for (Integer timestamp : uniqueTimestampsList) {
                    CardTransactions cardNode = new CardTransactions(timestamp);
                    transactions.add(cardNode);
                    vboxCard.getChildren().add(cardNode);
                }
            }
        });
    }

    public void applyFilters(String category, String account, List<String> tags) {
        // Ottieni i giorni e le transazioni filtrate dal database
        if (transactions != null) {
            transactions.clear();
        }
        Task<List<TransactionByDate>> generaCard = Data.userDatabase.getAllDaysOfTransaction(category, account, tags);

        generaCard.setOnSucceeded(event -> {
            if (generaCard.getValue() != null) {
                List<TransactionByDate> transactionByDateList = generaCard.getValue();

                // Rimuovi le card esistenti
                vboxCard.getChildren().clear();

                // Crea le card per ogni TransactionByDate
                for (TransactionByDate tbd : transactionByDateList) {
                    CardTransactions cardNode = new CardTransactions(tbd); // Passa l'oggetto TransactionByDate
                    transactions.add(cardNode);
                    vboxCard.getChildren().add(cardNode);
                }
            }
        });
    }





    @Override
    public void close() {executorService.shutdown();}
}

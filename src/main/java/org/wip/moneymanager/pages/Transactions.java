package org.wip.moneymanager.pages;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.components.CardTransactions;
import org.wip.moneymanager.components.CategorySelector;
import org.wip.moneymanager.components.DateTransactions;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.popUp.popUpFilterController;
import org.wip.moneymanager.popUp.transactionPopupController;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

    @FXML
    private CategorySelector categorySelector;

    @FXML
    private VBox vboxCard;



    protected Parent loaded;
    private transactionPopupController AddNewController;
    private popUpFilterController AddNewFilterController;
    private String date;



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
        pageTitle.textProperty().bind(Data.lsp.lsb("transactions"));
        newTransaction.setOnAction(event -> open_popup());
        filter.setOnAction(event -> openPopUpFilter());
        generateDailyCard();

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
                AddNewFilterController = new popUpFilterController(filter.getScene().getWindow());
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
        Task<List<Integer>> generaCard = Data.userDatabase.getAllDaysOfTransaction();

        generaCard.setOnSucceeded(event->{
            if (generaCard.getValue() != null) {

                List<Integer> daySelected = generaCard.getValue();

                Set<String> uniqueDates = new HashSet<>();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                for (Integer timestamp : daySelected) {

                    LocalDateTime dateTime = Instant.ofEpochSecond(timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    String formattedDate = dateTime.format(formatter);


                    uniqueDates.add(formattedDate);
                }

                List<String> uniqueDateList = uniqueDates.stream().collect(Collectors.toList());

                uniqueDateList.forEach(System.out::println);

                for (String date : uniqueDateList) {
                    CardTransactions cardNode = new CardTransactions(date);

                    vboxCard.getChildren().add(cardNode);

                }

                executorService.submit(generaCard);

            }

        });

    }

    @Override
    public void close() {
        executorService.shutdown();
    }




}

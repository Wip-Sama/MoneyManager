package org.wip.moneymanager.pages;

import javafx.animation.RotateTransition;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.components.CardTransactions;
import org.wip.moneymanager.model.DBObjects.TransactionByDate;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.popUp.PopUpFilterController;
import org.wip.moneymanager.popUp.TransactionPopupController;
import org.wip.moneymanager.utility.SVGLoader;
import org.wip.moneymanager.components.MultiDatePicker;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private SVGPath favoriteSvg;

    @FXML
    private VBox vboxCard;

    protected Parent loaded;
    private TransactionPopupController AddNewController;
    private PopUpFilterController AddNewFilterController;
    private final static String on_fav = new SVGLoader("favorite_on_icon").getPath();
    private final static String off_fav = new SVGLoader("favorite_off_icon").getPath();
    private boolean isFavorite = false;
    private MultiDatePicker multiDatePicker = new MultiDatePicker().withRangeSelectionMode();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Map<Integer, CardTransactions> cardCache = new HashMap<>();
    private List<CardTransactions> displayedTransactions = new ArrayList<>();

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
        HboxmultiDatePicker.getChildren().add(multiDatePicker.getDatePicker());
        generaCard(null, null, null);

        pageTitle.textProperty().bind(Data.lsp.lsb("transactions.pageTitle"));
        favouriteToggle.textProperty().bind(Data.lsp.lsb("transactions.favouriteToggle"));
        newTransaction.textProperty().bind(Data.lsp.lsb("transactions.newTransaction"));
        filter.textProperty().bind(Data.lsp.lsb("transactions.filter"));
        TransactionsRefreshButton.textProperty().bind(Data.lsp.lsb("transactions.refreshButton"));

        filter.setOnAction(event -> openPopUpFilter());
        newTransaction.setOnAction(event -> open_popup());
        TransactionsRefreshButton.setOnAction(event -> refresh());

        multiDatePicker.getSelectedDates().addListener((SetChangeListener<LocalDate>) change -> {
            filterTransactionsBySelectedDates(multiDatePicker.getSelectedDates());
        });


        favouriteToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                favoriteSvg.setContent(on_fav);
                isFavorite = true;
                filterFavoriteTransactions();
            } else {
                favoriteSvg.setContent(off_fav);
                isFavorite = false;
                restoreAllTransactions();
                filterTransactionsBySelectedDates( multiDatePicker.getSelectedDates());
            }
        });
    }


    private void open_popup() {
        try {
            if (AddNewController == null) {
                AddNewController = new TransactionPopupController(newTransaction.getScene().getWindow(), this);
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
                AddNewFilterController = new PopUpFilterController(filter.getScene().getWindow(), this);
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

    public void generaCard(String category, String account, List<String> tags) {
        Task<List<TransactionByDate>> task = Data.userDatabase.getAllDaysOfTransaction(category, account, tags);

        task.setOnSucceeded(event -> {
            List<TransactionByDate> transactionByDateList = task.getValue();
            if (transactionByDateList != null) {
                vboxCard.getChildren().clear();
                cardCache.clear();
                displayedTransactions.clear();

                // **Ordina la lista per data in ordine decrescente (dal più recente al più vecchio)**
                transactionByDateList.sort(Comparator.comparingInt(TransactionByDate::getDate).reversed());

                for (TransactionByDate tbd : transactionByDateList) {
                    int date = tbd.getDate();

                    CardTransactions cardNode = cardCache.getOrDefault(date, new CardTransactions(tbd, this));
                    if (!cardCache.containsKey(date)) {
                        cardCache.put(date, cardNode);
                    }

                    displayedTransactions.add(cardNode);
                    vboxCard.getChildren().add(cardNode);
                }

                if (favouriteToggle.isSelected()) {
                    filterFavoriteTransactions();
                }
            }
        });

        executorService.submit(task);
    }


    private void filterFavoriteTransactions() {
        vboxCard.getChildren().removeIf(card -> {
            if (card instanceof CardTransactions) {
                CardTransactions cardTransaction = (CardTransactions) card;
                return !cardTransaction.areFavorite();
            }
            return true;
        });
    }

    private void restoreAllTransactions() {
        vboxCard.getChildren().clear();
        for(CardTransactions card : displayedTransactions) {
            card.restoreAllTransactions();
        }
        displayedTransactions.forEach(vboxCard.getChildren()::add);
    }

    public void applyBlur() {
        GaussianBlur blur = new GaussianBlur();
        blur.setRadius(5);
        vboxCard.setEffect(blur);
    }

    public void removeBlur() {
        vboxCard.setEffect(null);
    }

    public Transactions getInstance(){
        return this;
    }

    public void refresh(){
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setNode(iconRefresh);
        rotateTransition.setDuration(Duration.seconds(0.5));
        rotateTransition.setByAngle(360);
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();
        if (multiDatePicker != null) {
            multiDatePicker.getSelectedDates().clear();
        }
        multiDatePicker.getDatePicker().setValue(null);

        if (!isFavorite){
            generaCard(null, null, null);
        }

    }

    @Override
    public void close() {
        executorService.shutdown();
    }

    public boolean isSetOnFavorite() {
        return isFavorite;
    }

    public void removeCard(CardTransactions cardTransactions) {
        vboxCard.getChildren().remove(cardTransactions);

    }

    private void filterTransactionsBySelectedDates(Set<LocalDate> selectedDates) {
        if (selectedDates.isEmpty()) {
            // Se non ci sono date selezionate, mostra tutte le transazioni
            restoreAllTransactions();
            return;
        }

        // Rimuovi solo le card che non corrispondono alle date selezionate
        vboxCard.getChildren().removeIf(node -> {
            if (node instanceof CardTransactions) {
                CardTransactions card = (CardTransactions) node;
                LocalDate transactionDate = card.getTransactionDate();
                return !selectedDates.contains(transactionDate); // Rimuovi solo quelle fuori dal range
            }
            return false;
        });

        // Aggiungi solo le transazioni che devono essere visibili e non sono già presenti
        for (CardTransactions card : displayedTransactions) {
            LocalDate transactionDate = card.getTransactionDate();
            if (selectedDates.contains(transactionDate) && !vboxCard.getChildren().contains(card)) {
                vboxCard.getChildren().add(card);
            }
        }
    }


}

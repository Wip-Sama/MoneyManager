package org.wip.moneymanager.components;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Window;
import javafx.util.Duration;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.model.DBObjects.dbTag;
import org.wip.moneymanager.model.DBObjects.dbTransaction;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.pages.Transactions;
import org.wip.moneymanager.popUp.TransactionInfoPopUp;
import org.wip.moneymanager.utility.SVGLoader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleTransactionController extends AnchorPane {

    @FXML
    private BorderPane backGroundT;

    @FXML
    private HBox GridtagPane;

    @FXML
    private Label amount;

    @FXML
    private SVGPath arrowTransaction;

    @FXML
    private Label categTransactions;

    @FXML
    private Label recipient;

    @FXML
    private Label sender;

    @FXML
    private SVGPath starTransaction;

    @FXML
    private ScrollPane tagPane;

    @FXML
    private Button deleteCard;

    @FXML
    private Button buttonFavourite;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private dbTransaction myTransaction;
    private static final String on_fav = new SVGLoader("star").getPath();
    private static final String off_fav = new SVGLoader("Star_empty").getPath();

    private final Transactions TransactionsPage;
    private static final String ORIGINAL_BACKGROUND_COLOR = "-fx-background-color: transparent;";
    private static final String HOVER_BACKGROUND_COLOR = "-fx-background-color: -fu-background-3; -fx-background-radius: 6;";
    private TransactionInfoPopUp addNewController;
    private List<dbTag> dbTags = new ArrayList<>();
    private CardTransactions parentCardTransactions;

    public SingleTransactionController(dbTransaction transaction, Transactions transactions, CardTransactions parentCardTransactions) {
        this.myTransaction = transaction;
        this.TransactionsPage = transactions;
        this.parentCardTransactions = parentCardTransactions;

        try {
            FXMLLoader loader = new FXMLLoader(SceneHandler.class.getResource("/org/wip/moneymanager/components/singleTransaction.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        Data.esm.register(executorService);
        generateTags();
        setupTooltips();
        setupDeleteCardEvents();
        setupBackgroundEvents();
        setupButtonFavourite();
        setupTransactionDetails();
    }

    private void setupTooltips() {
        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.millis(1));
        tooltip.setHideDelay(Duration.millis(0));
        tooltip.textProperty().bind(Data.lsp.lsb("singleTransaction.tooltip.doubleClick"));
        Tooltip.install(backGroundT, tooltip);

        Tooltip deleteCardTooltip = new Tooltip();
        deleteCardTooltip.setShowDelay(Duration.millis(1));
        deleteCardTooltip.setHideDelay(Duration.millis(0));
        deleteCardTooltip.textProperty().bind(Data.lsp.lsb("singleTransaction.tooltip.delete"));
        deleteCard.setOnMouseEntered(event -> Tooltip.install(deleteCard, deleteCardTooltip));
        deleteCard.setOnMouseExited(event -> Tooltip.uninstall(deleteCard, deleteCardTooltip));
    }

    private void setupDeleteCardEvents() {
        deleteCard.textProperty().bind(Data.lsp.lsb("singleTransaction.delete"));
        deleteCard.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                removeCard(myTransaction.id());
                event.consume();
            }
        });
    }

    private void setupBackgroundEvents() {
        backGroundT.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !event.getTarget().equals(deleteCard)) {
                openPopup();
            }
        });

        backGroundT.setOnMouseEntered(event -> backGroundT.setStyle(HOVER_BACKGROUND_COLOR));
        backGroundT.setOnMouseExited(event -> backGroundT.setStyle(ORIGINAL_BACKGROUND_COLOR));
    }

    private void setupButtonFavourite() {
        starTransaction.setContent(myTransaction.favorite() == 1 ? on_fav : off_fav);

        buttonFavourite.setOnAction(event -> {
            try {
                if (myTransaction.favorite() == 0) {
                    myTransaction.setFavourite(1);
                    starTransaction.setContent(on_fav);
                } else {
                    myTransaction.setFavourite(0);
                    parentCardTransactions.removeVbox(myTransaction);
                    starTransaction.setContent(off_fav);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setupTransactionDetails() {
        if (myTransaction.type() != 2) {
            if(myTransaction.type() == 0){
                amount.setStyle("-fx-text-fill: green;");
            } else {
                amount.setStyle("-fx-text-fill: red;");
            }
            try {
                String currency = Data.userDatabase.getCurrencyFromAccountid(myTransaction.account());
                String formattedAmount = myTransaction.amount() + " " + currency;
                amount.setText(formattedAmount);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            try {
                amount.setStyle("-fx-text-fill: -fu-text-1;");
                String fromCurrency = Data.userDatabase.getCurrencyFromAccountid(myTransaction.account());
                String toCurrency = Data.userDatabase.getCurrencyFromAccountid(myTransaction.second_account());
                double convertedAmount = Data.mmDatabase.convertCurrency(fromCurrency, toCurrency, myTransaction.amount());
                int convertedAmountInt = (int) convertedAmount;
                String formattedAmount = myTransaction.amount() + " " + fromCurrency + " -> " + convertedAmountInt + " " + toCurrency;
                amount.setText(formattedAmount);

            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }



        if (myTransaction.type() == 2) {
            setupTransferDetails();
        } else {
            setupCategoryDetails();
        }

        Task<String> taskNameAccount = Data.userDatabase.getNameAccountFromId(myTransaction.account());
        taskNameAccount.setOnSucceeded(event -> sender.setText(taskNameAccount.getValue()));
        executorService.submit(taskNameAccount);
    }

    private void setupTransferDetails() {
        arrowTransaction.setVisible(true);
        recipient.setVisible(true);

        Task<String> taskSecondAccountName = Data.userDatabase.getNameAccountFromId(myTransaction.second_account());
        taskSecondAccountName.setOnSucceeded(event -> {
            recipient.setText(taskSecondAccountName.getValue());
            categTransactions.textProperty().bind(Data.lsp.lsb("singleTransaction.transfer"));
        });
        executorService.submit(taskSecondAccountName);
    }

    private void setupCategoryDetails() {
        try {
            categTransactions.setText(Data.userDatabase.getCategoryNameById(myTransaction.category()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateTags() {
        tagPane.setDisable(true);

        Task<List<dbTag>> loadTagsTask = Data.userDatabase.getTagFromTransaction(myTransaction.id());
        loadTagsTask.setOnSucceeded(event -> {
            dbTags = loadTagsTask.getValue();
            if (dbTags != null) {
                dbTags.forEach(dbTagItem -> {
                    Tag tag = new Tag(dbTagItem.name(), 0, 0, dbTagItem.color());
                    GridtagPane.getChildren().add(tag);
                });
            }
        });

        loadTagsTask.setOnFailed(event -> loadTagsTask.getException().printStackTrace());
        executorService.submit(loadTagsTask);
    }

    public boolean isFavorite() {
        return myTransaction.favorite() == 1;
    }

    private void openPopup() {
        if (addNewController == null) {
            try {
                addNewController = new TransactionInfoPopUp(backGroundT.getScene().getWindow(), this, dbTags);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            addNewController.getContextMenu().setOnHidden(event -> TransactionsPage.removeBlur());
        }

        Window mainWindow = TransactionsPage.getScene().getWindow();
        double popupWidth = 712.0;
        double popupHeight = 400.0;
        double x = Math.max(mainWindow.getX() + (mainWindow.getWidth() - popupWidth) / 2, 0);
        double y = Math.max(mainWindow.getY() + (mainWindow.getHeight() - popupHeight) / 2, 0);

        TransactionsPage.applyBlur();
        addNewController.toggle(x, y);
    }

    public dbTransaction getTransaction() {
        return myTransaction;
    }

    public void refreshSingleTransaction() {
        TransactionsPage.refresh();
    }

    public void removeCard(int id) {
        Task<Boolean> removeTask = Data.userDatabase.removeTransaction(id);
        removeTask.setOnSucceeded(event -> {
            parentCardTransactions.removeVbox(myTransaction);
            TransactionsPage.refresh();
        });
        executorService.submit(removeTask);
    }
}

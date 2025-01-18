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
        Tooltip tooltip = new Tooltip("Doppio clic per dettagli");
        tooltip.setShowDelay(Duration.millis(1));
        tooltip.setHideDelay(Duration.millis(0));
        Tooltip.install(backGroundT, tooltip);

        Tooltip deleteCardTooltip = new Tooltip("Doppio clic per eliminare");
        deleteCardTooltip.setShowDelay(Duration.millis(1));
        deleteCardTooltip.setHideDelay(Duration.millis(0));
        deleteCard.setOnMouseEntered(event -> Tooltip.install(deleteCard, deleteCardTooltip));
        deleteCard.setOnMouseExited(event -> Tooltip.uninstall(deleteCard, deleteCardTooltip));
    }

    private void setupDeleteCardEvents() {
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

        backGroundT.setOnMouseMoved(event -> {
            if (!deleteCard.contains(event.getX() - deleteCard.getLayoutX(), event.getY() - deleteCard.getLayoutY())) {
                Tooltip.install(backGroundT, new Tooltip("Doppio clic per dettagli"));
            } else {
                Tooltip.uninstall(backGroundT, null);
            }
        });

        backGroundT.setOnMouseEntered(event -> backGroundT.setStyle(HOVER_BACKGROUND_COLOR));
        backGroundT.setOnMouseExited(event -> backGroundT.setStyle(ORIGINAL_BACKGROUND_COLOR));
    }

    private void setupButtonFavourite() {
        if(myTransaction.favorite() == 1) {
            starTransaction.setContent(on_fav);
        } else {
            starTransaction.setContent(off_fav);
        }



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
        amount.setText(String.valueOf(myTransaction.amount()));

        if (myTransaction.type() == 1) {
            amount.setStyle("-fx-text-fill: red;");
        } else if (myTransaction.type() == 0) {
            amount.setStyle("-fx-text-fill: green;");
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
            categTransactions.setText("Trasferimento");
        });
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
                    Tag tag = new Tag(dbTagItem.name(), 0, 0, dbTagItem.color());;
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
        double x = mainWindow.getX() + (mainWindow.getWidth() - popupWidth) / 2;
        double y = mainWindow.getY() + (mainWindow.getHeight() - popupHeight) / 2;

        x = Math.max(x, 0);
        y = Math.max(y, 0);

        TransactionsPage.applyBlur();
        addNewController.toggle(x, y);
    }

    public void removeBlurChild() {
        TransactionsPage.removeBlur();
    }

    public dbTransaction getTransaction() {
        return myTransaction;
    }

    public void refreshSingleTransaction() {
        TransactionsPage.refresh();
    }

    public void removeCard(int id){
        Task<Boolean> rimuoviT= Data.userDatabase.removeTransaction(id);
        rimuoviT.setOnSucceeded(event -> {

            parentCardTransactions.removeVbox(myTransaction);
            TransactionsPage.refresh();
        });
        executorService.submit(rimuoviT);
    }
}









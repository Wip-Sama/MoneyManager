package org.wip.moneymanager.components;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.GaussianBlur;
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
import org.wip.moneymanager.popUp.transactionPopupController;
import org.wip.moneymanager.utility.SVGLoader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Currency;
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
    private Label recipient; //destinatario

    @FXML
    private Label sender; //mittente

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
    private final static String on_fav = new SVGLoader("star").getPath();
    private final static String off_fav = new SVGLoader("Star_empty").getPath();

    private final Transactions Transactions;
    private final String originalBackgroundColor = "-fx-background-color: transparent;"; // Colore di sfondo originale
    private final String hoverBackgroundColor = "-fx-background-color: -fu-background-3;" + "-fx-background-radius: 6;"; // Colore di sfondo al passaggio del mouse (puoi cambiarlo)
    private TransactionInfoPopUp AddNewController;
    private CardTransactions parentCardTransactions;


    public SingleTransactionController(dbTransaction timestamp, Transactions transaction, CardTransactions parentCardTransactions) {
        myTransaction = timestamp;
        Transactions = transaction;
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
        Tooltip tooltip = new Tooltip("Doppio clic per dettagli");
        tooltip.setShowDelay(new Duration(1));
        tooltip.setHideDelay(new Duration(0));
        Tooltip.install(backGroundT, tooltip);
        deleteCard.textProperty().bind(Data.lsp.lsb("singleTransaction.deleteCard"));

        Tooltip backGroundTooltip = new Tooltip("Doppio clic per dettagli");
        backGroundTooltip.setShowDelay(new Duration(1));
        backGroundTooltip.setHideDelay(new Duration(0));


        Tooltip deleteCardTooltip = new Tooltip("Doppio clic per eliminare");
        deleteCardTooltip.setShowDelay(new Duration(1));
        deleteCardTooltip.setHideDelay(new Duration(0));


        deleteCard.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int id = myTransaction.id();
                removeCard(id);
                event.consume();
            }
        });

        deleteCard.setOnMouseEntered(event -> {
            Tooltip.install(deleteCard, deleteCardTooltip);
        });

        deleteCard.setOnMouseExited(event -> {
            Tooltip.uninstall(deleteCard, deleteCardTooltip);
        });


        backGroundT.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !event.getTarget().equals(deleteCard)) {
                try {
                    open_popup();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        backGroundT.setOnMouseMoved(event -> {
            if (!deleteCard.contains(event.getX() - deleteCard.getLayoutX(),
                    event.getY() - deleteCard.getLayoutY())) {
                Tooltip.install(backGroundT, backGroundTooltip);
            } else {
                Tooltip.uninstall(backGroundT, backGroundTooltip);
            }
        });

        backGroundT.setOnMouseExited(event -> {
            Tooltip.uninstall(backGroundT, backGroundTooltip);
        });




        generaTags();

        // Aggiungi gli eventi per il cambio di colore al passaggio del mouse
        backGroundT.setOnMouseEntered(event -> {
            backGroundT.setStyle(hoverBackgroundColor); // Cambia colore quando il mouse entra
        });

        backGroundT.setOnMouseExited(event -> {
            backGroundT.setStyle(originalBackgroundColor); // Ripristina il colore originale quando il mouse esce
        });

        Task<String> TaskNomeAccount = Data.userDatabase.getNameAccountFromId(myTransaction.account());
        TaskNomeAccount.setOnSucceeded(event -> {
            String Nome = TaskNomeAccount.getValue();
            sender.setText(Nome);
        });

        amount.setText(String.valueOf(myTransaction.amount()));

        if (myTransaction.favorite() != 0){
            starTransaction.setContent( on_fav);
        }

        buttonFavourite.setOnAction(event -> {
            if (myTransaction.favorite() == 0) {
                try {
                    myTransaction.setFavourite(1);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                starTransaction.setContent(on_fav);
            }
            else {
                try {
                    myTransaction.setFavourite(0);
                    parentCardTransactions.removeVbox(myTransaction);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                starTransaction.setContent( off_fav);

            }
        });

        if (myTransaction.type() == 1){
            amount.setStyle("-fx-text-fill: red;");
        } else if (myTransaction.type() == 0) {
            amount.setStyle("-fx-text-fill: green;");

        }
        if (myTransaction.type() == 2) {
            arrowTransaction.setVisible(true);
            recipient.setVisible(true);
            Task<String> TaskNomeSecondoAccount = Data.userDatabase.getNameAccountFromId(myTransaction.second_account());
            TaskNomeSecondoAccount.setOnSucceeded(event -> {
                String secondoNome = TaskNomeSecondoAccount.getValue();
                recipient.setText(secondoNome);
                categTransactions.setText("Trasferimento");
            });

        } else {
            try {
                categTransactions.setText(Data.userDatabase.getCategoryNameById(myTransaction.category()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void generaTags() {
        Task<List<dbTag>> loadTagsTask = Data.userDatabase.getTagFromTransaction(myTransaction.id());
        loadTagsTask.setOnSucceeded(event -> {
            List<dbTag> dbTags = loadTagsTask.getValue();
            if (dbTags != null) {
                for (dbTag dbTagItem : dbTags) {
                    Tag tag = new Tag(dbTagItem.name(), 0, 1, dbTagItem.color());
                    GridtagPane.getChildren().add(tag);
                }
            }
        });
        loadTagsTask.setOnFailed(event -> {
            Throwable exception = loadTagsTask.getException();
            exception.printStackTrace();
        });

        executorService.submit(loadTagsTask);
    }

    public boolean isFavorite() {
        if(myTransaction.favorite() == 1){
            return true;
        } else {
            return false;
        }
    }

    private void open_popup() throws IOException {

        if (AddNewController == null) {
            AddNewController = new TransactionInfoPopUp(backGroundT.getScene().getWindow(), this);
            AddNewController.getContextMenu().setOnHidden(event -> Transactions.removeBlur());
        }


        double popupWidth = 712.0; // Larghezza del popup
        double popupHeight = 400.0; // Altezza del popup

        // Ottieni le coordinate della finestra principale
        Window mainWindow = Transactions.getScene().getWindow();
        double windowX = mainWindow.getX();
        double windowY = mainWindow.getY();
        double windowWidth = mainWindow.getWidth();
        double windowHeight = mainWindow.getHeight();

        // Calcola le coordinate per il centro del popup rispetto alla finestra principale
        double x = windowX + (windowWidth - popupWidth) / 2;
        double y = windowY + (windowHeight - popupHeight) / 2;

        // Garantisci che il popup non esca dai bordi dello schermo
        x = Math.max(x, 0);
        y = Math.max(y, 0);

        Transactions.applyBlur();
        AddNewController.toggle(x, y);

    }


    public void removeBlurChild(){
        Transactions.removeBlur();
    }



    public dbTransaction getTransaction() {
        return myTransaction;
    }

    public void refreshSingleTransaction(){
        Transactions.refresh();
    }

    public void removeCard(int id){
        Data.userDatabase.removeTransaction(id);
        Transactions.refresh();
    }
}









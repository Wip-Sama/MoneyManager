package org.wip.moneymanager.components;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.model.DBObjects.dbTag;
import org.wip.moneymanager.model.DBObjects.dbTransaction;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.utility.SVGLoader;

import java.sql.SQLException;
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
    private Button buttonFavourite;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private dbTransaction myTransaction;
    private dbTransaction favourite;
    private final static String on_fav = new SVGLoader("star").getPath();
    private final static String off_fav = new SVGLoader("Star_empty").getPath();


    private final String originalBackgroundColor = "-fx-background-color: transparent;"; // Colore di sfondo originale
    private final String hoverBackgroundColor = "-fx-background-color: -fu-background-3;" + "-fx-background-radius: 6;"; // Colore di sfondo al passaggio del mouse (puoi cambiarlo)


    public SingleTransactionController(dbTransaction timestamp) {
        myTransaction = timestamp;
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
            Task<String> TaskNomeCategoria = Data.userDatabase.getCategoryFromId(myTransaction.category());
            TaskNomeCategoria.setOnSucceeded(event -> {
                String categoria = TaskNomeCategoria.getValue();
                categTransactions.setText(categoria);
            });
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
}









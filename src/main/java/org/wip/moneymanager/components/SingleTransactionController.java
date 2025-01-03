package org.wip.moneymanager.components;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.model.DBObjects.dbTag;
import org.wip.moneymanager.model.DBObjects.dbTransaction;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.Database;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleTransactionController extends AnchorPane {

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


        Task<String> TaskNomeAccount = Data.userDatabase.getNameAccountFromId(myTransaction.account());
        TaskNomeAccount.setOnSucceeded(event -> {
            String Nome = TaskNomeAccount.getValue();
            sender.setText(Nome);
        });

        amount.setText(String.valueOf(myTransaction.amount()));

        if (myTransaction.fauvorite() != 0){
            starTransaction.setContent("M10.788 3.103c.495-1.004 1.926-1.004 2.421 0l2.358 4.777 5.273.766c1.107.161 1.549 1.522.748 2.303l-3.816 3.72.901 5.25c.19 1.103-.968 1.944-1.959 1.424l-4.716-2.48-4.715 2.48c-.99.52-2.148-.32-1.96-1.424l.901-5.25-3.815-3.72c-.801-.78-.359-2.142.748-2.303L8.43 7.88l2.358-4.777Z");
        }

        buttonFavourite.setOnAction(event -> {
            if (myTransaction.fauvorite() == 0) {
                try {
                    myTransaction.setFavourite(1);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                starTransaction.setContent("M10.788 3.103c.495-1.004 1.926-1.004 2.421 0l2.358 4.777 5.273.766c1.107.161 1.549 1.522.748 2.303l-3.816 3.72.901 5.25c.19 1.103-.968 1.944-1.959 1.424l-4.716-2.48-4.715 2.48c-.99.52-2.148-.32-1.96-1.424l.901-5.25-3.815-3.72c-.801-.78-.359-2.142.748-2.303L8.43 7.88l2.358-4.777Z");
            }
            else {
                try {
                    myTransaction.setFavourite(0);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                starTransaction.setContent("M7.1939 2.10167C7.52403 1.43275 8.47789 1.43274 8.80802 2.10167L10.3291 5.18372L13.7304 5.67795C14.4685 5.78522 14.7633 6.69239 14.2291 7.21307L11.768 9.61212L12.349 12.9996C12.4751 13.7348 11.7034 14.2955 11.0431 13.9484L8.00096 12.349L4.95879 13.9484C4.29853 14.2955 3.52684 13.7348 3.65294 12.9996L4.23394 9.61212L1.77277 7.21307C1.23861 6.69239 1.53336 5.78522 2.27156 5.67795L5.67281 5.18372L7.1939 2.10167ZM8.00096 2.72593L6.54628 5.67343C6.41519 5.93906 6.16178 6.12317 5.86864 6.16577L2.61588 6.63842L4.9696 8.93273C5.18171 9.13949 5.27851 9.43739 5.22843 9.72935L4.6728 12.969L7.58215 11.4394C7.84434 11.3016 8.15758 11.3016 8.41977 11.4394L11.3291 12.969L10.7735 9.72935C10.7234 9.43739 10.8202 9.13949 11.0323 8.93273L13.386 6.63842L10.1333 6.16577C9.84014 6.12317 9.58673 5.93906 9.45564 5.67343L8.00096 2.72593Z");

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

    }









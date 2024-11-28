package org.wip.moneymanager.pages;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.components.CardConto;
import org.wip.moneymanager.components.ColorPickerPopup;
import org.wip.moneymanager.model.DBObjects.dbAccount;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.popUp.AddNewAccountController;
import org.wip.moneymanager.utility.SVGLoader;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Accounts extends BorderPane implements AutoCloseable {
    @FXML
    private VBox accounts_container;
    @FXML
    private ScrollPane scroll_miservesoloperingrandireilvbox;
    @FXML
    private ToggleButton hide_balance;
    @FXML
    private Button new_account;
    @FXML
    private Label your_accounts;
    @FXML
    private SVGPath eye_svg;

    protected Parent loaded;
    private AddNewAccountController AddNewAccount;

    private final static String show_eye = new SVGLoader("ic_fluent_eye_show_24_filled").getPath();
    private final static String hide_eye = new SVGLoader("ic_fluent_eye_hide_24_filled").getPath();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ReadOnlyBooleanProperty hideBalanceProperty() {
        return hide_balance.selectedProperty();
    }

    public Accounts() {
        Data.esm.register(executorService);
        try {
            FXMLLoader loader = new FXMLLoader(SceneHandler.class.getResource("/org/wip/moneymanager/pages/accounts.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loaded = loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void close() {
        //Tanto non va ma per ora lo lascio
        executorService.shutdown();
    }

    public double getAvailableSpace(ScrollPane scrollPane) {
        Bounds viewportBounds = scrollPane.getViewportBounds();
        Region content = (Region) scrollPane.getContent();
        double paddingLeft = content.getPadding().getLeft();
        double paddingRight = content.getPadding().getRight();
        return viewportBounds.getWidth() - paddingLeft - paddingRight;
    }

    private void initialize_accounts() throws ExecutionException, InterruptedException {
        Task<List<dbAccount>> accounts = Data.userDatabase.getAllAccounts();
        accounts.run();
        for (dbAccount account : accounts.get()) {
            CardConto accountComponent = new CardConto(account);
            accountComponent.destructProperty().addListener((_, _, newValue) -> {
                if (newValue) {
                    accounts_container.getChildren().remove(accountComponent);
                    Task<Boolean> delete = Data.userDatabase.forceRemoveAccount(account.id());
                    delete.run();
                }
            });
            accountComponent.hideBalanceProperty().bind(hide_balance.selectedProperty());
            accounts_container.getChildren().add(accountComponent);
        }
    }

    public void initialize() throws ExecutionException, InterruptedException {
        initialize_accounts();


        scroll_miservesoloperingrandireilvbox.viewportBoundsProperty().addListener((_, _, _) -> {
            double availableSpace = getAvailableSpace(scroll_miservesoloperingrandireilvbox);
            accounts_container.setPrefWidth(availableSpace);
        });

        your_accounts.textProperty().bind(Data.lsp.lsb("accounts.your_accounts"));
        new_account.textProperty().bind(Data.lsp.lsb("accounts.new_account"));
        hide_balance.textProperty().bind(Data.lsp.lsb("accounts.hide_balance"));

        hide_balance.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                eye_svg.setContent(show_eye);
                hide_balance.textProperty().bind(Data.lsp.lsb("accounts.show_balance"));
            } else {
                eye_svg.setContent(hide_eye);
                hide_balance.textProperty().bind(Data.lsp.lsb("accounts.hide_balance"));
            }
        });

        new_account.onActionProperty().set(_ ->open_popup());
    }

    private void open_popup() {
        if (AddNewAccount == null) {
            try {
                AddNewAccount = new AddNewAccountController(new_account.getScene().getWindow(), this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Bounds bounds = new_account.localToScreen(new_account.getBoundsInLocal());


        double x = bounds.getMaxX() - 463;
        double y = bounds.getMaxY();


        AddNewAccount.toggle(x, y);
    }

    public void refreshAccounts() throws ExecutionException, InterruptedException {
        accounts_container.getChildren().clear(); // Svuota il contenitore
        initialize_accounts();
    }
}

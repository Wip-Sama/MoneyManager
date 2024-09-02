package org.wip.moneymanager.pages;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import org.wip.moneymanager.MoneyManager;
import org.wip.moneymanager.components.CardConto;
import org.wip.moneymanager.model.DBObjects.dbAccount;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.utility.SVGLoader;

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

    private final static String show_eye = new SVGLoader("ic_fluent_eye_show_24_filled").getPath();
    private final static String hide_eye = new SVGLoader("ic_fluent_eye_hide_24_filled").getPath();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ReadOnlyBooleanProperty hideBalanceProperty() {
        return hide_balance.selectedProperty();
    }

    public Accounts() {
        Data.esm.register(executorService);
        try {
            FXMLLoader loader = new FXMLLoader(MoneyManager.class.getResource("pages/accounts.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
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
    }
}

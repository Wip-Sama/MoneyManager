package org.wip.moneymanager.popUp;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.wip.moneymanager.components.BalanceEditor;
import org.wip.moneymanager.components.Switch;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.types.AccountType;
import org.wip.moneymanager.pages.Accounts;
import java.io.IOException;
import java.time.ZoneId;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AddNewAccountController extends BorderPane {

    @FXML
    private Label LabelNewAccount;

    @FXML
    private BalanceEditor bilanceField;

    @FXML
    private Button cancelButton;

    @FXML
    private DatePicker dateField;

    @FXML
    private Switch includeSwitch;

    @FXML
    private Label labelBilance;

    @FXML
    private Label labelDate;

    @FXML
    private Label labelInclude;

    @FXML
    private Label labelTitlePop;

    @FXML
    private Label labelType;

    @FXML
    private TextField newAccountField;

    @FXML
    private BorderPane popUpPanell;

    @FXML
    private Button saveButton;

    @FXML
    private ChoiceBox<String> typeAccountField;

    private double xOffset = 0;
    private double yOffset = 0;
    private Accounts accountsPage;
    private final Popup popup = new Popup();
    private final Window ownerWindow;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public AddNewAccountController(Window window, Accounts accountsPage) throws IOException {
        Data.esm.register(executorService);
        this.accountsPage = accountsPage;
        this.ownerWindow = window;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/popUp/addNewAccount.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        Parent loaded = fxmlLoader.load();
        popup.getContent().add(loaded);
        window.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, _ -> hide());
    }

    @FXML
    public void initialize() {
        update_type_field();
        popUpPanell.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        popUpPanell.setOnMouseDragged(event -> {
            popup.setX(event.getScreenX() - xOffset);
            popup.setY(event.getScreenY() - yOffset);
        });

        cancelButton.setOnAction(event -> {
            clearFields();
            hide();
        });

        saveButton.setOnAction(event -> {
            try {
                saveNewAccount();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void show() {
        popup.show(ownerWindow);
    }

    public void hide() {
        popup.hide();
    }

    private void clearFields() {
        newAccountField.clear();
        bilanceField.reset();
        dateField.setValue(null);
        includeSwitch.reset();
        typeAccountField.getSelectionModel().clearSelection();
    }

    private void update_type_field() {
        int selectedIndex = typeAccountField.getSelectionModel().getSelectedIndex();
        typeAccountField.getItems().setAll(FXCollections.observableArrayList(
                Data.lsp.lsb("accounttype.cash").get(),
                Data.lsp.lsb("accounttype.bank").get(),
                Data.lsp.lsb("accounttype.credit_card").get(),
                Data.lsp.lsb("accounttype.debit_card").get(),
                Data.lsp.lsb("accounttype.savings").get(),
                Data.lsp.lsb("accounttype.investment").get(),
                Data.lsp.lsb("accounttype.loan").get()
        ));
        typeAccountField.getSelectionModel().select(selectedIndex);
    }

    private void saveNewAccount() throws ExecutionException, InterruptedException {
        String name = newAccountField.getText();
        double balance = bilanceField.getBalance();
        int creationDate = (int) dateField.getValue().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        int includeIntoTotals = includeSwitch.getState() ? 0 : 1;
        int accountType = typeAccountField.getSelectionModel().getSelectedIndex();
        String currency = bilanceField.getCurrency();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Aggiungiamo un controllo sulla connessione prima di tentare l'inserimento
                    if (Data.userDatabase.isConnected()) {
                        System.out.println("Database connected, proceeding with insert.");
                        Data.userDatabase.addAccount(name, accountType, balance, creationDate, includeIntoTotals, currency);
                    } else {
                        System.out.println("Database connection failed.");
                    }
                } catch (Exception e) {
                    System.err.println("Error occurred while adding account: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }
        };

        executorService.submit(task);
        clearFields();
        hide();
        accountsPage.refreshAccounts();
    }

}


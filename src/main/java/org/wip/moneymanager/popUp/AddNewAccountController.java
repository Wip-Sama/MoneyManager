package org.wip.moneymanager.popUp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import javafx.util.Duration;
import org.wip.moneymanager.components.BalanceEditor;
import org.wip.moneymanager.components.Switch;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.pages.Accounts;
import org.wip.moneymanager.utility.FieldAnimationUtils;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static javafx.scene.paint.Color.rgb;

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
    private Label ErrorLabel;

    @FXML
    private ChoiceBox<String> typeAccountField;

    private double xOffset = 0;
    private double yOffset = 0;
    private Accounts accountsPage;
    private final CustomMenuItem customMenuItem;
    private final ContextMenu contextMenu = new ContextMenu();
    private final Window node;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public AddNewAccountController(Window window, Accounts accountsPage) throws IOException {
        Data.esm.register(executorService);
        this.accountsPage = accountsPage;
        node = window;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/popUp/addNewAccount.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        Parent loaded = fxmlLoader.load();

        customMenuItem = new CustomMenuItem(loaded);
        customMenuItem.getStyleClass().add("tag-filter-menu-item");
        customMenuItem.hideOnClickProperty().set(false);
        contextMenu.getItems().add(customMenuItem);

        window.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, _ -> hide());
    }

    @FXML
    public void initialize() {

        ErrorLabel.setOpacity(0);
        // Traduzioni tramite lsp
        cancelButton.setText(Data.lsp.lsb("newAddAccount.cancelButtonLabel").get());
        saveButton.setText(Data.lsp.lsb("newAddAccount.saveButtonLabel").get());
        LabelNewAccount.setText(Data.lsp.lsb("newAddAccount.newAccountLabel").get());
        labelTitlePop.setText(Data.lsp.lsb("newAddAccount.newAccountLabelTitle").get());
        labelBilance.setText(Data.lsp.lsb("newAddAccount.balanceLabel").get());
        labelDate.setText(Data.lsp.lsb("newAddAccount.dateLabel").get());
        labelInclude.setText(Data.lsp.lsb("newAddAccount.includeLabel").get());
        labelType.setText(Data.lsp.lsb("newAddAccount.typeLabel").get());
        ErrorLabel.textProperty().bind(Data.lsp.lsb("addnewaccount.error"));

        update_type_field();
        cancelButton.setOnAction(event -> {
            clearFields();
            hide();
        });

        saveButton.setOnAction(event -> {
            try {
                if (validateFields()) {
                    saveNewAccount();
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // Listener per il campo username
        newAccountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                FieldAnimationUtils.removeErrorStyles(newAccountField);  // Rimuove gli stili di errore
                ErrorLabel.setOpacity(0);  // Nasconde il messaggio di errore
            }
        });

        // Listener per il campo password
        bilanceField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                FieldAnimationUtils.removeErrorStyles(bilanceField); // Remove error styles
                ErrorLabel.setOpacity(0);  // Hide the alert when the user starts typing
            }
        });

        // Listener per il campo password
        dateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                FieldAnimationUtils.removeErrorStyles(dateField);
                ErrorLabel.setOpacity(0);  // Nascondi l'alert quando l'utente inizia a scrivere
            }
        });

        typeAccountField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Verifica se è stato selezionato un valore (non deve essere null o vuoto)
            if (newValue != null && !newValue.isEmpty()) {
                // Rimuove gli stili di errore se l'utente seleziona un valore
                FieldAnimationUtils.removeErrorStyles(typeAccountField);
                ErrorLabel.setOpacity(0);  // Nasconde l'alert
            }
        });

    }

    public void show(double x, double y) {
        contextMenu.show(node, x, y);
    }


    public void hide() {
        contextMenu.hide();
    }

    private void clearFields() {
        newAccountField.clear();
        bilanceField.reset();
        dateField.setValue(null);
        includeSwitch.reset();
        typeAccountField.getSelectionModel().clearSelection();

        FieldAnimationUtils.removeErrorStyles(newAccountField);
        FieldAnimationUtils.removeErrorStyles(bilanceField);
        FieldAnimationUtils.removeErrorStyles(dateField);
        FieldAnimationUtils.removeErrorStyles(dateField);
        FieldAnimationUtils.removeErrorStyles(typeAccountField);

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

    // Funzione di validazione dei campi
    private boolean validateFields() {
        String name = newAccountField.getText();
        String balanceText = bilanceField.getText(); // Accediamo al campo testuale direttamente
        int accountType = typeAccountField.getSelectionModel().getSelectedIndex();





        AtomicBoolean hasError = new AtomicBoolean(false);

        // Controlla se il nome è vuoto
        if (name == null || name.trim().isEmpty()) {
            FieldAnimationUtils.animateFieldError(newAccountField);
            hasError.set(true);
        }


        if (dateField.getValue() == null) {
            FieldAnimationUtils.animateFieldError(dateField);
            hasError.set(true);
        }

        // Controlla se il balance è vuoto
        if (balanceText == null || balanceText.trim().isEmpty()) {
            FieldAnimationUtils.animateFieldError(bilanceField);
            hasError.set(true);
        }

        // Controlla se il tipo di account è selezionato
        if (accountType == -1) {
            FieldAnimationUtils.animateFieldError(typeAccountField);
            hasError.set(true);
        }

        // Mostra un unico messaggio di errore se c'è un errore
        if (hasError.get()) {
            showError("addnewaccount.error");
        }

        Task<List<String>> namesAccountsTask = Data.userDatabase.getAllAccountNames();
        namesAccountsTask.setOnSucceeded(event -> {
            List<String> accountNames = namesAccountsTask.getValue();
            if (accountNames.contains(name)) {
                hasError.set(true);
                showError("addnewaccount.samename");
                FieldAnimationUtils.animateFieldError(newAccountField);
            }
        });
        executorService.submit(namesAccountsTask);


        return !hasError.get(); // Valido se non ci sono errori
    }




    private void showError(String message) {
        ErrorLabel.textProperty().bind(Data.lsp.lsb(message));
        ErrorLabel.setTextFill(rgb(255,0,0));
        Timeline fadeInTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> ErrorLabel.setOpacity(0)),
                new KeyFrame(Duration.seconds(0.2), e -> ErrorLabel.setOpacity(1))
        );
        fadeInTimeline.setCycleCount(1);
        fadeInTimeline.play();
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
                        Data.userDatabase.addAccount(name, accountType, balance, creationDate, includeIntoTotals, currency);
                } catch (Exception e) {
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

    public void toggle(double x, double y) {
        if (contextMenu.isShowing()) {
            hide();
        } else {
            show(x, y);
        }
    }

}

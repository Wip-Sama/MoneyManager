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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private ComboBox<String>  typeAccountField;

    private double xOffset = 0;
    private double yOffset = 0;
    private Accounts accountsPage;
    private final CustomMenuItem customMenuItem;
    private final ContextMenu contextMenu = new ContextMenu();
    private final Window node;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public AddNewAccountController(Window window, Accounts accountsPage) throws IOException {
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
        Data.esm.register(executorService);
        typeAccountField.setEditable(false);
        bilanceField.setBalance(0);
        ErrorLabel.setOpacity(0);
        dateField.setValue(LocalDate.now());
        includeSwitch.setState(true);

        // fa in modo che il datepicker non possa essere modificato manualmente
        dateField.setEditable(false);
        dateField.getEditor().setEditable(false);
        dateField.getEditor().setOnMouseClicked(e -> {
            if (!dateField.isShowing()) {
                dateField.show();
            }
        });

        dateField.setValue(LocalDate.now());
        includeSwitch.setState(true);


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
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
        }
        });

        newAccountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                FieldAnimationUtils.removeErrorStyles(newAccountField);
                ErrorLabel.setOpacity(0);
            }
        });

        bilanceField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                FieldAnimationUtils.removeErrorStyles(bilanceField);
                ErrorLabel.setOpacity(0);
            }
        });

        dateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                FieldAnimationUtils.removeErrorStyles(dateField);
                ErrorLabel.setOpacity(0);
            }
        });

        typeAccountField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                FieldAnimationUtils.removeErrorStyles(typeAccountField);
                ErrorLabel.setOpacity(0);
            }
        });

    }

    public void show(double x, double y) {
        contextMenu.show(node, x, y);
    }


    public void hide() {
        clearErrore();
        contextMenu.hide();
    }

    private void clearFields() {
        newAccountField.clear();
        bilanceField.reset();
        dateField.setValue(null);
        includeSwitch.reset();
        typeAccountField.getSelectionModel().clearSelection();
    }

    public void clearErrore(){
        FieldAnimationUtils.removeErrorStyles(newAccountField);
        FieldAnimationUtils.removeErrorStyles(bilanceField);
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

    private boolean validateFields() {
        String name = newAccountField.getText();
        String balanceText = bilanceField.getText();
        int accountType = typeAccountField.getSelectionModel().getSelectedIndex();

        AtomicBoolean hasError = new AtomicBoolean(false);
        AtomicBoolean calendarError = new AtomicBoolean(false);

        if (name == null || name.trim().isEmpty()) {
            FieldAnimationUtils.animateFieldError(newAccountField);
            hasError.set(true);
        }

        if (dateField.getValue() == null) {
            FieldAnimationUtils.animateFieldError(dateField);
            hasError.set(true);
        }else if (dateField.getValue().isAfter(LocalDate.now())) {
            FieldAnimationUtils.animateFieldError(dateField);
            calendarError.set(true);
        }


        if (balanceText == null || balanceText.trim().isEmpty()) {
            FieldAnimationUtils.animateFieldError(bilanceField);
            hasError.set(true);
        }

        if (accountType == -1) {
            FieldAnimationUtils.animateFieldError(typeAccountField);
            hasError.set(true);
        }

        if (hasError.get()) {
            showError("addnewaccount.error");
        } else if (calendarError.get()) {
            showError("transactionPopUpController.error.future.date");
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

        return !hasError.get() && !calendarError.get();
    }


    private void showError(String message) {
        ErrorLabel.textProperty().bind(Data.lsp.lsb(message));
        //ErrorLabel.setTextFill(rgb(255,0,0));
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

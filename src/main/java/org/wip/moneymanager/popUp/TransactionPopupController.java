package org.wip.moneymanager.popUp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Duration;
import org.wip.moneymanager.components.CategorySelector;
import org.wip.moneymanager.components.TagFilter;
import org.wip.moneymanager.components.TagSelector;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.components.BalanceEditor;
import org.wip.moneymanager.pages.Transactions;
import org.wip.moneymanager.utility.FieldAnimationUtils;

import java.time.ZoneId;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionPopupController extends BorderPane {

    @FXML private VBox ContainerAccountCategory;
    @FXML private BorderPane BoderPanePopup;
    @FXML private Label labelTitle;
    @FXML private ToggleButton incomeButton;
    @FXML private ToggleButton expenseButton;
    @FXML private ToggleButton transferButton;
    @FXML private Label date;
    @FXML private DatePicker datePicker;
    @FXML private Label amount;
    @FXML private BalanceEditor balanceEditor;
    @FXML private Label category;
    @FXML private ComboBox<String> SecondoAccountComboBox;
    @FXML private Label account;
    @FXML private ComboBox<String> accountComboBox;
    @FXML private Label tags;
    @FXML private TagSelector tagSelector;
    @FXML private TextArea notes;
    @FXML private Label notesLabel;
    @FXML private CategorySelector categorySelector;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private final CustomMenuItem customMenuItem;
    private final ContextMenu contextMenu = new ContextMenu();
    private final Window node;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<String> accountNames;
    private boolean isTransfer;
    private final Transactions transaction;

    public TransactionPopupController(Window window, Transactions tr) throws IOException {
        this.node = window;
        this.transaction = tr;


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/popUp/transactionPopUp.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        Parent loaded = fxmlLoader.load();

        // configurazione del menu contestuale
        customMenuItem = new CustomMenuItem(loaded);
        customMenuItem.getStyleClass().add("tag-filter-menu-item");
        customMenuItem.hideOnClickProperty().set(false);
        contextMenu.getItems().add(customMenuItem);

        // nasconde il popup quando si clicca fuori
        window.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, _ -> hide());
    }

    @FXML
    private void initialize() {

        Data.esm.register(executorService);

        // imposta i testi di label e dei pulsanti
        labelTitle.setText(Data.lsp.lsb("transactionPopUpController.title").get());
        incomeButton.setText(Data.lsp.lsb("transactionPopUpController.income").get());
        expenseButton.setText(Data.lsp.lsb("transactionPopUpController.expense").get());
        transferButton.setText(Data.lsp.lsb("transactionPopUpController.transfer").get());

        date.setText(Data.lsp.lsb("transactionPopUpController.date").get());
        amount.setText(Data.lsp.lsb("transactionPopUpController.amount").get());
        account.setText(Data.lsp.lsb("transactionPopUpController.account").get());
        category.setText(Data.lsp.lsb("transactionPopUpController.category").get());
        tags.setText(Data.lsp.lsb("transactionPopUpController.tags").get());
        saveButton.setText(Data.lsp.lsb("transactionPopUpController.save").get());
        cancelButton.setText(Data.lsp.lsb("transactionPopUpController.cancel").get());
        errorLabel.setText(Data.lsp.lsb("transactionPopUpController.error.fields").get());
        notesLabel.setText(Data.lsp.lsb("transactionPopUpController.notesLabel").get());

        errorLabel.setOpacity(0);

        // task per caricare i nomi degli account dal database
        Task<List<String>> namesAccountsTask = Data.userDatabase.getAllAccountNames();
        namesAccountsTask.setOnSucceeded(event -> {
            accountNames = namesAccountsTask.getValue();
            if (accountNames != null) {
                accountComboBox.getItems().setAll(accountNames);
                SecondoAccountComboBox.getItems().setAll(accountNames);
            }
        });
        namesAccountsTask.setOnFailed(event -> {
            System.err.println("Errore nel caricamento degli account: " + namesAccountsTask.getException().getMessage());
        });

        executorService.submit(namesAccountsTask);

        // configurazione iniziale della visibilità dei componenti
        SecondoAccountComboBox.setVisible(false);
        SecondoAccountComboBox.setManaged(false);

        accountComboBox.getStyleClass().add("transaction-combo-box");
        SecondoAccountComboBox.getStyleClass().add("transaction-combo-box");

        categorySelector.setVisible(true);
        categorySelector.setManaged(true);

        tagSelector.clearTags();
        categorySelector.populateMainCategoriesForIncome();
        datePicker.setValue(LocalDate.now());



        // azione per il pulsante cancel
        cancelButton.setOnAction(e -> hide());

        // configurazione del gruppo di toggle per i pulsanti di tipo di transazione
        ToggleGroup toggleGroup = new ToggleGroup();
        incomeButton.setToggleGroup(toggleGroup);
        expenseButton.setToggleGroup(toggleGroup);
        transferButton.setToggleGroup(toggleGroup);

        incomeButton.setSelected(true);
        categorySelector.populateMainCategoriesForIncome();

        // listener per gestire la selezione dei pulsanti del tipo di transazione
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                toggleGroup.selectToggle(oldValue);
            }
        });

        // configurazione del datePicker
        datePicker.getEditor().setEditable(false);
        datePicker.setEditable(false);
        datePicker.getEditor().setOnMouseClicked(e -> {
            if (!datePicker.isShowing()) {
                datePicker.show();
            }
        });

        // azioni per i pulsanti di tipo di transazione
        incomeButton.setOnAction(e -> {
            onToggleButtonChange(false);
            categorySelector.populateMainCategoriesForIncome();
        });

        expenseButton.setOnAction(e -> {
            onToggleButtonChange(false);
            categorySelector.populateMainCategoriesForExpense();
        });

        transferButton.setOnAction(e -> {
            onToggleButtonChange(true);
        });

        // listener per la validazione del campo dell'importo
        balanceEditor.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                FieldAnimationUtils.removeErrorStyles(balanceEditor);
                errorLabel.setOpacity(0);
            }
        });

        // listener per la validazione del campo dell'account
        accountComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                FieldAnimationUtils.removeErrorStyles(accountComboBox);
                errorLabel.setOpacity(0);
            }
        });

        // listener per la validazione del campo del secondo account (trasferimenti)
        SecondoAccountComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                FieldAnimationUtils.removeErrorStyles(SecondoAccountComboBox);
                errorLabel.setOpacity(0);
            }
        });

        // listener per la validazione della categoria principale
        categorySelector.getCategoryBox().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                FieldAnimationUtils.removeErrorStyles(categorySelector.getCategoryBox());
                errorLabel.setOpacity(0);
            }
        });

        // listener per la validazione della sottocategoria
        categorySelector.getSubCategoryBox().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                FieldAnimationUtils.removeErrorStyles(categorySelector.getSubCategoryBox());
                errorLabel.setOpacity(0);
            }
        });

        // listener per la validazione della data
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                FieldAnimationUtils.removeErrorStyles(datePicker);
                errorLabel.setOpacity(0);
            }
        });

        // onaction del pulsante save
        saveButton.setOnAction(event -> {
            clearError();
            if (validateFields()) {
                try {
                    int transactionType = getTransactionType();
                    int transactionDate = (int) datePicker.getValue()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toEpochSecond();
                    double amount = Double.parseDouble(balanceEditor.getText().replace(',','.'));

                    // la task per salvare la transazione nel db
                    Task<Boolean> saveTask = Data.userDatabase.addNewTransaction(
                            transactionDate,
                            transactionType,
                            amount,
                            accountComboBox.getValue(),
                            isTransfer ? SecondoAccountComboBox.getValue() : null,
                            notes.getText(),
                            isTransfer ? null : categorySelector.getSelectedCategory(),
                            tagSelector.get_selected_tags()
                    );

                    saveTask.setOnSucceeded(e -> {
                        if (saveTask.getValue()) {
                            resetScreen();
                            transaction.refresh();
                            hide();
                        } else {
                            showError("transactionPopUpController.error.save");
                        }
                    });

                    saveTask.setOnFailed(e -> {
                        System.err.println("Error saving transaction: " + saveTask.getException());
                        showError("transactionPopUpController.error.save");
                    });

                    executorService.submit(saveTask);

                } catch (Exception e) {
                    System.err.println("Error preparing transaction: " + e.getMessage());
                    showError("transactionPopUpController.error.generic");
                }
            }


        });

        // onaction del pulsante cancel
        cancelButton.setOnAction(event -> {
            resetScreen();
            hide();
        });
    }

    // metodo che popola le combobox degli account
    private void populateComboBoxes(boolean isTransfer) {
        if (accountNames == null) {
            return;
        }

        if (this.isTransfer) {
            account.setText(Data.lsp.lsb("transactionPopUpController.sender").get());
            category.setText(Data.lsp.lsb("transactionPopUpController.recipient").get());

            accountComboBox.setOnAction(e -> {
                String selected = accountComboBox.getValue();
                if (selected != null) {
                    SecondoAccountComboBox.getItems().setAll(
                            accountNames.stream()
                                    .filter(acc -> !acc.equals(selected))
                                    .toList()
                    );
                }
            });

            secondAccountDefault();

        } else {
            accountComboBox.getItems().setAll(accountNames);
            account.setText(Data.lsp.lsb("transactionPopUpController.account").get());
            category.setText(Data.lsp.lsb("transactionPopUpController.category").get());
        }

    }

    private void secondAccountDefault(){
        accountComboBox.getItems().setAll(accountNames);
        if (accountComboBox.getValue() == null) {
            SecondoAccountComboBox.getItems().setAll(
                    accountNames.stream()
                            .filter(acc -> !acc.equals(accountComboBox.getValue()))
                            .toList()
            );
        }
    }

    // metodo che valida i campi del popup
    private boolean validateFields() {
        boolean hasError = false;
        int errorCount = 0;

        // Verifica se tutti i campi richiesti sono vuoti
        boolean allFieldsEmpty = (accountComboBox.getValue() == null || accountComboBox.getValue().trim().isEmpty()) &&
                (balanceEditor.getText() == null || balanceEditor.getText().trim().isEmpty()) &&
                datePicker.getValue() == null &&
                (!isTransfer && (categorySelector.getSelectedCategory() == null ||
                        categorySelector.getSelectedCategory().trim().isEmpty()));

        if (allFieldsEmpty) {

            FieldAnimationUtils.animateFieldError(accountComboBox);
            FieldAnimationUtils.animateFieldError(balanceEditor);
            FieldAnimationUtils.animateFieldError(datePicker);
            if (!isTransfer) {
                categorySelector.animateError();
            }
            showError("transactionPopUpController.error.all_fields");
            return false;
        }

        if (accountComboBox.getValue() == null || accountComboBox.getValue().trim().isEmpty()) {
            FieldAnimationUtils.animateFieldError(accountComboBox);
            showError("transactionPopUpController.error.account");
            hasError = true;
            errorCount++;
        }

        if (balanceEditor.getText() == null || balanceEditor.getText().trim().isEmpty()) {
            FieldAnimationUtils.animateFieldError(balanceEditor);
            if (!hasError) {
                showError("transactionPopUpController.error.amount");
            }
            hasError = true;
            errorCount++;
        } else {
            try {
                double amount = Double.parseDouble(balanceEditor.getText().replace(',','.'));
                if (amount <= 0) {
                    FieldAnimationUtils.animateFieldError(balanceEditor);
                    if (!hasError) {
                        showError("transactionPopUpController.error.amount.zero");
                    }
                    hasError = true;
                    errorCount++;
                }
            } catch (NumberFormatException e) {
                FieldAnimationUtils.animateFieldError(balanceEditor);
                if (!hasError) {
                    showError("transactionPopUpController.error.amount.invalid");
                }
                hasError = true;
                errorCount++;
            }
        }

        if (datePicker.getValue() == null) {
            FieldAnimationUtils.animateFieldError(datePicker);
            if (!hasError) {
                showError("transactionPopUpController.error.date");
            }
            hasError = true;
            errorCount++;
        }else if (datePicker.getValue().isAfter(LocalDate.now())) {
            FieldAnimationUtils.animateFieldError(datePicker);
            if (!hasError) {
                showError("transactionPopUpController.error.future.date");
            }
            hasError = true;
            errorCount++;
        }

        // Validazione categoria/secondo account in base al tipo di transazione
        if (isTransfer) {
            if (SecondoAccountComboBox.getValue() == null || SecondoAccountComboBox.getValue().trim().isEmpty()) {
                FieldAnimationUtils.animateFieldError(SecondoAccountComboBox);
                if (!hasError) {
                    showError("transactionPopUpController.error.recipient");
                }
                hasError = true;
                errorCount++;
            }
        } else {
            String mainCategory = categorySelector.getSelectedCategory();
            if (mainCategory == null || mainCategory.trim().isEmpty()) {
                categorySelector.animateError();
                if (!hasError) {
                    showError("transactionPopUpController.error.category");
                }
                hasError = true;
                errorCount++;
            }
        }

        // Mostra errore generico se ci sono più campi non validi
        if (errorCount > 1) {
            showError("transactionPopUpController.error.all_fields");
        }

        return !hasError;
    }



    // metodo che ottiene il tipo di transazione
    private int getTransactionType() {
        if (transferButton.isSelected()) {
            return 2; // Trasferimento
        }
        return incomeButton.isSelected() ? 0 : 1; // 0 = Entrata, 1 = Uscita
    }

    // metodo che mostra gli errori
    private void showError(String message) {
        errorLabel.setOpacity(1);
        errorLabel.textProperty().bind(Data.lsp.lsb(message));
        Timeline fadeInTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> errorLabel.setOpacity(0)),
                new KeyFrame(Duration.seconds(0.2), e -> errorLabel.setOpacity(1))
        );
        fadeInTimeline.setCycleCount(1);
        fadeInTimeline.play();
    }

    // metodo per rimuovere gli errori dai campi
    private void clearError() {
        FieldAnimationUtils.removeErrorStyles(accountComboBox);
        FieldAnimationUtils.removeErrorStyles(SecondoAccountComboBox);
        FieldAnimationUtils.removeErrorStyles(balanceEditor);
        FieldAnimationUtils.removeErrorStyles(datePicker);
        categorySelector.removeError();
        errorLabel.setOpacity(0);
    }


    // metodo per resettare la schermata
    private void resetScreen() {
            LocalDate currentDate = datePicker.getValue();
            datePicker.setValue(currentDate != null ? currentDate : LocalDate.now());

            accountComboBox.getSelectionModel().clearSelection();
            secondAccountDefault();
            //accountComboBox.setOnAction(null);
            //SecondoAccountComboBox.setOnAction(null);
            //account.setText("");
            //category.setText("");
            balanceEditor.reset();
            notes.setText("");
            categorySelector.discard();
            tagSelector.clearTags();
            TagFilter.refreshTags();
    }

    // metodo che gestisce il cambio del tipo di transazione
    private void onToggleButtonChange(boolean isTransfer) {

        clearError();

        if (this.isTransfer != isTransfer) {
            resetScreen();
            this.isTransfer = isTransfer;
        }

        if (isTransfer) {
            categorySelector.setVisible(false);
            categorySelector.setManaged(false);

            SecondoAccountComboBox.setVisible(true);
            SecondoAccountComboBox.setManaged(true);

            account.setText(Data.lsp.lsb("transactionPopUpController.sender").get());
            category.setText(Data.lsp.lsb("transactionPopUpController.recipient").get());
        } else {
            SecondoAccountComboBox.setVisible(false);
            SecondoAccountComboBox.setManaged(false);

            categorySelector.setVisible(true);
            categorySelector.setManaged(true);
        }

        populateComboBoxes(isTransfer);

    }

    // metodo per nascondere il popup
    private void hide() {
        clearError();
        contextMenu.hide();
    }

    // metodo per mostrare il popup
    public void show(double x, double y) {
        incomeButton.setSelected(true);
        onToggleButtonChange(false);


        contextMenu.show(node, x, y);
    }

    // metodo per alternare la visibilita' del popup
    public void toggle(double x, double y) {
        if (contextMenu.isShowing()) {
            hide();
        } else {
            show(x, y);
        }
    }
}
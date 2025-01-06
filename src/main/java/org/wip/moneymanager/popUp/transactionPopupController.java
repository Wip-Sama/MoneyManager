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
import org.wip.moneymanager.model.UserDatabase;
import org.wip.moneymanager.utility.FieldAnimationUtils;

import java.util.concurrent.atomic.AtomicBoolean;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javafx.scene.paint.Color.rgb;

public class transactionPopupController extends BorderPane {

    @FXML
    private VBox ContainerAccountCategory;

    @FXML
    private BorderPane BoderPanePopup;
    @FXML
    private Label labelTitle;
    @FXML
    private ToggleButton incomeButton;
    @FXML
    private ToggleButton expenseButton;
    @FXML
    private ToggleButton transferButton;
    @FXML
    private Label date;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label amount;
    @FXML
    private BalanceEditor balanceEditor;
    @FXML
    private Label category;
    @FXML
    private ComboBox<String> SecondoAccountComboBox;
    @FXML
    private Label account;
    @FXML
    private ComboBox<String> accountComboBox;
    @FXML
    private Label tags;
    @FXML
    private TagSelector tagSelector;
    @FXML
    private TextArea notes;
    @FXML
    private CategorySelector categorySelector;
    @FXML
    private Label errorLabel;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final CustomMenuItem customMenuItem;
    private final ContextMenu contextMenu = new ContextMenu();
    private final Window node;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<String> accountNames;
    private List<String> categoryNames;
    private String lastTransactionType = "income"; // Valore iniziale
    private boolean isTransfer;

    public transactionPopupController(Window window) throws IOException {
        this.node = window;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/popUp/transactionPopUp.fxml"));
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
    private void initialize() {
        Data.esm.register(executorService);

        errorLabel.setOpacity(0);

        // Inizializza accountNames list tramite Task
        Task<List<String>> namesAccountsTask = Data.userDatabase.getAllAccountNames();
        namesAccountsTask.setOnSucceeded(event -> {
            accountNames = namesAccountsTask.getValue(); // Salva i risultati nella variabile membro
            if (accountNames != null) {
                accountComboBox.getItems().setAll(accountNames); // Popola accountComboBox
                SecondoAccountComboBox.getItems().setAll(accountNames); // Popola SecondoAccountComboBox se necessario
            }
        });
        namesAccountsTask.setOnFailed(event -> {
            System.err.println("Errore nel caricamento degli account: " + namesAccountsTask.getException().getMessage());
        });

        executorService.submit(namesAccountsTask);

        // Imposta visibilità iniziale dei componenti
        SecondoAccountComboBox.setVisible(false);
        SecondoAccountComboBox.setManaged(false);

        categorySelector.setVisible(true);
        categorySelector.setManaged(true);


        cancelButton.setOnAction(e -> hide());

        ToggleGroup toggleGroup = new ToggleGroup();
        incomeButton.setToggleGroup(toggleGroup);
        expenseButton.setToggleGroup(toggleGroup);
        transferButton.setToggleGroup(toggleGroup);

        incomeButton.setSelected(true);
        categorySelector.populateMainCategoriesForIncome();

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                toggleGroup.selectToggle(oldValue);
            }
        });

        datePicker.getEditor().setEditable(false);
        datePicker.setEditable(false);
        datePicker.getEditor().setOnMouseClicked(e -> {
            if (!datePicker.isShowing()) {
                datePicker.show();
            }
        });

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

        saveButton.setOnAction(event -> {
            clearError();
            if (validateFields()) {
                hide();
                resetScreen();
            }
        });

        cancelButton.setOnAction(event -> {
            resetScreen();
            hide();
        });
    }




    private void populateComboBoxes(boolean isTransfer) {
        if (accountNames == null) {
            return;
        }

        if (this.isTransfer) {
            account.setText("Mittente");
            category.setText("Destinatario");

            // Setup source account listener
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

            // Populate initial lists
            accountComboBox.getItems().setAll(accountNames);
            if (accountComboBox.getValue() != null) {
                SecondoAccountComboBox.getItems().setAll(
                        accountNames.stream()
                                .filter(acc -> !acc.equals(accountComboBox.getValue()))
                                .toList()
                );
            }
        } else {
            accountComboBox.getItems().setAll(accountNames);
            account.setText("Conto");
            category.setText("Categoria");
        }
    }

    private boolean validateFields() {
        AtomicBoolean hasError = new AtomicBoolean(false);

        // Validazione account principale quando richiesto
        if (accountComboBox.getValue() == null || accountComboBox.getValue().trim().isEmpty()) {
            FieldAnimationUtils.animateFieldError(accountComboBox);
            hasError.set(true);
        }

        // Validazione balance
        if (balanceEditor.getText() == null || balanceEditor.getText().trim().isEmpty()) {
            FieldAnimationUtils.animateFieldError(balanceEditor);
            hasError.set(true);
        }

        // Validazione data
        if (datePicker.getValue() == null) {
            FieldAnimationUtils.animateFieldError(datePicker);
            hasError.set(true);
        }

        // Validazione in base al tipo di transazione
        if (isTransfer) {
            // Per i trasferimenti valida il secondo account
            if (SecondoAccountComboBox.getValue() == null || SecondoAccountComboBox.getValue().trim().isEmpty()) {
                FieldAnimationUtils.animateFieldError(SecondoAccountComboBox);
                hasError.set(true);
            }
        } else {
            // Per income/expense valida le categorie
            if (categorySelector.getSelectedCategory() == null || categorySelector.getSelectedSubCategory() == null) {
                categorySelector.animateError();
                hasError.set(true);
            }
        }

        if (hasError.get()) {
            showError("Inserisci tutti i campi!");
        }

        return !hasError.get();
    }

    private void showError(String message) {
        errorLabel.setOpacity(1);
        errorLabel.textProperty().bind(Data.lsp.lsb(message));
        errorLabel.setTextFill(rgb(255,0,0));
        Timeline fadeInTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> errorLabel.setOpacity(0)),
                new KeyFrame(Duration.seconds(0.2), e -> errorLabel.setOpacity(1))
        );
        fadeInTimeline.setCycleCount(1);
        fadeInTimeline.play();
    }

    private void clearError() {
        FieldAnimationUtils.removeErrorStyles(accountComboBox);
        FieldAnimationUtils.removeErrorStyles(SecondoAccountComboBox);
        FieldAnimationUtils.removeErrorStyles(balanceEditor);
        FieldAnimationUtils.removeErrorStyles(datePicker);
        categorySelector.removeError();
        errorLabel.setOpacity(0);
    }


    private void resetScreen() {
        //crea una variabile per la data attuale
        LocalDate currentDate = datePicker.getValue();
        //se la data attuale non è nulla allora la data del datepicker sarà la data inserita, altrimenti sarà la data attuale
        datePicker.setValue(currentDate != null ? currentDate : LocalDate.now());

        accountComboBox.getItems().clear();
        SecondoAccountComboBox.getItems().clear();
        accountComboBox.setOnAction(null);
        SecondoAccountComboBox.setOnAction(null);
        account.setText("");
        category.setText("");
        balanceEditor.reset();
        notes.setText("");
        categorySelector.clear();
        tagSelector.clearTags();
        TagFilter.refreshTags();


        /*
        // pulisce il date picker
        try {
            // prima prende il textfield del datepicker
            TextField dateEditor = datePicker.getEditor();
            // pulisce il testo
            dateEditor.setText("");
            // poi pulisce il valore
            datePicker.setValue(null);
        } catch (Exception e) {
            // se c'e' un errore pulisce solo il valore
            datePicker.setValue(null);
        }

        */


        System.out.println("Schermata resettata.");
    }



    private void onToggleButtonChange(boolean isTransfer) {
        // con questo if la schermata solo se cambio il tipo di transazione
        if (this.isTransfer != isTransfer) {
            resetScreen();
            this.isTransfer = isTransfer;
        }

        if (isTransfer) {
            // Modalità Trasferimento
            categorySelector.setVisible(false); // Nasconde il selettore di categoria
            categorySelector.setManaged(false); // Rimuove lo spazio occupato

            SecondoAccountComboBox.setVisible(true); // Mostra il secondo account
            SecondoAccountComboBox.setManaged(true); // Garantisce la gestione dello spazio

            // Configura le etichette
            account.setText("Mittente");
            category.setText("Destinatario");
        } else {
            // Modalità Income/Expense
            SecondoAccountComboBox.setVisible(false); // Nasconde il secondo account
            SecondoAccountComboBox.setManaged(false); // Rimuove lo spazio occupato

            categorySelector.setVisible(true); // Mostra il selettore di categoria
            categorySelector.setManaged(true); // Garantisce la gestione dello spazio

            // Configura le etichette
            account.setText("Conto");
            category.setText("Categoria");
        }

        // Popola le combo box in base alla modalità corrente
        populateComboBoxes(isTransfer);
    }


    private void hide() {
        clearError();
        contextMenu.hide();
    }

    public void show(double x, double y) {
        incomeButton.setSelected(true);
        onToggleButtonChange(false);
        tagSelector.clearTags();
        categorySelector.populateMainCategoriesForIncome();
        datePicker.setValue(LocalDate.now());

        contextMenu.show(node, x, y);
    }

    public void toggle(double x, double y) {
        if (contextMenu.isShowing()) {
            hide();
        } else {
            show(x, y);
        }
    }





}
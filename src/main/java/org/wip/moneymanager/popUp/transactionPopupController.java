package org.wip.moneymanager.popUp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import org.wip.moneymanager.components.CategorySelector;
import org.wip.moneymanager.components.TagSelector;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.components.BalanceEditor;
import org.wip.moneymanager.model.UserDatabase;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class transactionPopupController extends BorderPane {
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
    private ChoiceBox<String> categoryChoiceBox;
    @FXML
    private Label account;
    @FXML
    private ChoiceBox<String> accountChoiceBox;
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

    public transactionPopupController(Window window) throws IOException {
        Data.esm.register(executorService);
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
        cancelButton.setOnAction(e -> hide());

        ToggleGroup toggleGroup = new ToggleGroup();
        incomeButton.setToggleGroup(toggleGroup);
        expenseButton.setToggleGroup(toggleGroup);
        transferButton.setToggleGroup(toggleGroup);

        //imposta l'incomeButton come default
        incomeButton.setSelected(true);

        //impedisce che il toggle venga deselezionato
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                toggleGroup.selectToggle(oldValue);
            }
        });

        incomeButton.setOnAction(e -> {
            System.out.println("Income button clicked");
            onToggleButtonChange(false);
        });

        expenseButton.setOnAction(e -> {
            System.out.println("Expense button clicked");
            onToggleButtonChange(false);
        });

        transferButton.setOnAction(e -> {
            System.out.println("Transfer button clicked");
            onToggleButtonChange(true);
        });

        // configurazione iniziale per conto e categoria
        onToggleButtonChange(false);
    }

    private void populateChoiceBoxes(boolean isTransfer) {
        UserDatabase userDatabase = UserDatabase.getInstance();

        // Esegui le query solo se i risultati non sono già stati memorizzati
        if (accountNames == null || categoryNames == null) {
            System.out.println("Esecuzione delle query per ottenere i nomi degli account e delle categorie.");
            userDatabase.getAllAccountNames().run();
            userDatabase.getAllCategoryNames().run();

            try {
                accountNames = userDatabase.getAllAccountNames().get();
                categoryNames = userDatabase.getAllCategoryNames().get();

                // Log per verificare che i dati siano stati recuperati
                System.out.println("Nomi degli account recuperati: " + accountNames);
                System.out.println("Nomi delle categorie recuperati: " + categoryNames);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isTransfer) {
            // pulisce gli elementi attuali e i listener
            accountChoiceBox.getItems().clear();
            categoryChoiceBox.getItems().clear();
            accountChoiceBox.setOnAction(null);
            categoryChoiceBox.setOnAction(null);

            // imposto i nuovi elementi
            accountChoiceBox.getItems().setAll(accountNames);
            categoryChoiceBox.getItems().setAll(accountNames);
            account.setText("Mittente");
            category.setText("Destinatario");

            // Log per verificare il popolamento delle ChoiceBox
            System.out.println("ChoiceBox popolati per il trasferimento.");

            // qui gestisco la logica per gli account, se cambio uno devo aggiornare l'altro e viceversa
            accountChoiceBox.setOnAction(e -> {
                String selectedAccount = accountChoiceBox.getValue();
                if (selectedAccount != null) {
                    List<String> availableAccounts = accountNames.stream()
                            .filter(acc -> !acc.equals(selectedAccount))
                            .toList();
                    String currentDestination = categoryChoiceBox.getValue();
                    categoryChoiceBox.getItems().setAll(availableAccounts);
                    if (currentDestination != null && availableAccounts.contains(currentDestination)) {
                        categoryChoiceBox.setValue(currentDestination);
                    }
                }
            });

            categoryChoiceBox.setOnAction(e -> {
                String selectedDestination = categoryChoiceBox.getValue();
                if (selectedDestination != null) {
                    List<String> availableAccounts = accountNames.stream()
                            .filter(acc -> !acc.equals(selectedDestination))
                            .toList();
                    String currentSource = accountChoiceBox.getValue();
                    accountChoiceBox.getItems().setAll(availableAccounts);
                    if (currentSource != null && availableAccounts.contains(currentSource)) {
                        accountChoiceBox.setValue(currentSource);
                    }
                }
            });
        } else {
            accountChoiceBox.getItems().setAll(accountNames);
            categoryChoiceBox.getItems().setAll(categoryNames);
            account.setText("Conto");
            category.setText("Categoria");

            // Log per verificare il popolamento delle ChoiceBox
            System.out.println("ChoiceBox popolati per conto e categoria.");
        }
    }

    private void resetScreen() {
        accountChoiceBox.getItems().clear();
        categoryChoiceBox.getItems().clear();
        accountChoiceBox.setOnAction(null);
        categoryChoiceBox.setOnAction(null);
        account.setText("");
        category.setText("");
        datePicker.setValue(null);
        balanceEditor.reset();
        notes.setText("");
        //aggiungere tag

        System.out.println("Schermata resettata.");
    }

    private boolean isAnyFieldFilled() {
        return datePicker.getValue() != null ||
                !balanceEditor.getText().isEmpty() ||
                !categoryChoiceBox.getSelectionModel().isEmpty() ||
                !accountChoiceBox.getSelectionModel().isEmpty();
        //aggiungere controllo tag
    }

    private void onToggleButtonChange(boolean isTransfer) {
        if(isAnyFieldFilled()){
            resetScreen();
        }
        populateChoiceBoxes(isTransfer);
    }

    private void hide() {
        contextMenu.hide();
    }

    public void show(double x, double y) {
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
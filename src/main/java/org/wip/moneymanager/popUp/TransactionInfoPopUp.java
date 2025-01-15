package org.wip.moneymanager.popUp;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Duration;
import org.wip.moneymanager.components.*;
import org.wip.moneymanager.model.DBObjects.dbTag;
import org.wip.moneymanager.model.DBObjects.dbTransaction;
import org.wip.moneymanager.model.Data;


import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionInfoPopUp extends BorderPane {
    @FXML
    private BorderPane BoderPanePopup;

    @FXML
    private Label account;

    @FXML
    private VBox BoxButtonRight;

    @FXML
    private Button buttonExit;

    @FXML
    private ComboBox<String> accountsComboBox;

    @FXML
    private Label amountLabel;

    @FXML
    private BalanceEditor balanceCounter;

    @FXML
    private Label category;

    @FXML
    private CategorySelector categorySelectorTwo;

    @FXML
    private Label date;

    @FXML
    private DatePicker datesPicker;

    @FXML
    private Button deleteButton;

    @FXML
    private Button discardButton;

    @FXML
    private Button editButton;

    @FXML
    private Button expenseButton;

    @FXML
    private Button incomeButton;

    @FXML
    private Label labelTransaction;

    @FXML
    private TextArea notesAgg;

    @FXML
    private Button saveEditButton;

    @FXML
    private ComboBox<String> secondAccountComboBox;

    @FXML
    private TagSelector tagPane;

    @FXML
    private Label tagsLabel;

    @FXML
    private Button transferButton;

    private List<String> accountNames;
    private final CustomMenuItem customMenuItem;
    private final ContextMenu contextMenu = new ContextMenu();
    private final Window node;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Runnable onCloseCallback;
    private final SingleTransactionController controller;
    private dbTransaction myTransaction;
    private final List<dbTag> listaTag;

    public TransactionInfoPopUp(Window window, SingleTransactionController fatherTransactions, List<dbTag> listaTag) throws IOException {
        this.node = window;
        this.controller = fatherTransactions;
        this.listaTag = listaTag;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/popUp/infoTransaction.fxml"));
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
    private void initialize() throws SQLException {
        Data.esm.register(executorService);
        buttonExit.setOnAction(event -> close());
        myTransaction = controller.getTransaction();
        setupEditButton();
    }

    private void initializeNoEditable()  {
        datesPicker.setValue(LocalDateTime.ofInstant(Instant.ofEpochSecond(myTransaction.date()), ZoneId.systemDefault()).toLocalDate());
        balanceCounter.setBalance(myTransaction.amount());
        balanceCounter.reset();
        notesAgg.setText(myTransaction.note());
        setFieldsEditable(false);
        saveEditButton.setManaged(false);
        discardButton.setManaged(false);
        deleteButton.setManaged(true);
        editButton.setManaged(true);

        Tooltip deleteCardTooltip = new Tooltip("Doppio clic per eliminare");
        deleteCardTooltip.setShowDelay(Duration.millis(1));
        deleteCardTooltip.setHideDelay(Duration.millis(0));
        deleteButton.setOnMouseEntered(event -> Tooltip.install(deleteButton, deleteCardTooltip));
        deleteButton.setOnMouseExited(event -> Tooltip.uninstall(deleteButton, deleteCardTooltip));

        deleteButton.setOnAction(event ->{
            controller.removeCard(myTransaction.id());
        });

        if(tagPane.get_selected_tags().isEmpty()){
            popolaTags();
        }

        // Inizializza accountNames se è null
        if (accountNames == null) {
            accountNames = new ArrayList<>(); // Imposta una lista vuota se ancora non inizializzata
        }

        // Assicurati che il task venga avviato per caricare i dati degli account
        Task<List<String>> namesAccountsTask = Data.userDatabase.getAllAccountNames();
        namesAccountsTask.setOnSucceeded(event -> {
            accountNames = namesAccountsTask.getValue(); // Salva i risultati nella variabile membro
            if (accountNames != null) {
                accountsComboBox.getItems().setAll(accountNames); // Popola accountComboBox
            }
        });

        try {
            accountsComboBox.getSelectionModel().select(Data.userDatabase.getAccountNameById(myTransaction.account()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Gestione per il tipo di transazione (spesa, entrata, trasferimento)
        if (myTransaction.type() == 0) {
            expenseButton.setDisable(true);
            transferButton.setDisable(true);
            incomeButton.setStyle("-fx-border-color: green;" + "-fx-border-radius: 6");
            secondAccountComboBox.setVisible(false);
            secondAccountComboBox.setManaged(false);
            category.setText("Category");
            categorySelectorTwo.populateMainCategoriesForIncome();
            try {
                categorySelectorTwo.setCategory_box(myTransaction.category());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        } else if (myTransaction.type() == 1) {

            incomeButton.setDisable(true);
            transferButton.setDisable(true);
            expenseButton.setStyle("-fx-border-color: red;" + "-fx-border-radius: 6");
            secondAccountComboBox.setVisible(false);
            secondAccountComboBox.setManaged(false);
            category.setText("Category");
            categorySelectorTwo.populateMainCategoriesForExpense();
            try {
                categorySelectorTwo.setCategory_box(myTransaction.category());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        } else {
            expenseButton.setDisable(true);
            incomeButton.setDisable(true);
            transferButton.setStyle("-fx-border-color: white;" + "-fx-border-radius: 6");
            categorySelectorTwo.setVisible(false);
            categorySelectorTwo.setManaged(false);
            category.setText("Second Account");
            editButton.setDisable(true);
            editButton.setOpacity(0.2);

            try {
                secondAccountComboBox.getSelectionModel().select(Data.userDatabase.getAccountNameById(myTransaction.second_account()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // Popola inizialmente le comboBox degli account
            accountsComboBox.getItems().setAll(accountNames);
            accountsComboBox.setOnAction(e -> {
                String selected = accountsComboBox.getValue();
                if (selected != null) {
                    secondAccountComboBox.getItems().setAll(
                            accountNames.stream()
                                    .filter(acc -> !acc.equals(selected))
                                    .toList()
                    );
                }
            });

            // Prevenire l'uso di account selezionato più di una volta
            if (accountsComboBox.getValue() != null) {
                secondAccountComboBox.getItems().setAll(
                        accountNames.stream()
                                .filter(acc -> !acc.equals(accountsComboBox.getValue()))
                                .toList()
                );
            }

            // Aggiungi un tooltip
            Tooltip tooltip = new Tooltip("Non puoi modificare un trasferimento");
            tooltip.setShowDelay(new Duration(1));
            tooltip.setHideDelay(new Duration(0));
            Tooltip.install(BoxButtonRight, tooltip);
        }


    }

    private void popolaTags() {
        if (listaTag != null) {
            listaTag.forEach(dbTagItem -> {
                Tag tag = new Tag(dbTagItem.name(), 1, 1, dbTagItem.color());;
                tagPane.addTag(tag);
            });
        }
    }

    private void hide() {
        contextMenu.hide();
    }

    public void toggle(double x, double y) {
        if (contextMenu.isShowing()) {
            hide();
        } else {
            show(x, y);
        }
    }

    private void setFieldsEditable(boolean editable) {
        // Disabilita o abilita i campi per la modifica
        accountsComboBox.setDisable(!editable);
        datesPicker.setDisable(!editable);
        notesAgg.setDisable(!editable);
        categorySelectorTwo.setDisable(!editable);
        balanceCounter.setDisable(!editable);
        secondAccountComboBox.setDisable(!editable);
        tagPane.setDisable(!editable);

        // Mostra i pulsanti corretti
        saveEditButton.setVisible(editable);
        discardButton.setVisible(editable);
        editButton.setVisible(!editable);
        deleteButton.setVisible(!editable);
    }

    public void show(double x, double y)  {
        contextMenu.show(node, x, y);
        initializeNoEditable();

    }

    public void close() {
        if (onCloseCallback != null) {
            onCloseCallback.run();
        }
        hide();
    }

    private void setupEditButton() {
        editButton.setOnAction(event -> {
            setFieldsEditable(true);
            editButton.setVisible(false);
            deleteButton.setVisible(false);
            editButton.setManaged(false);
            deleteButton.setManaged(false);

            saveEditButton.setVisible(true);
            discardButton.setVisible(true);
            saveEditButton.setManaged(true);
            discardButton.setManaged(true);
        });

        saveEditButton.setOnAction(event -> {
            saveChanges();
            hide();

        });

        discardButton.setOnAction(event -> {
            // Annulla le modifiche e ripristina i valori originali
            discardChanges();
        });
    }

    private void discardChanges() {
        initializeNoEditable();
    }

    private void saveChanges() {
            // Memorizza i valori originali
            int originalDate = myTransaction.date();
            int originalAccountId = myTransaction.account();
            double originalAmount = myTransaction.amount();
            int originalCategory = myTransaction.category();
            int originalSecondAccount = myTransaction.second_account();

            // 1. Salva solo se la data è cambiata
            int creationDate = (int) datesPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
            if (creationDate != originalDate) {
                try {
                    myTransaction.setDate(creationDate);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            // 2. Salva solo se l'account è cambiato
            try {
                int accountId = Data.userDatabase.getAccountIdByName(accountsComboBox.getValue());
                if (accountId != originalAccountId) {
                    myTransaction.setAccount(accountId);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // 3. Salva solo se l'importo è cambiato
            double balance = balanceCounter.getBalance();
            if (balance != originalAmount) {
                try {
                    myTransaction.setAmount(balance);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            // 4. Salva solo se la categoria è cambiata
            if (myTransaction.type() != 2) {
                int selectedCategoryId;
                if (categorySelectorTwo.getSelectedSubCategory() == null) {
                    try {
                        selectedCategoryId = Data.userDatabase.getCategoryIdByName(categorySelectorTwo.getSelectedCategory());
                        if (selectedCategoryId != originalCategory) {
                            myTransaction.setCategory(selectedCategoryId);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        selectedCategoryId = Data.userDatabase.getCategoryIdByName(categorySelectorTwo.getSelectedSubCategory());
                        if (selectedCategoryId != originalCategory) {
                            myTransaction.setCategory(selectedCategoryId);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            // 5. Salva solo se il secondo account è cambiato (per i trasferimenti)
            if (myTransaction.type() == 2) {
                try {
                    int accountSecond = Data.userDatabase.getAccountIdByName(secondAccountComboBox.getValue());
                    if (accountSecond != originalSecondAccount) {
                        myTransaction.setSecondAccount(accountSecond);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            // 6. Gestione dei tag (salva solo se i tag sono cambiati)
            List<Tag> selectedTags = tagPane.get_selected_tags();
            List<String> currentTagNames = new ArrayList<>();
            for (Tag tag : selectedTags) {
                currentTagNames.add(tag.getTag());
            }

            List<Integer> previousTagIds = new ArrayList<>();
            for (dbTag tag : listaTag) {
                previousTagIds.add(tag.id());
            }

            // Confronta le liste dei tag correnti con quelli precedenti
            Task<Boolean> updateTask = Data.userDatabase.updateTransactionTags(myTransaction.id(), currentTagNames, previousTagIds);
            updateTask.setOnSucceeded(event -> {
                controller.refreshSingleTransaction();
            });
            executorService.submit(updateTask);
        }



        public ContextMenu getContextMenu() {
        return contextMenu;
    }
}

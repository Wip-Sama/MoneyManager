package org.wip.moneymanager.popUp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;
import org.wip.moneymanager.components.CategorySelector;
import org.wip.moneymanager.components.TagSelector;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.UserDatabase;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class popUpFilterController extends AnchorPane {
    @FXML
    private ComboBox<String> accountCombo;

    @FXML
    private Label accountFilter;

    @FXML
    private Button buttonFilter;

    @FXML
    private Button cancelPopUp;

    @FXML
    private CategorySelector categoryCombo;

    @FXML
    private Label categoryFilter;

    @FXML
    private Label notifyError;

    @FXML
    private AnchorPane popUpFilter;

    @FXML
    private TagSelector tagCombo;

    @FXML
    private Label tagFilter;

    @FXML
    private Label title;

    @FXML
    void handleFilterAction(ActionEvent event) {
        // Implementazione della logica di filtraggio
    }

    private final CustomMenuItem customMenuItem;
    private final ContextMenu contextMenu = new ContextMenu();

    private String selectedCategory;
    private String selectedSubCategory;
    private String selectedAccount;
    private List<String> accountNames;

    private final Window ownerWindow;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public popUpFilterController(Window window) throws IOException {
        this.ownerWindow = window;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/popUp/popUpFilter.fxml"));
        fxmlLoader.setController(this);
        Parent loaded = fxmlLoader.load();

        customMenuItem = new CustomMenuItem(loaded);
        customMenuItem.getStyleClass().add("tag-filter-menu-item");
        customMenuItem.hideOnClickProperty().set(false);
        contextMenu.getItems().add(customMenuItem);

        window.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, _ -> hide());

        // Listener per il reset quando il popup viene chiuso
        contextMenu.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Quando il popup si chiude
                resetFilters(); // Resetta i filtri
            }
        });
    }

    @FXML
    private void initialize() {
        Data.esm.register(executorService);
        title.textProperty().bind(Data.lsp.lsb("popUpFilterController.title"));
        accountFilter.textProperty().bind(Data.lsp.lsb("popUpFilterController.accountFilter"));
        categoryFilter.textProperty().bind(Data.lsp.lsb("popUpFilterController.categoryFilter"));
        tagFilter.textProperty().bind(Data.lsp.lsb("popUpFilterController.tagFilter"));
        buttonFilter.textProperty().bind(Data.lsp.lsb("popUpFilterController.buttonFilter"));
        cancelPopUp.textProperty().bind(Data.lsp.lsb("popUpFilterController.cancelPopUp"));
        notifyError.textProperty().bind(Data.lsp.lsb("popUpFilterController.notifyError"));

        categoryCombo.populateMainCategoriesType();
        initializeAccountNames();
        populateAccountCombo();
        buttonFilter.setOnAction(event -> {handleFilterAction();});
        cancelPopUp.setOnAction(e -> hide());
        notifyError.setOpacity(0);
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

    public void show(double x, double y) {
        resetFilters();
        tagCombo.clearTags(); // Pulisce i tag quando il menu viene mostrato
        contextMenu.show(ownerWindow, x, y);
    }

    private void initializeAccountNames() {
        try {
            UserDatabase userDatabase = UserDatabase.getInstance();
            accountNames = userDatabase.getAllAccountNames().get();
            if (accountNames == null || accountNames.isEmpty()) {
                accountNames = List.of();
                System.err.println("Nessun account trovato nel DB");
            }
        } catch (Exception e) {
            accountNames = List.of(); // Lista vuota in caso di errore
            System.err.println("Errore di caricamento dei nomi: " + e.getMessage());
        }
    }

    private void populateAccountCombo() {
        if (accountNames == null || accountCombo == null) {
            System.err.println("Impossibile popolare accountCombo: accountNames o accountCombo è null");
            return;
        }

        accountCombo.getItems().setAll(accountNames);
        accountCombo.setValue(null); // Non selezionare alcun valore di default
    }

    @FXML
    private void handleFilterAction() {
        selectedCategory = categoryCombo.getSelectedCategory();
        selectedSubCategory = categoryCombo.getSelectedSubCategory();
        selectedAccount = accountCombo.getValue();

        // Logica per visualizzare l'errore se nessun filtro è selezionato
        if (selectedCategory == null && selectedSubCategory == null && selectedAccount == null) {
            // Nessun filtro selezionato, mostra l'errore
            notifyError.setOpacity(1); // Mostra il messaggio di errore
            notifyError.setVisible(true); // Assicurati che la label sia visibile
        } else {
            // Almeno un filtro è selezionato, continua con la logica
            System.out.println("Categoria selezionata: " + selectedCategory);
            System.out.println("Sottocategoria selezionata: " + selectedSubCategory);
            System.out.println("Account selezionato: " + selectedAccount);

            // Nascondi l'errore, se visibile
            notifyError.setOpacity(0); // Nasconde il messaggio di errore
            notifyError.setVisible(false); // Nasconde la label
            hide(); // Chiudi il popup se i filtri sono validi
        }
    }


    public List<String> getSelectedFilters() {
        List<String> filters = new ArrayList<>();
        if (selectedCategory != null) filters.add(selectedCategory);
        if (selectedSubCategory != null) filters.add(selectedSubCategory);
        if (selectedAccount != null) filters.add(selectedAccount);
        return filters;
    }

    public void resetFilters() {
        // Reset delle categorie
        if (categoryCombo != null) {
            System.out.println("Resetting categoryCombo");
            categoryCombo.clear();
            categoryCombo.populateMainCategoriesType();
        }


        if (accountCombo != null) {
            System.out.println("Resetting accountCombo");
            accountCombo.setValue(null);
            selectedAccount = null;
        }

        // Reset degli errori
        notifyError.setOpacity(0);

        // Aggiornamento della selezione effettiva
        selectedCategory = categoryCombo.getSelectedCategory();
        selectedSubCategory = categoryCombo.getSelectedSubCategory();
    }

}

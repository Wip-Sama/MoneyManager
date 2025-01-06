package org.wip.moneymanager.popUp;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;
import org.wip.moneymanager.components.CategorySelector;
import org.wip.moneymanager.components.Tag;
import org.wip.moneymanager.components.TagSelector;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.UserDatabase;
import org.wip.moneymanager.pages.Transactions;

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
    private final Transactions transactions;
    public popUpFilterController(Window window, Transactions transactions) throws IOException {
        this.ownerWindow = window;
        this.transactions = transactions;
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
        Task<List<String>> takeAccountsTask = Data.userDatabase.getAllAccountNames();
        takeAccountsTask.setOnSucceeded(e -> {
            List<String> accountNames = takeAccountsTask.getValue();
            if (accountNames.size() > 0) {
                accountCombo.getItems().setAll(accountNames);
                accountCombo.setValue(null); //
            }
        });
    }

    @FXML
    private void handleFilterAction() {
        if (categoryCombo.getSelectedSubCategory() != null) {
            selectedCategory = categoryCombo.getSelectedSubCategory();
        }
        else{
            selectedCategory = categoryCombo.getSelectedCategory();
        }

        selectedAccount = accountCombo.getValue();
        List<Tag> selectedTags = tagCombo.get_selected_tags();


        //visualizzare l'errore se nessun filtro è selezionato
        if (selectedCategory == null && selectedAccount == null && selectedTags.isEmpty()) {
            notifyError.setOpacity(1); // Mostra il messaggio di errore
        } else {
            if (!selectedTags.isEmpty()) {
                List<String> filters = new ArrayList<>();
                for (Tag t : selectedTags){
                    filters.add(t.getTag());
                    System.out.println(t.getTag());
                }
            transactions.applyFilters(selectedCategory, selectedAccount, filters);
            } else {
                transactions.applyFilters(selectedCategory, selectedAccount, null);
            }
            notifyError.setOpacity(0); // Nasconde il messaggio di errore
            hide(); // Chiudi il popup se i filtri sono validi
        }
    }

    public void resetFilters() {
        // Reset delle categorie
        if (categoryCombo != null) {
            categoryCombo.clear();
            categoryCombo.populateMainCategoriesType();
        }

        if (accountCombo != null) {
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

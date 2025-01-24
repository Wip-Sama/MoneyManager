package org.wip.moneymanager.popUp;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;
import org.wip.moneymanager.components.CategorySelector;
import org.wip.moneymanager.components.Tag;
import org.wip.moneymanager.components.TagSelector;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.pages.Transactions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PopUpFilterController extends AnchorPane {

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

    private final CustomMenuItem customMenuItem;
    private final ContextMenu contextMenu = new ContextMenu();

    private String selectedCategory;
    private String selectedSubCategory;
    private String selectedAccount;
    private final Window ownerWindow;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Transactions transactions;

    public PopUpFilterController(Window window, Transactions transactions) throws IOException {
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

        contextMenu.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                resetFilters();
            }
        });
    }

    @FXML
    private void initialize() {
        Data.esm.register(executorService);
        bindUIComponents();
        categoryCombo.populateMainCategoriesType();
        initializeAccountNames();
        configureButtons();
        notifyError.setOpacity(0);
    }

    private void bindUIComponents() {
        title.textProperty().bind(Data.lsp.lsb("popUpFilterController.title"));
        accountFilter.textProperty().bind(Data.lsp.lsb("popUpFilterController.accountFilter"));
        categoryFilter.textProperty().bind(Data.lsp.lsb("popUpFilterController.categoryFilter"));
        tagFilter.textProperty().bind(Data.lsp.lsb("popUpFilterController.tagFilter"));
        buttonFilter.textProperty().bind(Data.lsp.lsb("popUpFilterController.buttonFilter"));
        cancelPopUp.textProperty().bind(Data.lsp.lsb("popUpFilterController.cancelPopUp"));
        notifyError.textProperty().bind(Data.lsp.lsb("popUpFilterController.notifyError"));
    }

    private void configureButtons() {
        buttonFilter.setOnAction(event -> handleFilterAction());
        cancelPopUp.setOnAction(event -> hide());
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
        tagCombo.clearTags();
        contextMenu.show(ownerWindow, x, y);
    }

    private void initializeAccountNames() {
        Task<List<String>> takeAccountsTask = Data.userDatabase.getAllAccountNames();
        takeAccountsTask.setOnSucceeded(event -> {
            List<String> accountNames = takeAccountsTask.getValue();
            if (!accountNames.isEmpty()) {
                accountCombo.getItems().setAll(accountNames);
                accountCombo.setValue(null);
            }
        });
        executorService.submit(takeAccountsTask);
    }

    @FXML
    private void handleFilterAction() {
        selectedCategory = categoryCombo.getSelectedSubCategory() != null
                ? categoryCombo.getSelectedSubCategory()
                : categoryCombo.getSelectedCategory();

        selectedAccount = accountCombo.getValue();
        List<Tag> selectedTags = tagCombo.get_selected_tags();

        if (selectedCategory == null && selectedAccount == null && selectedTags.isEmpty()) {
            notifyError.setOpacity(1);
        } else {
            applyFilters(selectedTags);
            notifyError.setOpacity(0);
            hide();
        }
    }

    private void applyFilters(List<Tag> selectedTags) {
        List<String> filters = new ArrayList<>();
        if (!selectedTags.isEmpty()) {
            selectedTags.forEach(tag -> filters.add(tag.getTag()));
        }
        transactions.generaCard(selectedCategory, selectedAccount, filters.isEmpty() ? null : filters);
    }

    public void resetFilters() {
        if (categoryCombo != null) {
            categoryCombo.clear();
            categoryCombo.populateMainCategoriesType();
        }

        if (accountCombo != null) {
            accountCombo.setValue(null);
            selectedAccount = null;
        }

        notifyError.setOpacity(0);
        selectedCategory = null;
        selectedSubCategory = null;
    }
}

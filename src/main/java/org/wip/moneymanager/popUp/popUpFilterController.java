package org.wip.moneymanager.popUp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
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
    private ComboBox<?> accountCombo;

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

    }

    private final CustomMenuItem customMenuItem;
    private final ContextMenu contextMenu = new ContextMenu();



    private String selectedCategory;
    private String selectedSubCategory;

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
        buttonFilter.setOnAction(event -> {handleFilterAction();});




        // Pulsante per chiudere il popup
        cancelPopUp.setOnAction(e -> hide());



        notifyError.setOpacity(0); // Nascondi il messaggio di errore all'inizio
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
        contextMenu.show(ownerWindow, x, y);
    }






    @FXML
    private void handleFilterAction() {
        // Recupera i valori selezionati

        selectedCategory = categoryCombo.getSelectedCategory();
        selectedSubCategory = categoryCombo.getSelectedSubCategory();


        System.out.println("Categoria selezionata: " + selectedCategory);
        System.out.println("Sottocategoria selezionata: " + selectedSubCategory);

        // Logica successiva: Puoi gestire cosa fare con i valori salvati
        if (selectedCategory != null || selectedSubCategory != null) {
            hide(); // Chiudi il popup
        } else {
            notifyError.setOpacity(1); // Mostra il messaggio di errore
        }
    }

    public List<String> getSelectedFilters() {
        List<String> filters = new ArrayList<>();
        if (selectedCategory != null) filters.add(selectedCategory);
        if (selectedSubCategory != null) filters.add(selectedSubCategory);
        return filters;
    }




}

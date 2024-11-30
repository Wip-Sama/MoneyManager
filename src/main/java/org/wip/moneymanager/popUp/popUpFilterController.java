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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class popUpFilterController extends AnchorPane {
    @FXML
    private ComboBox<String> accountChoice;

    @FXML
    private Label accountFilter;

    @FXML
    private Button buttonFilter;

    @FXML
    private Button cancelPopUp;

    @FXML
    private CategorySelector categoryChoice;

    @FXML
    private Label categoryFilter;

    @FXML
    private Label notifyError;

    @FXML
    private AnchorPane popUpFilter;

    @FXML
    private TagSelector tagChoice;

    @FXML
    private Label tagFilter;

    @FXML
    private Label title;

    private final CustomMenuItem customMenuItem;
    private final ContextMenu contextMenu = new ContextMenu();


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

        populateComboBoxes();
        // Pulsante per chiudere il popup
        cancelPopUp.setOnAction(e -> hide());



        notifyError.setOpacity(0); // Nascondi il messaggio di errore all'inizio
    }

    // Metodo che popola le ComboBox con i dati degli account e delle categorie
    @FXML
    private void populateComboBoxes() {
        UserDatabase userDatabase = UserDatabase.getInstance();

        // Carica i nomi delle categorie
        userDatabase.getAllCategoryNames().run();
        userDatabase.getAllAccountNames().run();

        try {
            // Recupera i nomi delle categorie
            List<String> categoryNames = userDatabase.getAllCategoryNames().get();
            if (categoryNames.isEmpty()) {
                System.err.println("La lista dei nomi delle categorie è vuota!");
            } else {
                System.out.println("Categorie trovate: " + categoryNames);
            }

            // Recupera i nomi dei conti
            List<String> accountNames = userDatabase.getAllAccountNames().get();
            if (accountNames.isEmpty()) {
                System.err.println("La lista dei nomi degli account è vuota!");
            } else {
                System.out.println("Account trovati: " + accountNames);
            }


            accountChoice.getItems().setAll(accountNames);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore durante il popolamento delle ComboBox: " + e.getMessage());
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

    public void show(double x, double y) {
        contextMenu.show(ownerWindow, x, y);
    }
}

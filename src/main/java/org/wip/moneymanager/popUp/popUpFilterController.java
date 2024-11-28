package org.wip.moneymanager.popUp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.wip.moneymanager.components.TagSelector;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.UserDatabase;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class popUpFilterController extends AnchorPane {
    @FXML
    private ChoiceBox<String> accountChoice;

    @FXML
    private Label accountFilter;

    @FXML
    private Button buttonFilter;

    @FXML
    private Button cancelPopUp;

    @FXML
    private ChoiceBox<String> categoryChoice;

    @FXML
    private Label categoryFilter;

    @FXML
    private Label notifyError;

    @FXML
    private AnchorPane popUpFilter;

    @FXML
    private Label tagFilter;

    @FXML
    private TagSelector tagFilterResearch;

    @FXML
    private Label title;

    private double xOffset = 0;
    private double yOffset = 0;
    private final Popup popup = new Popup();
    private final Window ownerWindow;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    public popUpFilterController(Window window) throws IOException {
        Data.esm.register(executorService);
        this.ownerWindow = window;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/popUp/popUpFilter.fxml"));
        fxmlLoader.setController(this);
        Parent loaded = fxmlLoader.load();

        Scene popupScene = new Scene(loaded);
        popupScene.getRoot().setStyle("-fu-accent: " + Data.dbUser.accentProperty().get().getHex() + ";");

        popup.getContent().add(loaded);
        window.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, _ -> hide());
    }

    @FXML
    private void initialize() {
        // Pulsante per chiudere il popup
        cancelPopUp.setOnAction(e -> hide());

        // Drag del popup
        this.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        this.setOnMouseDragged(event -> {
            popup.setX(event.getScreenX() - xOffset);
            popup.setY(event.getScreenY() - yOffset);
        });

        notifyError.setOpacity(0); // Nascondi il messaggio di errore all'inizio
    }

    // Metodo che popola le ChoiceBox con i dati degli account e delle categorie
    @FXML
    private void populateChoiceBoxes() {
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

            // Popola le ChoiceBox
            categoryChoice.getItems().setAll(categoryNames);
            accountChoice.getItems().setAll(accountNames);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore durante il popolamento delle ChoiceBox: " + e.getMessage());
        }
    }

    private void hide() {
        popup.hide();
    }

    public void show() {
        populateChoiceBoxes();
        popup.show(ownerWindow);
    }
}

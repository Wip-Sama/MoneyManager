package org.wip.moneymanager.components;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import org.wip.moneymanager.model.DBObjects.dbTag;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.popUp.AddNewTagController;
import org.wip.moneymanager.popUp.TagInfoPopUp;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TagHandler extends AnchorPane {

    @FXML
    private Button addNewTag;

    @FXML
    private TextField searchBar;

    @FXML
    private FlowPane tag_pane;

    @FXML
    private BorderPane infoPane;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<dbTag> allTags;
    private TagInfoPopUp addNewTagController;
    private static ContextMenu contextMenu = null;
    private TagInfoPopUp tagInfoPopUp;

    public TagHandler() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/taghandler.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    public void initialize() {
        Data.esm.register(executorService);
        searchBar.promptTextProperty().bind(Data.lsp.lsb("searchBar.searchTagPlaceholder"));

        // Task per recuperare tutti i tag
        Task<List<dbTag>> task = Data.userDatabase.getAllTag();
        task.setOnSucceeded(event -> {
            allTags = task.getValue();
            displayTags(allTags);  // Visualizza i tag
        });
        executorService.submit(task);

        searchBar.textProperty().addListener((observable, oldValue, newValue) -> filterTags(newValue));

        addNewTag.setOnAction(event -> openAddTagPopUp());  // Apre il popup per aggiungere un tag
    }

    private void openAddTagPopUp() {
        // Se il controller per l'aggiunta del tag non esiste, crealo
        if (addNewTagController == null) {
            addNewTagController = new TagInfoPopUp( this, TagInfoPopUp.Mode.ADD);
        }
        addNewTagController.setTag(null);  // Passa null al controller per l'aggiunta del tag
        addNewTagController.setMode(TagInfoPopUp.Mode.ADD);  // Imposta la modalità di aggiunta
        Bounds bounds = addNewTag.localToScreen(addNewTag.getBoundsInLocal());
        double x = bounds.getMaxX() - 300;
        double y = bounds.getMaxY() + 2;
        showContextMenu(addNewTagController, x, y);  // Mostra il popup di aggiunta tag
    }

    private void displayTags(List<dbTag> tags) {
        tag_pane.getChildren().clear();  // Pulisce i tag esistenti
        for (dbTag tag : tags) {
            Tag tagComponent = new Tag(tag);
            tag_pane.getChildren().add(tagComponent);
            tagComponent.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    openTagInfoPopUp(tag);  // Apre il popup per la modifica del tag
                }
            });
        }
    }

    private void filterTags(String filterText) {
        List<dbTag> filteredTags = allTags.stream()
                .filter(tag -> tag.name().toLowerCase().contains(filterText.toLowerCase()))
                .toList();
        displayTags(filteredTags);  // Visualizza i tag filtrati
    }

    public void refreshTags() {
        // Ricarica la lista dei tag dal database
        Task<List<dbTag>> task = Data.userDatabase.getAllTag();
        task.setOnSucceeded(event -> {
            allTags = task.getValue();
            displayTags(allTags);  // Mostra i tag aggiornati
        });
        executorService.submit(task);
    }

    private void openTagInfoPopUp(dbTag tag) {
        if (tag == null) return;  // Se il tag è null, non aprire il popup

        // Se il popup per la modifica non è già stato creato, crealo
        if (tagInfoPopUp == null) {
            tagInfoPopUp = new TagInfoPopUp(this, TagInfoPopUp.Mode.EDIT);
        }
        tagInfoPopUp.setMode(TagInfoPopUp.Mode.EDIT);  // Imposta la modalità di modifica
        tagInfoPopUp.setTag(tag);  // Passa il tag al popup per la modifica
        Bounds bounds = addNewTag.localToScreen(addNewTag.getBoundsInLocal());
        double x = bounds.getMaxX() - 330;
        double y = bounds.getMaxY() + 2;
        showContextMenu(tagInfoPopUp, x, y);  // Mostra il popup di modifica tag
    }

    private void showContextMenu(Object controller, double x, double y) {
        CustomMenuItem customMenuItem = new CustomMenuItem((Node) controller);
        customMenuItem.getStyleClass().add("tag-filter-menu-item");
        customMenuItem.hideOnClickProperty().set(false);

        if (contextMenu == null) {
            contextMenu = new ContextMenu();
        }

        contextMenu.getItems().clear();
        contextMenu.getItems().add(customMenuItem);
        contextMenu.show(this, x, y);
        contextMenu.setOnHidden(event -> {
            refreshTags();  // Ricarica la lista dei tag dopo la chiusura del popup
        });
    }

    public void closeInfoTag() {
        if (contextMenu != null) {
            contextMenu.hide();
        }
    }
}

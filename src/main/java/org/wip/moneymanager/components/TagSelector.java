package org.wip.moneymanager.components;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.wip.moneymanager.popUp.AddNewTagController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TagSelector extends BorderPane {
    @FXML
    private HBox tag_list;

    @FXML
    private Button add_tag;

    @FXML
    private Button add_new_tag;

    protected Parent loaded;
    private AddNewTagController addNewTagController;
    private static final TagFilter tagFilter = new TagFilter();
    private final CustomMenuItem customMenuItem;
    private static ContextMenu contextMenu = null;

    //Grazie JavaFX per farmi mettere tutta sta roba, spero che almeno ne valga la pena
    private final ArrayList<Tag> tagsList = new ArrayList<>();
    private final ObservableList<Tag> observableTagList = FXCollections.observableArrayList(tagsList);
    private final ListProperty<Tag> tags = new SimpleListProperty<>(observableTagList);

    public List<Tag> get_selected_tags() {
        List<Tag> selected_tags = new ArrayList<>();
        for (Tag tag : tags) {
            if (tag.getTagStatus() == 1) {
                selected_tags.add(tag);
            }
        }
        return selected_tags;
    }

    public TagSelector() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/tagselector.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            loaded = fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        customMenuItem = new CustomMenuItem(tagFilter);
        customMenuItem.hideOnClickProperty().set(false);

        contextMenu = new ContextMenu(customMenuItem);
        contextMenu.getStyleClass().add("tag-filter-context-menu");
    }

    public void initialize() {
        add_tag.setOnAction(_ -> showTagFilter());

        tags.addListener((_, _, newValue) -> {
            tag_list.getChildren().clear();
            tag_list.getChildren().addAll(newValue);
        });

        tagFilter.tagsProperty().addListener((_, _, newValue) -> {
            for (Tag tag : tagFilter.tagsProperty()) {
                // Aggiungi tag solo se non è già presente
                boolean exists = tags.stream()
                        .anyMatch(existingTag -> existingTag.getTag().equals(tag.getTag()));
                if (!exists) {
                    Tag tmp = new Tag(tag.tagProperty().get(), tag.getTagStatus(), tag.getModalita(), tag.getColor());
                    tmp.tagStatusProperty().bindBidirectional(tag.tagStatusProperty());
                    tmp.tagStatusProperty().addListener((_, _, newValue1) -> tmp.setVisible(newValue1.intValue() != 0));
                    tmp.managedProperty().bindBidirectional(tmp.visibleProperty());
                    tmp.setVisible(false);
                    tags.add(tmp);
                }
            }
        });

        add_new_tag.setOnAction(_ -> showAddNewTag());
    }

    private void addTag(Tag tag) {
        tags.add(tag);
    }


    public  void disable(){
        add_tag.setDisable(true);
        add_new_tag.setDisable(true);
    }

    public void enable(){
        add_tag.setDisable(false);
        add_new_tag.setDisable(false);
    }
    private void removeTag(Tag tag) {
        tags.remove(tag);
    }

    private void addTags(List<Tag> tags) {
        this.tags.addAll(tags);
    }

    private void showTagFilter() {
        // Crea un nuovo CustomMenuItem con il contenuto del tag filter
        CustomMenuItem newCustomMenuItem = new CustomMenuItem(tagFilter);
        newCustomMenuItem.getStyleClass().add("tag-filter-menu-item");
        newCustomMenuItem.hideOnClickProperty().set(false);

        // Aggiorna il ContextMenu con il nuovo CustomMenuItem
        contextMenu.getItems().clear(); // Rimuovi il contenuto precedente
        contextMenu.getItems().add(newCustomMenuItem);

        double screenX = add_tag.localToScreen(add_tag.getBoundsInLocal()).getMinX() - 280;
        double screenY = add_tag.localToScreen(add_tag.getBoundsInLocal()).getMinY() + 30;
        contextMenu.show(this, screenX, screenY);

        this.layout();
    }

    private void showAddNewTag() {
        try {
            if (addNewTagController == null) {
                addNewTagController = new AddNewTagController(loaded.getScene().getWindow());
            }

            // Crea un nuovo CustomMenuItem con il contenuto di AddNewTagController
            CustomMenuItem newCustomMenuItem = new CustomMenuItem(addNewTagController);
            newCustomMenuItem.getStyleClass().add("tag-filter-menu-item");
            newCustomMenuItem.hideOnClickProperty().set(false);

            // Aggiorna il ContextMenu con il nuovo CustomMenuItem
            contextMenu.getItems().clear(); // Rimuovi il contenuto precedente
            contextMenu.getItems().add(newCustomMenuItem);

            // Mostra il ContextMenu
            double screenX = add_new_tag.localToScreen(add_new_tag.getBoundsInLocal()).getMinX() - 280;
            double screenY = add_new_tag.localToScreen(add_new_tag.getBoundsInLocal()).getMinY() + 30;
            contextMenu.show(this, screenX, screenY);

            // Forza il ricalcolo del layout per aggiornare il contenuto
            this.layout();  // Esegui il layout sulla scena o sul nodo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void closeAddNewTag() {
        contextMenu.hide(); // Chiudi il menu
        tagFilter.refreshTags(); // Pulisci i tag nel filtro
    }

    public void clearTags() {
        // Resetta completamente i tag nel TagSelector
        tags.clear();
        tag_list.getChildren().clear();
        tagFilter.refreshTags();
    }

    public void reset() {
        // Resetta tutti i tag e aggiorna il filtro
        clearTags();
        tagFilter.refreshTags();
    }

}

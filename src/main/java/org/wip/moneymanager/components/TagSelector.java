package org.wip.moneymanager.components;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    private AddNewTagController addNewTagController;
    private final TagFilter tagFilter = new TagFilter(this  );
    private final CustomMenuItem tagFilterMenu;
    private final ContextMenu addNewTagMenu; // WTF perché queso è un context menu e l'altro è un custom menu item
    private final ListProperty<Tag> tags = new SimpleListProperty<>(FXCollections.observableArrayList());
    List<Tag> tagsSelected = new ArrayList<>();


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
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        tagFilterMenu = new CustomMenuItem(tagFilter);
        tagFilterMenu.hideOnClickProperty().set(false);

        addNewTagMenu = new ContextMenu(tagFilterMenu);
        addNewTagMenu.getStyleClass().add("tag-filter-context-menu");
    }

    @FXML
    public void initialize() {
        add_tag.setOnAction(_ -> showTagFilter());

        tags.addListener((_, _, newValue) -> {
            tag_list.getChildren().clear();
            tag_list.getChildren().addAll(newValue);
        });

        tagFilter.tagsProperty().addListener((_, _, newValue) -> {
            for (Tag tag : tagFilter.tagsProperty()) {
                // Aggiungi tag solo se non è già presente
                boolean exists = tags.stream().anyMatch(existingTag -> existingTag.getTag().equals(tag.getTag()));
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

    public void selectTag(Tag tag) {
        tagsSelected.add(tag);
    }

    public void setVisibleSelectedTags(){
        for (Tag t : tagFilter.tagsProperty()) {
            for (Tag tag : tagsSelected) {
                if (t.getTag().equals(tag.getTag())) {
                    // Aggiungi tag solo se non è già presente
                    boolean exists = tags.stream().anyMatch(existingTag -> existingTag.getTag().equals(tag.getTag()));
                    if (!exists) {
                        Tag tmp = new Tag(tag.tagProperty().get(), tag.getTagStatus(), tag.getModalita(), tag.getColor());
                        tmp.tagStatusProperty().bindBidirectional(tag.tagStatusProperty());
                        tmp.tagStatusProperty().addListener((_, _, newValue1) -> tmp.setVisible(newValue1.intValue() != 0));
                        tmp.managedProperty().bindBidirectional(tmp.visibleProperty());
                        tmp.setVisible(true);
                        tags.add(tmp);
                    } else {
                        tags.stream().filter(existingTag -> existingTag.getTag().equals(tag.getTag())).findFirst().ifPresent(existingTag -> existingTag.tagStatusProperty().set(1));
                    }
                }
            }
        }
    }

    private void showTagFilter() {
        // Crea un nuovo CustomMenuItem con il contenuto del tag filter
        CustomMenuItem newCustomMenuItem = new CustomMenuItem(tagFilter);
        newCustomMenuItem.getStyleClass().add("tag-filter-menu-item");
        newCustomMenuItem.hideOnClickProperty().set(false);

        // Aggiorna il ContextMenu con il nuovo CustomMenuItem
        addNewTagMenu.getItems().clear(); // Rimuovi il contenuto precedente
        addNewTagMenu.getItems().add(newCustomMenuItem);

        double screenX = add_tag.localToScreen(add_tag.getBoundsInLocal()).getMinX() - 280;
        double screenY = add_tag.localToScreen(add_tag.getBoundsInLocal()).getMinY() + 30;
        addNewTagMenu.show(this, screenX, screenY);

        this.layout();
    }

    private void showAddNewTag() {
        try {
            if (addNewTagController == null) {
                addNewTagController = new AddNewTagController(this);
            }

            // Crea un nuovo CustomMenuItem con il contenuto di AddNewTagController
            CustomMenuItem newCustomMenuItem = new CustomMenuItem(addNewTagController);
            newCustomMenuItem.getStyleClass().add("tag-filter-menu-item");
            newCustomMenuItem.hideOnClickProperty().set(false);

            // Aggiorna il ContextMenu con il nuovo CustomMenuItem
            addNewTagMenu.getItems().clear(); // Rimuovi il contenuto precedente
            addNewTagMenu.getItems().add(newCustomMenuItem);

            // Mostra il ContextMenu
            double screenX = add_new_tag.localToScreen(add_new_tag.getBoundsInLocal()).getMinX() - 280;
            double screenY = add_new_tag.localToScreen(add_new_tag.getBoundsInLocal()).getMinY() + 30;
            addNewTagMenu.show(this, screenX, screenY);

            // Forza il ricalcolo del layout per aggiornare il contenuto
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeAddNewTag() {
        addNewTagMenu.hide();
        tagFilter.refreshTags();
    }

    public void clearTags() {
        tags.clear();
        tag_list.getChildren().clear();
        tagFilter.refreshTags();
    }
}

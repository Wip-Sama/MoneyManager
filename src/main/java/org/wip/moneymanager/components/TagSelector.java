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
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.popUp.AddNewAccountController;
import org.wip.moneymanager.popUp.AddNewTagController;

import java.io.IOException;
import java.util.ArrayList;

public class TagSelector extends BorderPane {
    @FXML
    private HBox tag_list;

    @FXML
    private Button add_tag;

    @FXML
    private Button add_new_tag;

    protected Parent loaded;
    private AddNewTagController AddNewtag;
    private final TagFilter tagFilter = new TagFilter();
    private final CustomMenuItem customMenuItem;
    private final ContextMenu contextMenu;

    //Grazie JavaFX per farmi mettere tutta sta roba, spero che almeno ne valga la pena
    private final ArrayList<Tag> tagsList = new ArrayList<>();
    private final ObservableList<Tag> observableTagList = FXCollections.observableArrayList(tagsList);
    private final ListProperty<Tag> tags = new SimpleListProperty<>(observableTagList);

    public TagSelector() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/tagselector.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            loaded = fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        tagFilter.setPrefWidth(400);

        customMenuItem = new CustomMenuItem(tagFilter);
        customMenuItem.getStyleClass().add("tag-filter-menu-item");
        customMenuItem.hideOnClickProperty().set(false);

        contextMenu = new ContextMenu(customMenuItem);
        contextMenu.getStyleClass().add("tag-filter-context-menu");

        // Sarebbe meglio avere tutto in tag-filter-context-menu
        // quindi tag-filter-context-menu.menu-item ecc...
        // Ma per ora funziona in caso lo cambierò in futuro
        // è più una mia preferenza che altro
    }

    public void initialize() {
        add_tag.setOnAction(_ -> show_tag_filter());
        tags.addListener((_, _, newValue) -> {
            tag_list.getChildren().clear();
            tag_list.getChildren().addAll(newValue);
        });
        for (Tag tag : tagFilter.tagsProperty()) {
            Tag tmp = new Tag(tag.tagProperty().get(), tag.getTagStatus(), tag.getModalita());
            tmp.tagStatusProperty().bindBidirectional(tag.tagStatusProperty());
            tmp.tagStatusProperty().addListener((_, _, newValue) -> tmp.setVisible(newValue.intValue() != 0));
            tmp.managedProperty().bindBidirectional(tmp.visibleProperty());
            tmp.setVisible(false);
            tags.add(tmp);
        }

        add_new_tag.setOnAction(event -> {
            try {
                if (AddNewtag == null) {
                    AddNewtag = new AddNewTagController(loaded.getScene().getWindow());
                }
                AddNewtag.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void show_tag_filter() {
        double screenX = add_tag.localToScreen(add_tag.getBoundsInLocal()).getMinX()-400;
        double screenY = add_tag.localToScreen(add_tag.getBoundsInLocal()).getMinY()+50;
        contextMenu.show(this, screenX, screenY);
    }
}

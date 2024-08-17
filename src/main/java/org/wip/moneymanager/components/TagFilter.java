package org.wip.moneymanager.components;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.ArrayList;


public class TagFilter extends BorderPane {
    @FXML
    public TextField search_bar;

    @FXML
    public FlowPane tag_pane;

    @FXML
    public ScrollPane scroll_pane;

    private final ArrayList<Tag> tagsList = new ArrayList<>();
    private final ObservableList<Tag> observableTagList = FXCollections.observableArrayList(tagsList);
    private final ListProperty<Tag> tags = new SimpleListProperty<>(observableTagList);

    public ReadOnlyListProperty<Tag> tagsProperty() {
        return tags;
    }

    public TagFilter() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/tagfilter.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        tags.addListener((_, _, newValue) -> {
            tag_pane.getChildren().clear();
            tag_pane.getChildren().addAll(newValue);
            for (Tag tag : newValue) {
                search_bar.textProperty().addListener((_, _, newValue1) -> {
                    if (newValue1.isEmpty()) {
                        tag.setVisible(true);
                    } else {
                        tag.setVisible(tag.tagProperty().get().contains(newValue1));
                    }
                });
                tag.managedProperty().bindBidirectional(tag.visibleProperty());
            }
        });
        //TODO: Sostituire con un get dal org.wip.moneymanager.model
        for (int i = 0; i < 100; i++) {
            tags.add(new Tag("Tag test " + i, 0, 2));
        }
    }
}

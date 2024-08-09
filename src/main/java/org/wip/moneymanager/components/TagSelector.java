package org.wip.moneymanager.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.BufferedInputStream;
import java.io.IOException;

public class TagSelector extends BorderPane {
    @FXML
    private HBox tag_list;

    @FXML
    private Button add_tag;

    private TagFilter tagFilter;
    protected Parent loaded;

    public TagSelector() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/tagselector.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            loaded = fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void initialize() {
        add_tag.setOnAction(event -> {
            show_tag_filter();
        });
    }

    private void show_tag_filter() {
        if (tagFilter == null) {
            tagFilter = new TagFilter(getScene().getWindow());
        } else {
            tagFilter.toggle();
        }
    }
}

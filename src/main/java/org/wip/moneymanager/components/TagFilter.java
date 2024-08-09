package org.wip.moneymanager.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.Vector;

public class TagFilter extends BorderPane {
    @FXML
    public TextField search_bar;

    @FXML
    public FlowPane tag_pane;

    @FXML
    public ScrollPane scroll_pane;


    private Vector<Tag> tags = new Vector<>();

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
        //tag_pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        for (int i = 0; i < 100; i++) {
            Tag tmp = new Tag("testtag"+i, 0, 1);
            tmp.setVisibleListener(search_bar.textProperty());
            tmp.managedProperty().bindBidirectional(tmp.visibleProperty());
            tags.add(tmp);
        }
        tag_pane.getChildren().addAll(tags);
    }
}

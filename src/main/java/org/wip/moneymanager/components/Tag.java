package org.wip.moneymanager.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import org.wip.moneymanager.model.DBObjects.dbTag;

import java.io.IOException;

public class Tag extends HBox {

    @FXML
    private ToggleButton singleTag;

    private dbTag tag;

    public Tag() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/tag.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Impossibile caricare il file tag.fxml", e);
        }
    }

    public void initialize(dbTag tag) {
        this.tag = tag;
        singleTag.setText(tag.name());
        singleTag.setStyle("-fx-background-color: " + tag.color() + ";");

        singleTag.setOnAction(event -> {
            if (singleTag.isSelected()) {
                SearchBar.addToFilter(tag);
            } else {
                SearchBar.removeToFilter(tag);
            }
        });
    }
}

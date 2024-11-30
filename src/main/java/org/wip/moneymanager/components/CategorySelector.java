package org.wip.moneymanager.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class CategorySelector extends HBox {

    @FXML
    private ComboBox<String> category_box;

    @FXML
    private Label separator;

    @FXML
    private ComboBox<String> sub_category_box;

    public CategorySelector() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/categorySelector.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void initialize(){

    }

}

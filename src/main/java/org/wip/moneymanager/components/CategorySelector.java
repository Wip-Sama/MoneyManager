package org.wip.moneymanager.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class CategorySelector extends BorderPane {

    @FXML
    private ChoiceBox<String> category_box;

    @FXML
    private Label separator;

    @FXML
    private ChoiceBox<String> sub_category_box;

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

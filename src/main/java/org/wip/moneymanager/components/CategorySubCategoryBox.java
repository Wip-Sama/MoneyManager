package org.wip.moneymanager.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class CategorySubCategoryBox extends HBox {

    @FXML
    private Label category;

    @FXML
    private ChoiceBox<?> categoryBox;

    @FXML
    private Label subCategory;

    @FXML
    private ChoiceBox<?> subCategoryBox;

    public CategorySubCategoryBox() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/categorySubCategoryBox.fxml"));
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

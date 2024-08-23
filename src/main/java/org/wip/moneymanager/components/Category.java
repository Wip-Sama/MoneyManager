package org.wip.moneymanager.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import org.wip.moneymanager.model.Data;

import java.io.IOException;

public class Category extends BorderPane {

    @FXML
    private Button rename;
    @FXML
    private Button delete;
    @FXML
    private TextField category_display;


    public final BooleanProperty selected = new SimpleBooleanProperty(false);

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    public Category() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/category.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void update_rename_button() {
        rename.textProperty().unbind();
        rename.textProperty().bind(Data.lsp.lsb(category_display.editableProperty().get() ? "categoryeditor.save" : "categoryeditor.rename"));
    }

    @FXML
    public void initialize() {
        rename.textProperty().bind(Data.lsp.lsb(category_display.editableProperty().get() ? "categoryeditor.save" : "categoryeditor.rename"));
        delete.textProperty().bind(Data.lsp.lsb("categoryeditor.delete"));
        selected.addListener((_, _, newValue) -> {
            pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, newValue);
        });
        onMouseClickedProperty().set(event -> {
            selected.set(!selected.get());
        });
        rename.onMouseClickedProperty().set(_ -> {
            if (!category_display.editableProperty().get()) {
                category_display.requestFocus();
            }
            category_display.editableProperty().set(category_display.editableProperty().not().get());
        });
        category_display.onKeyReleasedProperty().set(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                category_display.editableProperty().set(false);
            }
        });
        category_display.editableProperty().addListener((_, _, _) -> {
            rename.textProperty().unbind();
            rename.textProperty().bind(Data.lsp.lsb(category_display.editableProperty().get() ? "categoryeditor.save" : "categoryeditor.rename"));
        });
        onMouseExitedProperty().set(_ -> category_display.editableProperty().set(false));
    }
}

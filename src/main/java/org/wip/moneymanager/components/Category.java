package org.wip.moneymanager.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.wip.moneymanager.model.DBObjects.dbCategory;
import org.wip.moneymanager.model.Data;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
// perché java non supporta gli alias per gli import...

public class Category extends BorderPane {
    @FXML
    private Button rename;
    @FXML
    private Button delete;
    @FXML
    private TextField category_display;

    public final BooleanProperty destruct = new SimpleBooleanProperty(false);
    public final BooleanProperty selected = new SimpleBooleanProperty(false);
    private long lastClickTime = 0;
    private dbCategory dbcategory;

    private String oldName;
    public boolean is_tmp = true;
    private int parent = -1;
    private int type = 0;
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private static final PseudoClass CONTORNO_ABILITATO = PseudoClass.getPseudoClass("contorno_abilitato");

    public final BooleanProperty renaming = new SimpleBooleanProperty(false);

    public dbCategory getDbcategory() {
        return dbcategory;
    }

    public Category() {
        is_tmp = false;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/category.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Category(dbCategory dbcategory) {
        this();
        this.dbcategory = dbcategory;
        type = dbcategory.type().get();
        category_display.setText(dbcategory.name().get());
    }

    public Category(int type) {
        this();
        this.type = type;
        is_tmp = true;
    }

    public Category(int type, int parent) {
        this(type);
        this.parent = parent;
    }

    private void update_rename_button() {
        rename.textProperty().unbind();
        rename.textProperty().bind(Data.lsp.lsb(renaming.get() ? "categoryeditor.save" : "categoryeditor.rename"));
    }

    @FXML
    public void initialize() {
        update_rename_button();
        delete.textProperty().bind(Data.lsp.lsb("categoryeditor.delete"));
        selected.addListener((_, _, newValue) -> {
            pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, newValue);
        });

        onMouseClickedProperty().set(_ -> selected.set(!selected.get()));

        onMouseExitedProperty().set(_ -> {
            if (!is_tmp) {
                renaming.set(false);
            }
        });

        rename.onMouseClickedProperty().set(_ -> {
            renaming.set(!renaming.get());
        });

        category_display.onKeyPressedProperty().set(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                renaming.set(false);
            }
        });

        category_display.focusedProperty().addListener((_, _, newValue) -> {
            if (renaming.get()) {
                category_display.requestFocus();
            }
        });

        category_display.editableProperty().bind(renaming);

        renaming.addListener((_, _, newValue) -> {
            update_rename_button();
            pseudoClassStateChanged(CONTORNO_ABILITATO, newValue);

            if (newValue) {
                oldName = category_display.getText();
                category_display.requestFocus();
            } else {
                if (category_display.getText().equals(oldName) && !category_display.getText().isEmpty()) {
                    return;
                }
                if (category_display.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, Data.lsp.lsb("alert.namme_cannot_be_null").get(), ButtonType.OK);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                    if (is_tmp) {
                        destruct.set(true);
                    }
                    return;
                }

                Task<dbCategory> exist;
                if (parent == -1)
                    exist = Data.userDatabase.getCategory(category_display.getText(), type);
                else
                    exist = Data.userDatabase.getSubcategory(category_display.getText(), type, parent);
                exist.run();

                try {
                    if (exist.get() == null) {
                        if (dbcategory == null) {
                            Task<Boolean> create;
                            if (parent == -1) {
                                create = Data.userDatabase.createCategory(category_display.getText(), type);
                            } else {
                                create = Data.userDatabase.createSubcategory(category_display.getText(), type, parent);
                            }
                            create.run();
                            Task<dbCategory> ct = Data.userDatabase.getCategory(category_display.getText(), type);
                            ct.run();
                            dbcategory = ct.get();
                            is_tmp = false;
                        } else {
                            dbcategory.setName(category_display.getText());
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING, Data.lsp.lsb("alert.this_name_is_already_in_use").get(), ButtonType.OK);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.showAndWait();
                        if (is_tmp) {
                            destruct.set(true);
                            return;
                        } else {
                            category_display.setText(oldName);
                        }
                    }
                } catch (InterruptedException | ExecutionException | SQLException e) {
                    throw new RuntimeException(e);
                }
                requestFocus();
            }
        });

        category_display.sceneProperty().addListener((_, _, newValue1) -> {
            if (newValue1 != null) {
                renaming.set(is_tmp);
            }
        });

        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(new Duration(0));
        tooltip.setHideDelay(new Duration(0));
        tooltip.textProperty().bind(Data.lsp.lsb("categoryeditor.delete.tooltip"));
        delete.setTooltip(tooltip);
        delete.setOnMouseExited(_ -> tooltip.textProperty().bind(Data.lsp.lsb("categoryeditor.delete.tooltip")));
        delete.setOnMouseClicked(event -> handleMouseClick(event.isShiftDown()));
        delete.setOnMouseEntered(event -> updateTooltipText(tooltip, event.isShiftDown()));
    }

    private void updateTooltipText(Tooltip tooltip, boolean shiftDown) {
        if (shiftDown) {
            tooltip.textProperty().bind(Data.lsp.lsb("categoryeditor.delete.tooltip_shift"));
        } else {
            tooltip.textProperty().bind(Data.lsp.lsb("categoryeditor.delete.tooltip"));
        }
    }

    private void handleMouseClick(boolean shiftDown) {
        long currentTime = System.currentTimeMillis();
        if (shiftDown) {
            destruct.set(true);
        } else {
            if (currentTime - lastClickTime <= 200) {
                destruct.set(true);
            }
            lastClickTime = currentTime;
        }
    }

    public void selectionable(boolean value) {
        if (value) {
            onMouseClickedProperty().set(_ -> selected.set(!selected.get()));
        } else {
            onMouseClickedProperty().set(_ -> renaming.set(true));
            selected.set(false);
        }
    }
}

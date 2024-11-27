package org.wip.moneymanager.components;

import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class Tag extends BorderPane {
    @FXML
    private Label tagLabel;

    @FXML
    private BorderPane tagBackground;

    public final IntegerProperty red = new SimpleIntegerProperty(0);
    public final IntegerProperty green = new SimpleIntegerProperty(0);
    public final IntegerProperty blue = new SimpleIntegerProperty(0);

    private final StringProperty color = new SimpleStringProperty();

    private final IntegerProperty tag_status = new SimpleIntegerProperty(0);
    private final StringProperty tag = new SimpleStringProperty();
    private final IntegerProperty modalita = new SimpleIntegerProperty(0);
    // 0: display
    // 1: select
    // 2: filter

    private static final PseudoClass DISCARDED_PSEUDO_CLASS = PseudoClass.getPseudoClass("discarded");
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    public Tag() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/tag.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        onMouseClickedProperty().set(_ -> {
            if (modalita.get() == 2) {
                tag_status.set((tag_status.get() + 1) % 3);
            } else if (modalita.get() == 1) {
                tag_status.set((tag_status.get() + 1) % 2);
            }
        });
        tag_status.addListener((_, _, newValue) -> {
            pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, newValue.intValue() == 1);
            pseudoClassStateChanged(DISCARDED_PSEUDO_CLASS, newValue.intValue() == 2);
        });
    }

    public Tag(String _tag, int _tag_status, int _modalita, String color) {
        this();
        this.tag.set(_tag);
        this.tag_status.set(_tag_status);
        this.modalita.set(_modalita);
        this.color.set(color);
        setColor(color);
    }

    private void setColor(String color) {
        tagBackground.setStyle("-fx-background-color: " + color + ";");
    }

    @FXML
    public void initialize() {
        tagLabel.textProperty().bindBidirectional(tag);
    }

    public String getTag() {
        return tag.get();
    }

    public void setTag(String tag) {
        this.tag.set(tag);
    }

    public StringProperty tagProperty() {
        return tag;
    }

    public int getModalita() {
        return modalita.get();
    }

    public void setModalita(int mod) {
        this.modalita.set(mod);
    }

    public IntegerProperty modalitaProperty() {
        if (modalita.get() < 0) {
            modalita.set(0);
        } else if (modalita.get() > 2) {
            modalita.set(2);
        }
        return modalita;
    }

    public int getTagStatus() {
        return tag_status.get();
    }

    public void setTag_status(int status) {
        if (status < 0) {
            status = 0;
        } else if (status > 2) {
            status = 2;
        }
        this.tag_status.set(status);
    }

    public IntegerProperty tagStatusProperty() {
        return tag_status;
    }

    public String getColor() {
        return color.get();
    }
}

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

    private final IntegerProperty tag_status = new SimpleIntegerProperty(0);

    /*TODO: non so se vogliamo far in modo che i tag abbiamo un colore selezionabile dall'utente
    *  vorrei evitare ma nel dubbio metto le property pronte casomai volessimo farlo*/
    public final IntegerProperty red = new SimpleIntegerProperty(0);
    public final IntegerProperty green = new SimpleIntegerProperty(0);
    public final IntegerProperty blue = new SimpleIntegerProperty(0);

    private final StringProperty tag = new SimpleStringProperty();

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
        onMouseClickedProperty().set(event -> {
            tag_status.set((tag_status.get() + 1) % 3);
        });
        tag_status.addListener((_, _, newValue) -> {
            pseudoClassStateChanged(DISCARDED_PSEUDO_CLASS, newValue.intValue() == 1);
            pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, newValue.intValue() == 2);
        });
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
}

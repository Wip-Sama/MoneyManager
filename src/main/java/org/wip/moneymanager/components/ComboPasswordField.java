package org.wip.moneymanager.components;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.SVGPath;
import org.wip.moneymanager.utility.SVGLoader;

import java.io.*;

public class ComboPasswordField extends StackPane {
    @FXML
    private PasswordField password_field;

    @FXML
    private TextField text_field;

    @FXML
    private BorderPane icon_pane;

    @FXML
    private SVGPath visible_icon;

    private boolean requesting_focus;
    private final int[] selection = new int[2];
    private String show_eye;
    private String hide_eye;
    public final StringProperty password = new SimpleStringProperty();

    public ComboPasswordField() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/combopasswordfield.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        // Ringraziamo java per avere liste infinite di funzioni per fare una cosa stupida come leggere una stringa da un file
        show_eye = new SVGLoader("ic_fluent_eye_show_24_filled.svg").getPath();
        hide_eye = new SVGLoader("ic_fluent_eye_hide_24_filled.svg").getPath();
    }
    @FXML
    public void initialize() {
        // Preferisco salvare il testo in una variabile interna alla classe che leggere il textfield per averla
        text_field.textProperty().bindBidirectional(password);
        password_field.textProperty().bindBidirectional(password);

        icon_pane.setOnMouseClicked(_ -> {
            // dato che richiediamo il focus mentre copiamo i dati è importante che non vengano aggiornati dai listener
            // vorrei far notare che sono riuscito a far mantenete la selezione tra lo swap di text e passwordfield
            requesting_focus = true;
            if (password_field.isVisible()) {
                text_field.setVisible(true);
                text_field.requestFocus();
                if (selection[0] != selection[1])
                    text_field.selectRange(selection[0], selection[1]);
                else
                    text_field.positionCaret(selection[0]);
                password_field.setVisible(false);
                visible_icon.setContent(hide_eye);
            } else {
                password_field.setVisible(true);
                password_field.requestFocus();
                if (selection[0] != selection[1])
                    password_field.selectRange(selection[0], selection[1]);
                else
                    password_field.positionCaret(selection[0]);
                text_field.setVisible(false);
                visible_icon.setContent(show_eye);
            }
            requesting_focus = false;
        });

        password_field.caretPositionProperty().addListener((_, _, newValue) -> {
            if (requesting_focus)
                return;
            if(password_field.focusedProperty().get())
                selection[0] = selection[1] = newValue.intValue();
        });
        text_field.caretPositionProperty().addListener((_, _, newValue) -> {
            if (requesting_focus)
                return;
            if(text_field.focusedProperty().get())
                selection[0] = selection[1] = newValue.intValue();
        });

        password_field.selectionProperty().addListener((_, _, newValue) -> {
            if (requesting_focus)
                return;
            if(password_field.focusedProperty().get()) {
                selection[0] = newValue.getStart();
                selection[1] = newValue.getEnd();
            }
        });
        text_field.selectionProperty().addListener((_, _, newValue) -> {
            if (requesting_focus)
                return;
            if(text_field.focusedProperty().get()) {
                    selection[0] = newValue.getStart();
                    selection[1] = newValue.getEnd();
            }
        });
    }
}

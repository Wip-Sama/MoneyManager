package org.wip.moneymanager.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import org.wip.moneymanager.model.Data;

import java.io.IOException;
import java.sql.SQLException;

public class ColorPickerButton extends AnchorPane {
    @FXML
    protected Button color_picker_button;

    protected ToggleButton tg;

    private final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    public final BooleanProperty selected = new SimpleBooleanProperty(false);
    // TODO: Shift + click = setta il colore già presente nel color picker
    // TODO: Cambiare il colore cambia il tema globale

    protected final Property<Number> red = new SimpleDoubleProperty(0);
    protected final Property<Number> green = new SimpleDoubleProperty(0);
    protected final Property<Number> blue = new SimpleDoubleProperty(0);

    protected Parent loaded;
    private ColorPickerPopup colorPickerPopup;

    public ColorPickerButton() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/colorpickerbutton.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            loaded = fxmlLoader.load();
        } catch (IOException e) {
            // Se questo fallisce realisticamente non siamo in grado di caricare l'app quindi c'è un problema di fondo
            e.printStackTrace();
        }

        selected.addListener((_, _, newValue) -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, newValue)
        );
    }

    @FXML
    protected void initialize() {
        red.addListener(_ -> updateColors());
        green.addListener(_ -> updateColors());
        blue.addListener(_ -> updateColors());

        color_picker_button.onActionProperty().set(_ -> select_color());
    }

    protected void updateColors() {
        color_picker_button.setStyle("-fx-background-color: -fu-stroke-rest, rgb(" + red.getValue() + "," + green.getValue() + "," + blue.getValue() + ")");
    }

    @FXML
    protected void select_color() {
        if (colorPickerPopup == null) {
            try {
                colorPickerPopup = new ColorPickerPopup(loaded.getScene().getWindow());
                colorPickerPopup.changesSaved.addListener((_, _, newValue) -> {
                    if (newValue) {
                        try {
                            Data.dbUser.setAccent(getRGB());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    colorPickerPopup.changesSaved.set(false);
                });

                // Potremmo far in modo che i colori cambiano solo se l'utente conferma la selezione
                colorPickerPopup.red_channel.bindBidirectional(red);
                colorPickerPopup.green_channel.bindBidirectional(green);
                colorPickerPopup.blue_channel.bindBidirectional(blue);
            } catch (IOException e) {
                // TODO: Dire che qualcosa è andato storto con l'avvio del color picker
                // TODO: Loggare l'errore
            }
        }
        colorPickerPopup.show();
    }

    public void setRed(Number red) {
        this.red.setValue(red);
    }

    public void setGreen(Number green) {
        this.green.setValue(green);
    }

    public void setBlue(Number blue) {
        this.blue.setValue(blue);
    }

    public void setRGB(int[] rgb) {
        this.red.setValue(rgb[0]);
        this.green.setValue(rgb[1]);
        this.blue.setValue(rgb[2]);
    }
    public void setRGB(int red, int green, int blue) {
        this.red.setValue(red);
        this.green.setValue(green);
        this.blue.setValue(blue);
    }

    public int[] getRGB() {
        return new int[] {red.getValue().intValue(), green.getValue().intValue(), blue.getValue().intValue()};
    }
}
package org.wip.moneymanager.components;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ColorPickerButton extends AnchorPane {
    @FXML
    protected Button color_picker_button;

    protected final boolean selected = false;
    // TODO: Aggiungere la spunta per far capire che è stato selezionato (come il ColorPickerPreset)

    protected static Property<Number> red = new SimpleDoubleProperty(0);
    protected static Property<Number> green = new SimpleDoubleProperty(0);
    protected static Property<Number> blue = new SimpleDoubleProperty(0);

    protected static Parent loaded;
    private static ColorPickerPopup colorPickerPopup;

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
    }

    @FXML
    protected void initialize() throws IOException {
        red.addListener(_ -> updateColors());
        green.addListener(_ -> updateColors());
        blue.addListener(_ -> updateColors());

        color_picker_button.onActionProperty().set(event -> {
            select_color();
        });
    }

    protected void updateColors() {
        color_picker_button.setStyle("-fx-background-color: -fu-stroke-rest, rgb(" + red.getValue() + "," + green.getValue() + "," + blue.getValue() + ")");
    }

    @FXML
    protected void select_color() {
        if (colorPickerPopup == null) {
            try {
                colorPickerPopup = new ColorPickerPopup(loaded.getScene().getWindow());
                // Potremmo far in modo che i colori cambiano solo se l'utente conferma la selezione
                colorPickerPopup.red_channel.bindBidirectional(red);
                colorPickerPopup.green_channel.bindBidirectional(green);
                colorPickerPopup.blue_channel.bindBidirectional(blue);
            } catch (IOException e) {
                // TODO: Dire che qualcosa è andato storto con l'avvio del color picker
                // TODO: Loggare l'errore
            }
        }
        colorPickerPopup.toggle();
    }
}
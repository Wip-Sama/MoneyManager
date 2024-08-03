package org.wip.moneymanager.components;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ColorPickerPreset extends AnchorPane {
    protected static Property<Number> red = new SimpleDoubleProperty(0);
    protected static Property<Number> green = new SimpleDoubleProperty(0);
    protected static Property<Number> blue = new SimpleDoubleProperty(0);

    @FXML
    protected Button color_preset_button;

    protected static Parent loaded;

    public ColorPickerPreset(int r, int g, int b) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/colorpickerpreset.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            loaded = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        red.setValue(r);
        green.setValue(g);
        blue.setValue(b);
    }

    public ColorPickerPreset() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/colorpickerpreset.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            loaded = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateColor() {
        color_preset_button.setStyle("-fx-background-color: -fu-stroke-rest, rgb(" + red.getValue() + "," + green.getValue() + "," + blue.getValue() + ")");
    }

    @FXML
    public void initialize() {
        red.addListener(_ -> updateColor());
        green.addListener(_ -> updateColor());
        blue.addListener(_ -> updateColor());
        updateColor();
    }
}

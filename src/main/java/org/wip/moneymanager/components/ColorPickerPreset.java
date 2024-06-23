package org.wip.moneymanager.components;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.wip.moneymanager.HelloApplication;

import java.io.IOException;

public class ColorPickerPreset extends AnchorPane {
    protected static Property<Number> red = new SimpleDoubleProperty(0);
    protected static Property<Number> green = new SimpleDoubleProperty(0);
    protected static Property<Number> blue = new SimpleDoubleProperty(0);

    protected static Parent loaded;

    public ColorPickerPreset(int r, int g, int b) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("components/colorpickerpreset.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        loaded = fxmlLoader.load();
        red.setValue(r);
        green.setValue(g);
        blue.setValue(b);
    }

    public ColorPickerPreset() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("components/colorpickerpreset.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        loaded = fxmlLoader.load();
    }
}

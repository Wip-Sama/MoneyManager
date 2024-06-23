package org.wip.moneymanager.components;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.wip.moneymanager.HelloApplication;

import java.io.IOException;

public class ColorPickerButton extends AnchorPane {
    @FXML
    protected Button color_picker_button;

    protected static Property<Number> red = new SimpleDoubleProperty(0);
    protected static Property<Number> green = new SimpleDoubleProperty(0);
    protected static Property<Number> blue = new SimpleDoubleProperty(0);

    protected static Parent loaded;
    private static ColorPickerPopup colorPickerPopup;

    public ColorPickerButton() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("components/colorpickerbutton.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        loaded = fxmlLoader.load();
    }

    @FXML
    protected void initialize() {
        red.addListener(ChangeListener -> updateColors());
        green.addListener(ChangeListener -> updateColors());
        blue.addListener(ChangeListener -> updateColors());
    }

    protected void updateColors() {
        color_picker_button.setStyle("-fx-background-color: -fu-stroke-rest, rgb(" + red.getValue() + "," + green.getValue() + "," + blue.getValue() + ")");
    }

    @FXML
    protected void select_color() throws IOException {
        if (colorPickerPopup == null) {
            colorPickerPopup = new ColorPickerPopup(loaded.getScene().getWindow());
            colorPickerPopup.red_channel.bindBidirectional(red);
            colorPickerPopup.green_channel.bindBidirectional(green);
            colorPickerPopup.blue_channel.bindBidirectional(blue);
        }
        colorPickerPopup.toggle();
    }


}
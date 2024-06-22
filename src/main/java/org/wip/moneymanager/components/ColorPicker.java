package org.wip.moneymanager.components;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.time.temporal.ValueRange;
import java.util.function.UnaryOperator;

public class ColorPicker {
    @FXML
    protected Pane color_preview;

    @FXML
    protected Slider red_slider;

    @FXML
    protected Slider green_slider;

    @FXML
    protected Slider blue_slider;

    @FXML
    protected TextField red_textfield;

    @FXML
    protected TextField green_textfield;

    @FXML
    protected TextField blue_textfield;

    protected Property<Number> red_channel;
    protected Property<Number> green_channel;
    protected Property<Number> blue_channel;

    @FXML
    protected void initialize() {

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty())
                return change;

            try {
                int value = Integer.parseInt(newText);
                if (value >= 0 && value <= 255) {
                    return change;
                }
            } catch (NumberFormatException e) {
                // Guardate che error handler pazzesco
            }
            return null;
        };
        StringConverter<Number> sc = new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return number.toString();
            }

            @Override
            public Number fromString(String s) {
                if (s == null || s.isEmpty()) {
                    return 0;
                }
                return Integer.parseInt(s);
            }
        };

        // Limita i textfield ad accettare solo numeri tra 0 e 255
        red_textfield.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, filter));
        green_textfield.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, filter));
        blue_textfield.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, filter));

        // Forza il valore della slider ad essere uguale a quello del textfield
        red_textfield.textProperty().bindBidirectional(red_slider.valueProperty(), sc);
        green_textfield.textProperty().bindBidirectional(green_slider.valueProperty(), sc);
        blue_textfield.textProperty().bindBidirectional(blue_slider.valueProperty(), sc);
        red_slider.valueProperty().addListener((_, _, newValue) -> {
            red_textfield.setText(String.valueOf(newValue.intValue()));
            updateColorPreview();
        });
        green_slider.valueProperty().addListener((_, _, newValue) -> {
            green_textfield.setText(String.valueOf(newValue.intValue()));
            updateColorPreview();
        });
        blue_slider.valueProperty().addListener((_, _, newValue) -> {
            blue_textfield.setText(String.valueOf(newValue.intValue()));
            updateColorPreview();
        });

    }

    protected void updateColorPreview() {
        color_preview.setStyle("-fx-background-color: rgb(" + red_slider.getValue() + "," + green_slider.getValue() + "," + blue_slider.getValue() + ");");
    }

    @FXML
    protected void discard() {

    }

    @FXML
    protected void save() {

    }
}

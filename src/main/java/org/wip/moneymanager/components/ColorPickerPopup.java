package org.wip.moneymanager.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.wip.moneymanager.View.SceneHandler;

import java.io.IOException;
import java.util.function.UnaryOperator;

public class ColorPickerPopup extends BorderPane{
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

    public final BooleanProperty changesSaved = new SimpleBooleanProperty(false);

    public final Property<Number> red_channel = new SimpleDoubleProperty(0);
    public final Property<Number> green_channel = new SimpleDoubleProperty(0);
    public final Property<Number> blue_channel = new SimpleDoubleProperty(0);

    public final int[] rgb = new int[3];
    private final CustomMenuItem customMenuItem;
    private final ContextMenu contextMenu = new ContextMenu();
    private final Window node;

    public ColorPickerPopup(Window window) throws IOException {
        node = window;
        FXMLLoader fxmlLoader = new FXMLLoader(SceneHandler.class.getResource("/org/wip/moneymanager/components/colorpickerpopup.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        Parent loaded = fxmlLoader.load();

        customMenuItem = new CustomMenuItem(loaded);
        customMenuItem.getStyleClass().add("tag-filter-menu-item");
        customMenuItem.hideOnClickProperty().set(false);
        contextMenu.getItems().add(customMenuItem);

        window.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, _ -> hide());
    }

    @FXML
    protected void initialize() {
        UnaryOperator<TextFormatter.Change> text_filter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                change.setText("0");
                return change;
            }
            try {
                if (newText.length() > 1)
                    newText = newText.replaceAll("^0+(?!$)", "");

                int value = Integer.parseInt(newText);
                if (value >= 0 && value <= 255) {
                    return change;
                }
            } catch (NumberFormatException e) {
                return null;
            }
            return null;
        };

        StringConverter<Number> converter = new StringConverter<>() {
            public String toString(Number n) {
                return n == null ? "0" : String.valueOf(n.intValue());
            }
            public Number fromString(String s) {
                return s.isEmpty() ? 0 : Integer.parseInt(s);
            }
        };

        red_textfield.setTextFormatter(new TextFormatter<>(text_filter));
        green_textfield.setTextFormatter(new TextFormatter<>(text_filter));
        blue_textfield.setTextFormatter(new TextFormatter<>(text_filter));

        red_textfield.textProperty().bindBidirectional(red_channel, converter);
        green_textfield.textProperty().bindBidirectional(green_channel, converter);
        blue_textfield.textProperty().bindBidirectional(blue_channel, converter);

        red_slider.valueProperty().bindBidirectional(red_channel);
        green_slider.valueProperty().bindBidirectional(green_channel);
        blue_slider.valueProperty().bindBidirectional(blue_channel);

        red_channel.addListener(_ -> updateColorPreview());
        green_channel.addListener(_ -> updateColorPreview());
        blue_channel.addListener(_ -> updateColorPreview());
    }

    protected void updateColorPreview() {
        color_preview.setStyle("-fx-background-color: rgb(" + red_slider.getValue() + "," + green_slider.getValue() + "," + blue_slider.getValue() + ");");
    }

    protected void store_values() {
        rgb[0] = red_channel.getValue().intValue();
        rgb[1] = green_channel.getValue().intValue();
        rgb[2] = blue_channel.getValue().intValue();
    }

    protected void restore_previous_values() {
        red_channel.setValue(rgb[0]);
        green_channel.setValue(rgb[1]);
        blue_channel.setValue(rgb[2]);
    }

    public void show(double x, double y) {
        store_values();
        contextMenu.show(node, x, y);
    }

    public void hide() {
        restore_previous_values();
        contextMenu.hide();
    }

    public void toggle(double x, double y) {
        if (contextMenu.isShowing()) {
            hide();
        } else {
            show(x, y);
        }
    }

    @FXML
    protected void discard() {
        changesSaved.set(false);
        hide();
    }

    @FXML
    protected void save() {
        store_values();
        changesSaved.set(true);
        hide();
    }
}

package org.wip.moneymanager.components;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.wip.moneymanager.HelloApplication;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.function.UnaryOperator;

public class ColorPickerPopup {
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

    private double xOffset = 0;
    private double yOffset = 0;
    public Property<Number> red_channel = new SimpleDoubleProperty(0);
    public Property<Number> green_channel = new SimpleDoubleProperty(0);
    public Property<Number> blue_channel = new SimpleDoubleProperty(0);
    public int[] rgb = new int[3];

    private final Popup popup = new Popup();
    private final Window node;

    public ColorPickerPopup(Window window) throws IOException {
        node = window;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("components/colorpickerpopup.fxml"));
        fxmlLoader.setRoot(new BorderPane()); /* L'alternativa è extends Borderpane e setRoot(this) */
        fxmlLoader.setController(this);
        Parent loaded = fxmlLoader.load();
        popup.getContent().add(loaded);

        /* Controlliamo se l'utente interagisce con la finestra sottostante e in caso chiusiamo il pulsante*/
        window.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, _ -> hide());
    }

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
        StringConverter<Number> sc = new StringConverter<>() {
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
        red_textfield.textProperty().addListener((_, _, newValue) -> {
            if (newValue.isEmpty()) {
                red_slider.setValue(0);
                red_textfield.setText("0");
            } else {
                red_slider.setValue(Integer.parseInt(newValue));
            }
        });
        green_textfield.textProperty().addListener((_, _, newValue) -> {
            if (newValue.isEmpty()) {
                green_slider.setValue(0);
                green_textfield.setText("0");
            } else {
                green_slider.setValue(Integer.parseInt(newValue));
            }
        });
        blue_textfield.textProperty().addListener((_, _, newValue) -> {
            if (newValue.isEmpty()) {
                blue_slider.setValue(0);
                blue_textfield.setText("0");
            } else {
                blue_slider.setValue(Integer.parseInt(newValue));
            }
        });

        red_slider.valueProperty().addListener((_, _, newValue) -> {
            red_channel.setValue(newValue.intValue());
            red_textfield.setText(String.valueOf(newValue.intValue()));
            updateColorPreview();
        });
        green_slider.valueProperty().addListener((_, _, newValue) -> {
            green_channel.setValue(newValue.intValue());
            green_textfield.setText(String.valueOf(newValue.intValue()));
            updateColorPreview();
        });
        blue_slider.valueProperty().addListener((_, _, newValue) -> {
            blue_channel.setValue(newValue.intValue());
            blue_textfield.setText(String.valueOf(newValue.intValue()));
            updateColorPreview();
        });

        color_preview.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        color_preview.setOnMouseDragged(event -> {
            popup.setX(event.getScreenX() - xOffset);
            popup.setY(event.getScreenY() - yOffset);
        });
    }

    protected void updateColorPreview() {
        color_preview.setStyle("-fx-background-color: rgb(" + red_slider.getValue() + "," + green_slider.getValue() + "," + blue_slider.getValue() + ");");
    }

    protected void updateSliders() {
        red_slider.setValue(red_channel.getValue().intValue());
        green_slider.setValue(green_channel.getValue().intValue());
        blue_slider.setValue(blue_channel.getValue().intValue());
    }

    protected void updateTextfields() {
        red_textfield.setText(String.valueOf(red_channel.getValue().intValue()));
        green_textfield.setText(String.valueOf(green_channel.getValue().intValue()));
        blue_textfield.setText(String.valueOf(blue_channel.getValue().intValue()));
    }

    protected void store_previous_values() {
        rgb[0] = red_channel.getValue().intValue();
        rgb[1] = green_channel.getValue().intValue();
        rgb[2] = blue_channel.getValue().intValue();
    }

    protected void load_previous_values() {
        red_channel.setValue(rgb[0]);
        green_channel.setValue(rgb[1]);
        blue_channel.setValue(rgb[2]);
        updateSliders();
        updateTextfields();
    }

    public void show() {
        store_previous_values();
        popup.show(node);
    }

    public void hide() {
        load_previous_values();
        updateColorPreview();
        popup.hide();
    }

    public void toggle() {
        if (popup.isShowing()) {
            hide();
        } else {
            show();
        }
    }

    @FXML
    protected void discard() {
        hide();
    }

    @FXML
    protected void save() {
        store_previous_values();
        hide();
    }
}

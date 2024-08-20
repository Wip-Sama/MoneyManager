package org.wip.moneymanager.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
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
import org.wip.moneymanager.MoneyManager;
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

    public final BooleanProperty changesSaved = new SimpleBooleanProperty(false);

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
        FXMLLoader fxmlLoader = new FXMLLoader(MoneyManager.class.getResource("components/colorpickerpopup.fxml"));
        fxmlLoader.setRoot(new BorderPane()); /* L'alternativa è extends Borderpane e setRoot(this) */
        fxmlLoader.setController(this);
        // realisticamente non dovrebbe mai fallire ma anche se lo facesse non è lui a dover gestire l'errore
        // dato che viene chiamato sempre da una classe e non fxml è quella classe che deve accollarsi l'errore
        Parent loaded = fxmlLoader.load();
        popup.getContent().add(loaded);

        /* Controlliamo se l'utente interagisce con la finestra sottostante e in caso chiusiamo il pulsante*/
        window.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, _ -> hide());

        // Me del futuro, questo codice fa schifo, i popup non dovrebbero essere usati in questo modo
        // Ero indeciso se riscriverlo come context menu o lasciarlo così
        // Ho deciso di lasciarlo così solo per vader vedere che so usare i popup
        // In questo contesto ha un sacco di problemi
        // Es: se passate ad un'altra app vi ritroverete il popup anche sopra essa
    }

    @FXML
    protected void initialize() {
        UnaryOperator<TextFormatter.Change> text_filter = change -> {
            // TODO: mentre si digita è possibile inserire infiniti 0
            // Programmi come ps hanno lasciato questo comportamento
            // Non mi piace più di tanto ma non è una priorità
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

        // Limita i textfield ad accettare solo numeri tra 0 e 255
        red_textfield.setTextFormatter(new TextFormatter<>(text_filter));
        green_textfield.setTextFormatter(new TextFormatter<>(text_filter));
        blue_textfield.setTextFormatter(new TextFormatter<>(text_filter));

        // Forza il valore della slider a essere uguale a quello del textfield
        // NumberStringConverter non sembra funzionare correttamente in questo caso, quindi resto col mio
        red_textfield.textProperty().bindBidirectional(red_channel, converter);
        green_textfield.textProperty().bindBidirectional(green_channel, converter);
        blue_textfield.textProperty().bindBidirectional(blue_channel, converter);

        red_slider.valueProperty().bindBidirectional(red_channel);
        green_slider.valueProperty().bindBidirectional(green_channel);
        blue_slider.valueProperty().bindBidirectional(blue_channel);

        // Aggiorna il colore della preview quando i valori dei channel cambiano
        red_channel.addListener(_ -> updateColorPreview());
        green_channel.addListener(_ -> updateColorPreview());
        blue_channel.addListener(_ -> updateColorPreview());

        /* Si può spostare see */
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
        // Questo metodo di aggiornare i colori non mi piace ma non ho trovato un modo migliore
        // e fa tutto ciò che deve fare
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

    public void show() {
        store_values();
        popup.show(node);
    }

    public void hide() {
        restore_previous_values();
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

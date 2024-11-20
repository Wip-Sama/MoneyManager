package org.wip.moneymanager.components;

import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.wip.moneymanager.model.Data;

import java.io.IOException;
import java.sql.SQLException;

public class ColorPickerPreset extends AnchorPane {
    @FXML
    protected Button color_preset_button;

    protected static Parent loaded;

    private final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    public final BooleanProperty selected = new SimpleBooleanProperty(false);

    private final IntegerProperty red = new SimpleIntegerProperty(0);
    private final IntegerProperty green = new SimpleIntegerProperty(0);
    private final IntegerProperty blue = new SimpleIntegerProperty(0);

    public ColorPickerPreset(int r, int g, int b) {
        this();
        red.set(r);
        green.set(g);
        blue.set(b);
    }

    public ColorPickerPreset() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/colorpickerpreset.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            loaded = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            /*Se succede qualcosa qui abbiamo sbagliato a fare il file quindi va sistemato prima di mandarlo in produzione*/
        }

        onMouseClickedProperty().set(_ -> {
            selected.set(!selected.get());
            if (selected.get()) {
                try {
                    Data.dbUser.setAccent(getRGB());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        selected.addListener((_, _, newValue) -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, newValue)
        );
    }

    public void updateColor() {
        setStyle("-fx-background-color: -fu-stroke-colors, rgb(" + red.getValue() + "," + green.getValue() + "," + blue.getValue() + ")");
    }

    @FXML
    public void initialize() {
        red.addListener(_ -> updateColor());
        green.addListener(_ -> updateColor());
        blue.addListener(_ -> updateColor());
        updateColor();
    }

    public int getRed() {
        return red.get();
    }
    public int getGreen() {
        return green.get();
    }
    public int getBlue() {
        return blue.get();
    }
    public int[] getRGB() {
        return new int[] {red.get(), green.get(), blue.get()};
    }

    public void setRed(int intero) {
        if (intero < 0) {
            this.red.set(0);
            return;
        } else if (intero > 255) {
            this.red.set(255);
            return;
        }
        this.red.set(intero);
    }
    public void setGreen (int intero) {
        if (intero < 0) {
            this.green.set(0);
            return;
        } else if (intero > 255) {
            this.green.set(255);
            return;
        }
        this.green.set(intero);
    }
    public void setBlue(int intero) {
        if (intero < 0) {
            this.blue.set(0);
            return;
        } else if (intero > 255) {
            this.blue.set(255);
            return;
        }
        this.blue.set(intero);
    }
    public void setRGB(int r, int g, int b) {
        setRed(r);
        setGreen(g);
        setBlue(b);
    }
    public void setRGB(int[] rgb) {
        setRed(rgb[0]);
        setGreen(rgb[1]);
        setBlue(rgb[2]);
    }

    public IntegerProperty redProperty() {
        return red;
    }
    public IntegerProperty greenProperty() {
        return green;
    }
    public IntegerProperty blueProperty() {
        return blue;
    }
}

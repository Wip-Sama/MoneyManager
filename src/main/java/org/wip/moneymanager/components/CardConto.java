package org.wip.moneymanager.components;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class CardConto extends AnchorPane {
    @FXML
    private BorderPane edit_pane;

    @FXML
    private BorderPane display_pane;

    @FXML
    private ToggleButton edit_pane_toggle;

    @FXML
    private Button delete_button;

    @FXML
    private ChoiceBox<String> type_field;

    public CardConto() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/cardconto.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        Rectangle clip = new Rectangle();
        clip.setArcWidth(12);
        clip.setArcHeight(12);
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        clip.setMouseTransparent(true);
        setClip(clip);
        edit_pane_toggle.selectedProperty().addListener((_, _, newValue) -> {
            // metto 160 e non edit_pane.getHeight() perché altrimenti con i bordi arrotondati non si vede bene
            double endHeight = newValue ? display_pane.getHeight() + 160 : display_pane.getHeight();
            Timeline timeline = new Timeline();
            KeyValue keyValue = new KeyValue(this.prefHeightProperty(), endHeight);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue); // Adjust duration as needed
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
        });
        type_field.getItems().addAll("Conto Corrente", "Carta di Credito", "Conto Risparmio");
        type_field.setValue("Conto Corrente");
    }
}

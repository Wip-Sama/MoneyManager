package org.wip.moneymanager.components;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.sql.Date;

public class CardConto extends AnchorPane {
    /*Controlli*/
    @FXML
    private BorderPane edit_pane;

    @FXML
    private BorderPane display_pane;

    @FXML
    private ToggleButton edit_pane_toggle;

    @FXML
    private Button delete_button;

    /*Parte di modifica*/
    @FXML
    private TextField name_field;

    @FXML
    private BalanceEditor balance_field;

    @FXML
    private ChoiceBox<String> type_field;

    @FXML
    private DatePicker creation_date_field;

    @FXML
    private Switch include_into_totals_field;

    /*Parte dei label*/
    @FXML
    private Label account_name;

    @FXML
    private Label account_type;

    @FXML
    private Label account_balance;

    @FXML
    private Label account_creation_date;

    private BooleanProperty hide_balance;

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
        /* Graphics part */
        Rectangle clip = new Rectangle();
        clip.setArcWidth(20);
        clip.setArcHeight(20);
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

        /* Edit part */
        hide_balance.addListener((_, _, newValue) -> {
            if (newValue) {
                account_balance.setText("Balance: 00 "+"Insert Default currency here");
                account_balance.setStyle("-fx-text-fill: -fu-text-2");
            } else {

                account_balance.setStyle("-fx-text-fill: -fu-text-1");
            }
            balance_field.setVisible(newValue);
        });

        account_balance.textProperty().addListener((_, _, newValue) -> {
            if (hide_balance.get()) {

            }
        });

        type_field.getItems().addAll("Conto Corrente", "Carta di Credito", "Conto Risparmio");
        type_field.setValue("Conto Corrente");
    }
}

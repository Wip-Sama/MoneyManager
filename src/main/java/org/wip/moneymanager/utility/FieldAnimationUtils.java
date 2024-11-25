package org.wip.moneymanager.utility;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.wip.moneymanager.components.BalanceEditor;
import org.wip.moneymanager.components.ComboPasswordField;

public class FieldAnimationUtils {
    //Vi ho messo dei commenti cosi chi usa questa classe (LUCA) è più facile capire
    private static final double SHAKE_DISTANCE = 2;
    private static final double SHAKE_DURATION = 0.25;

    // Metodo per creare la Timeline di animazione
    private static Timeline createShakeTimeline(javafx.scene.Node node) {
        return new Timeline(
                new KeyFrame(Duration.seconds(0), e -> node.setTranslateX(0)),
                new KeyFrame(Duration.seconds(0.05), e -> node.setTranslateX(-SHAKE_DISTANCE)),
                new KeyFrame(Duration.seconds(0.1), e -> node.setTranslateX(SHAKE_DISTANCE)),
                new KeyFrame(Duration.seconds(0.15), e -> node.setTranslateX(-SHAKE_DISTANCE)),
                new KeyFrame(Duration.seconds(0.20), e -> node.setTranslateX(SHAKE_DISTANCE)),
                new KeyFrame(Duration.seconds(SHAKE_DURATION), e -> node.setTranslateX(0))
        );
    }

    // Metodo per animare l'errore
    private static void animateError(javafx.scene.Node field) {
        if (!field.getStyleClass().contains("errore")) {
            field.getStyleClass().add("errore");
        }
        Timeline shakeTimeline = createShakeTimeline(field);
        shakeTimeline.setCycleCount(1);
        shakeTimeline.play();
    }

    // Aggiunge l'errore alla TextField
    public static void animateFieldError(TextField field) {
        animateError(field);
    }

    // Rimuove l'errore dalla TextField
    public static void removeErrorStyles(TextField field) {
        field.getStyleClass().remove("errore");
    }

    // Aggiunge l'errore alla ComboPasswordField
    public static void animateFieldError(ComboPasswordField field) {
        for (javafx.scene.Node child : field.getChildren()) {
            if (child instanceof TextField || child instanceof PasswordField) {
                animateError(child);
            }
        }
    }

    // Rimuove l'errore dalla ComboPasswordField
    public static void removeErrorStyles(ComboPasswordField field) {
        for (javafx.scene.Node child : field.getChildren()) {
            child.getStyleClass().remove("errore");
        }
    }

    // Aggiunge l'errore alla ChoiceBox
    public static void animateFieldError(ChoiceBox<?> choiceBox) {
        if (choiceBox.getSelectionModel().getSelectedItem() == null) {
            if (!choiceBox.getStyleClass().contains("errore")) {
                choiceBox.getStyleClass().add("errore");
            }
            Timeline shakeTimeline = createShakeTimeline(choiceBox);
            shakeTimeline.setCycleCount(1);
            shakeTimeline.play();
        }
    }

    // Rimuove l'errore dalla ChoiceBox
    public static void removeErrorStyles(ChoiceBox<?> choiceBox) {
        choiceBox.getStyleClass().remove("errore");
    }

    // Aggiunge l'errore al DatePicker
    public static void animateFieldError(DatePicker datePicker) {
        datePicker.getStyleClass().add("errore-bordi");
        Timeline shakeTimeline = createShakeTimeline(datePicker);
        shakeTimeline.setCycleCount(1);
        shakeTimeline.play();
    }

    // Rimuove l'errore dal DatePicker
    public static void removeErrorStyles(DatePicker datePicker) {
        datePicker.getStyleClass().remove("errore-bordi");
    }

    // Aggiunge l'errore al BalanceEditor
    public static void animateFieldError(BalanceEditor field) {
        animateError(field);
    }

    // Rimuove l'errore dal BalanceEditor
    public static void removeErrorStyles(BalanceEditor field) {
        field.getStyleClass().remove("errore");
    }
}

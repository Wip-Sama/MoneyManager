package org.wip.moneymanager.utility;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.wip.moneymanager.components.BalanceEditor;
import org.wip.moneymanager.components.ComboPasswordField;

public class FieldAnimationUtils {
    private static final double SHAKE_DISTANCE = 2;
    private static final double SHAKE_DURATION = 0.25;

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

    private static void animateError(javafx.scene.Node field, String errorStyleClass) {
        if (!field.getStyleClass().contains(errorStyleClass)) {
            field.getStyleClass().add(errorStyleClass);
        }
        Timeline shakeTimeline = createShakeTimeline(field);
        shakeTimeline.setCycleCount(1);
        shakeTimeline.play();
    }

    private static void removeErrorStyles(javafx.scene.Node field, String errorStyleClass) {
        field.getStyleClass().remove(errorStyleClass);
    }

    public static void animateFieldError(TextField field) {
        animateError(field, "errore");
    }

    public static void removeErrorStyles(TextField field) {
        removeErrorStyles(field, "errore");
    }

    public static void animateFieldError(ComboPasswordField field) {
        for (javafx.scene.Node child : field.getChildren()) {
            if (child instanceof TextField || child instanceof PasswordField) {
                animateError(child, "errore");
            }
        }
    }

    public static void removeErrorStyles(ComboPasswordField field) {
        for (javafx.scene.Node child : field.getChildren()) {
            removeErrorStyles(child, "errore");
        }
    }

    public static void animateFieldError(ComboBox<?> comboBox) {
        animateError(comboBox, "errore");
    }

    public static void removeErrorStyles(ComboBox<?> comboBox) {
        removeErrorStyles(comboBox, "errore");
    }

    public static void animateFieldError(DatePicker datePicker) {
        animateError(datePicker, "errore-bordi");
    }

    public static void removeErrorStyles(DatePicker datePicker) {
        removeErrorStyles(datePicker, "errore-bordi");
    }

    public static void animateFieldError(BalanceEditor field) {
        animateError(field, "errore");
    }

    public static void removeErrorStyles(BalanceEditor field) {
        removeErrorStyles(field, "errore");
    }
}

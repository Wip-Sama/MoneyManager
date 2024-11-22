package org.wip.moneymanager.utility;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.wip.moneymanager.components.ComboPasswordField;

public class FieldAnimationUtils {

    public static void animateFieldError(TextField field) {
        if (!field.getStyleClass().contains("errore")) {
            field.getStyleClass().add("errore");
        }
        Timeline shakeTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> field.setTranslateX(0)),
                new KeyFrame(Duration.seconds(0.05), e -> field.setTranslateX(-2)),
                new KeyFrame(Duration.seconds(0.1), e -> field.setTranslateX(2)),
                new KeyFrame(Duration.seconds(0.15), e -> field.setTranslateX(-2)),
                new KeyFrame(Duration.seconds(0.20), e -> field.setTranslateX(2)),
                new KeyFrame(Duration.seconds(0.25), e -> field.setTranslateX(0))
        );
        shakeTimeline.setCycleCount(1);
        shakeTimeline.play();
    }

    public static void animateFieldError(ComboPasswordField field) {
        for (javafx.scene.Node child : field.getChildren()) {
            if (child instanceof TextField || child instanceof PasswordField) {
                if (!child.getStyleClass().contains("errore")) {
                    child.getStyleClass().add("errore");
                }
            }
        }

        Timeline shakeTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.05), e -> field.setTranslateX(-2)),
                new KeyFrame(Duration.seconds(0.1), e -> field.setTranslateX(2)),
                new KeyFrame(Duration.seconds(0.15), e -> field.setTranslateX(-2)),
                new KeyFrame(Duration.seconds(0.20), e -> field.setTranslateX(2)),
                new KeyFrame(Duration.seconds(0.25), e -> field.setTranslateX(0))
        );
        shakeTimeline.setCycleCount(1);
        shakeTimeline.play();
    }

    public static void removeErrorStyles(TextField field) {
        field.getStyleClass().remove("errore");
    }

    public static void removeErrorStyles(ComboPasswordField field) {
        for (javafx.scene.Node child : field.getChildren()) {
            child.getStyleClass().remove("errore");
        }
    }
}

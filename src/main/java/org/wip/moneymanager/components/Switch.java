package org.wip.moneymanager.components;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Switch extends AnchorPane {
    @FXML
    private Pane switch_state_indicator;

    @FXML
    private Label text_off;

    @FXML
    private Label text_on;

    private final BooleanProperty state = new SimpleBooleanProperty(false);

    public Switch() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/switch.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Switch(boolean state) {
        this();
        this.state.set(state);
    }

    public boolean getState() {
        return state.get();
    }

    public void setState(boolean state) {
        this.state.set(state);
        updateIndicatorPosition();
    }

    public void updateState(boolean state) {
        this.state.set(state);
    }

    public BooleanProperty stateProperty() {
        return state;
    }

    private void updateIndicatorPosition() {
        if (state.get()) {
            AnchorPane.setRightAnchor(switch_state_indicator, 0.0);
            AnchorPane.setLeftAnchor(switch_state_indicator, null);
        } else {
            AnchorPane.setLeftAnchor(switch_state_indicator, 0.0);
            AnchorPane.setRightAnchor(switch_state_indicator, null);
        }
    }

    public void initialize() {
        state.addListener((_, _, newValue) -> {
            double endPosition;
            if (getRightAnchor(switch_state_indicator) == null) {
                endPosition = newValue ? getWidth() - switch_state_indicator.getWidth() : 0.0;
            } else {
                endPosition = newValue ? 0.0 : - getWidth() + switch_state_indicator.getWidth();
            }
            Timeline timeline = new Timeline();
            KeyValue keyValue = new KeyValue(switch_state_indicator.translateXProperty(), endPosition);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(100), keyValue); // Adjust duration as needed
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
        });
        onMouseClickedProperty().set(_ -> state.set(!state.get()));
    }

    public void reset() {
        setState(false);
    }
}
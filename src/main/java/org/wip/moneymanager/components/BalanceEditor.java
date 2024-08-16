package org.wip.moneymanager.components;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.function.UnaryOperator;

public class BalanceEditor extends HBox {
    @FXML
    private TextField balance_field;

    @FXML
    private ChoiceBox<String> currency_field;

    private final PseudoClass FOCUSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("focused");

    public BalanceEditor() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/balanceeditor.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void initialize() {
        UnaryOperator<TextFormatter.Change> text_filter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                change.setText("0");
                return change;
            }
            try {
                if (newText.length() > 1) {
                    newText = newText.replaceAll("^0+(?!$)", "");

                }
                Double.parseDouble(newText);
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        };

        balance_field.setTextFormatter(new TextFormatter<>(text_filter));
        balance_field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                pseudoClassStateChanged(FOCUSED_PSEUDO_CLASS, true);
            } else {
                pseudoClassStateChanged(FOCUSED_PSEUDO_CLASS, false);
            }
        });
        //TODO: Sostituire con entry dal org.wip.moneymanager.database
        currency_field.getItems().addAll("USD", "EUR", "RUB");
        currency_field.setValue("EUR");
    }
}

package org.wip.moneymanager.components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import org.wip.moneymanager.model.Data;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.UnaryOperator;

public class BalanceEditor extends HBox {
    @FXML
    private TextField balance_field;

    @FXML
    private ChoiceBox<String> currency_field;

    private final PseudoClass FOCUSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("focused");
    private final DoubleProperty balance = new SimpleDoubleProperty(0);

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

    public void initialize() throws ExecutionException, InterruptedException {
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
        balance_field.focusedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                pseudoClassStateChanged(FOCUSED_PSEUDO_CLASS, true);
            } else {
                pseudoClassStateChanged(FOCUSED_PSEUDO_CLASS, false);
            }
        });

        Data.subscribe_busy();
        Task<List<String>> currencies = Data.mmDatabase.getAllCurrencyName();
        currencies.run();
        currencies.get().stream().sorted().forEach(currency -> currency_field.getItems().add(currency.toUpperCase()));
        currency_field.setValue(Data.dbUser.main_currencyProperty().get().toUpperCase());
        Data.unsubscribe_busy();
        currency_field.setValue("EUR");
    }

    public void setBalance(double balance) {
        balance_field.setText(String.valueOf(balance));
    }

    public double getBalance() {
        return Double.parseDouble(balance_field.getText());
    }

    public void setCurrency(String currency) {
        currency_field.setValue(currency);
    }

    public String getCurrency() {
        return currency_field.getValue();
    }
}

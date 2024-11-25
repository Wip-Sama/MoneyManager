package org.wip.moneymanager.components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.wip.moneymanager.model.Data;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.UnaryOperator;

public class BalanceEditor extends HBox {
    @FXML
    private TextField balance_field;

    @FXML
    private Separator sep;

    @FXML
    private ChoiceBox<String> currency_field;

    private final PseudoClass FOCUSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("focused");
   private final DoubleProperty balance = new SimpleDoubleProperty(0);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public BalanceEditor() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/balanceeditor.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void initialize() {
        Data.esm.register(executorService);
        UnaryOperator<TextFormatter.Change> text_filter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                change.setText("");
                return change;
            }
            try {
                if (newText.length() > 1) {
                    newText = newText.replaceAll("^+(?!$)", "");
                }
                Double.parseDouble(newText);
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        };

        balance_field.setTextFormatter(new TextFormatter<>(text_filter));
        balance_field.focusedProperty().addListener((_, _, newValue) -> pseudoClassStateChanged(FOCUSED_PSEUDO_CLASS, newValue));
        Task<List<String>> currencies = Data.mmDatabase.getAllCurrencyName();
        executorService.submit(currencies);
        currencies.setOnSucceeded(_ -> currencies.getValue().stream().sorted().forEach(currency -> currency_field.getItems().add(currency.toUpperCase())));

        currency_field.setValue(Data.dbUser.main_currencyProperty().get().toUpperCase());
    }

    public void only_choice_box() {
        balance_field.setVisible(false);
        balance_field.setManaged(false);
        sep.setVisible(false);
        sep.setManaged(false);
        HBox.setHgrow(currency_field, Priority.ALWAYS);
        currency_field.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
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

    public ReadOnlyObjectProperty<String> currencyProperty() {
        return currency_field.valueProperty();
    }

    public void reset() {
        balance_field.setText("");
        currency_field.setValue(Data.dbUser.main_currencyProperty().get().toUpperCase());
    }

    public String getText(){
        return balance_field.getText();
    }

    public DoubleProperty balanceProperty() {
        return balance;
    }

    public TextField getTextField() {
        return balance_field;
    }
}

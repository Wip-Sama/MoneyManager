package org.wip.moneymanager.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTransactions extends HBox {

    @FXML
    private Button arrowLeftDate;

    @FXML
    private Button arrowRightDate;

    @FXML
    private Label labelDate;

    private LocalDate currentDate;
    private DateFormatType currentFormat;
    private DateTimeFormatter formatterMonth = DateTimeFormatter.ofPattern("MMM yyyy");
    private DateTimeFormatter formatterYear = DateTimeFormatter.ofPattern("yyyy");

    public enum DateFormatType {
        DAILY, MONTHLY
    }

    public DateTransactions() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/dateTransactions.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Imposta la data corrente
        currentDate = LocalDate.now();
        currentFormat = DateFormatType.DAILY; // Formato predefinito
        updateLabelDate();

        // Listener per cambiare data/mese
        arrowLeftDate.setOnAction(event -> updateDate(-1));
        arrowRightDate.setOnAction(event -> updateDate(1));
    }

    public void setDateFormat(DateFormatType format) {
        currentFormat = format;
        updateLabelDate();
    }

    public String getSelectedDate() {
        switch (currentFormat) {
            case DAILY:
                return currentDate.format(formatterMonth); // Giorno, mese, anno
            case MONTHLY:
                return currentDate.format(formatterYear); // Mese, anno
            default:
                return "";
        }
    }

    private void updateDate(int increment) {
        // Modifica la data in base al formato attivo
        switch (currentFormat) {
            case DAILY:
                currentDate = currentDate.plusMonths(increment);
                break;
            case MONTHLY:
                currentDate = currentDate.plusYears(increment);
                break;

        }
        updateLabelDate();
    }

    private void updateLabelDate() {
        labelDate.setText(getSelectedDate());

    }


}

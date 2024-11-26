package org.wip.moneymanager.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import java.io.IOException;
import java.time.LocalDate;
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

    public enum DateFormatType {
        DAILY, MONTHLY, CALENDAR
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
        currentFormat = DateFormatType.DAILY;  // Inizialmente settiamo il formato come giornaliero
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
                return currentDate.format(DateTimeFormatter.ofPattern("MMM yyyy")); // Solo mese e anno
            case MONTHLY:
                return currentDate.format(DateTimeFormatter.ofPattern("yyyy")); // Solo anno
            case CALENDAR:
                return currentDate.format(DateTimeFormatter.ofPattern("MMM yyyy")); // Solo mese e anno
            default:
                return "";
        }
    }

    private void updateDate(int increment) {
        // Controlla il formato attivo e aggiorna la data in base a quello
        switch (currentFormat) {
            case DAILY:
            case CALENDAR:


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

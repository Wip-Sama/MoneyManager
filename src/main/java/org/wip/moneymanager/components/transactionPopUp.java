package org.wip.moneymanager.components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;


public class transactionPopUp {

    @FXML
    private Button entrateButton, speseButton, trasferimentiButton, saveButton, cancelButton;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField importoField, categoriaField, contoField, tagsField;

    // Metodo per gestire il click sui pulsanti laterali
    @FXML
    private void handleButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        entrateButton.getStyleClass().remove("selected");
        speseButton.getStyleClass().remove("selected");
        trasferimentiButton.getStyleClass().remove("selected");
        clickedButton.getStyleClass().add("selected");
    }

    // Metodo per gestire il click sul pulsante "Salva"
    @FXML
    private void handleSaveButtonClick() {
        //TODO: Implementa la logica di salvataggio qui
    }

    // Metodo per gestire il click sul pulsante "Annulla"
    @FXML
    private void handleCancelButtonClick() {
        //TODO: Implementa la logica di annullamento qui
    }
}


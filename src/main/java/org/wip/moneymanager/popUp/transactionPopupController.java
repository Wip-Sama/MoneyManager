package org.wip.moneymanager.popUp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class transactionPopupController {

    @FXML
    private Button inButton;
    @FXML
    private Button speseButton;
    @FXML
    private Button trasferimentiButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    @FXML
    private void initialize() {
        // Imposta gli eventi per i pulsanti
        inButton.setOnAction(event -> handleInButton());
        speseButton.setOnAction(event -> handleSpeseButton());
        trasferimentiButton.setOnAction(event -> handleTrasferimentiButton());
        saveButton.setOnAction(event -> handleSaveButton());
        cancelButton.setOnAction(event -> handleCancelButton());
    }
    @FXML
    private void handleInButton() {
        System.out.println("In button clicked");
        inButton.getStyleClass().add("selected");
    }
    @FXML
    private void handleSpeseButton() {
        System.out.println("Spese button clicked");
        speseButton.getStyleClass().add("selected");
    }
    @FXML
    private void handleTrasferimentiButton() {
        System.out.println("Trasferimenti button clicked");
        trasferimentiButton.getStyleClass().add("selected");
    }
    @FXML
    private void handleSaveButton() {
        System.out.println("Save button clicked");
        // Logica per salvare i dati
    }
    @FXML
    private void handleCancelButton() {
        System.out.println("Cancel button clicked");
        // Logica per annullare l'operazione
    }
}

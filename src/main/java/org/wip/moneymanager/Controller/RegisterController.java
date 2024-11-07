package org.wip.moneymanager.Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.wip.moneymanager.components.ComboPasswordField;
import org.wip.moneymanager.model.MMDatabase; // Import your MMDatabase class
import org.wip.moneymanager.View.SceneHandler; // Import the SceneHandler class

public class RegisterController {

    @FXML
    private Label labelLogin;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginChangeButton;

    @FXML
    private Label labelPassword;

    @FXML
    private Label labelUsername;

    @FXML
    private ComboPasswordField passwordFieldRegister;

    @FXML
    private Button registerButton;

    @FXML
    private TextField usernameFieldRegister;

    private final MMDatabase db = MMDatabase.getInstance();

    @FXML
    void ChangeToLogin(ActionEvent event) {
        // Use SceneHandler to show the login screen
        SceneHandler.getInstance((Stage) loginChangeButton.getScene().getWindow()).showLoginScreen();
    }

    @FXML
    void RegisterUser(ActionEvent event) {
        String username = usernameFieldRegister.getText();
        String password = passwordFieldRegister.password.get();

        // Validate input
        if (username.isEmpty() && (password == null || password.isEmpty())) {
            showError("Username and Password cannot be empty.");
            animateFieldError(usernameFieldRegister);
            animateFieldError(passwordFieldRegister);
            return;
        } else if (username.isEmpty()) {
            showError("Username cannot be empty.");
            animateFieldError(usernameFieldRegister);
            return;
        } else if (password.isEmpty()) {
            showError("Password cannot be empty.");
            animateFieldError(passwordFieldRegister);
            return;
        }

        // Create a task to register the user
        Task<Boolean> registrationTask = db.createUser(username, password);

        registrationTask.setOnSucceeded(e -> {
            boolean isRegistered = registrationTask.getValue(); // Get the result of the task
            if (isRegistered) {
                errorLabel.setText("Registration successful!");
                errorLabel.setOpacity(1);
                // Optionally navigate to the login screen or perform other actions here
                SceneHandler.getInstance((Stage) registerButton.getScene().getWindow()).showLoginScreen();
            } else {
                errorLabel.setText("Registration failed!");
                errorLabel.setOpacity(1);
            }
        });

        registrationTask.setOnFailed(e -> {
            errorLabel.setText("An error occurred during registration.");
            errorLabel.setOpacity(1);
        });

        // Start the task in a new thread
        new Thread(registrationTask).start();
    }


    // Metodo per applicare animazione sul bordo rosso dei campi di input
    private void animateFieldError(javafx.scene.control.TextInputControl field) {
        field.setStyle("-fx-background-color: transparent;");  // Imposta il bordo iniziale trasparente
        Timeline shakeTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> field.setStyle("-fx-background-color: red, red, -fu-foreground-rest; ")),
                new KeyFrame(Duration.seconds(0.05), e -> field.setTranslateX(-2)),
                new KeyFrame(Duration.seconds(0.1), e -> field.setTranslateX(2)),
                new KeyFrame(Duration.seconds(0.15), e -> field.setTranslateX(-2)),
                new KeyFrame(Duration.seconds(0.20), e -> field.setTranslateX(2)),
                new KeyFrame(Duration.seconds(0.25), e -> field.setTranslateX(0))
        );
        shakeTimeline.setCycleCount(1);
        shakeTimeline.play();
    }

    private void animateFieldError(ComboPasswordField field) {

        for (javafx.scene.Node child : field.getChildren()) {
            if (child instanceof TextField) {
                ((TextField) child).setStyle("-fx-background-color: red, red, -fu-foreground-rest; ");
            } else if (child instanceof PasswordField) {
                ((PasswordField) child).setStyle("-fx-background-color: red, red, -fu-foreground-rest; ");
            }
        }

        field.setStyle("-fx-background-color: transparent;");  // Imposta il bordo iniziale trasparente
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

    // Metodo per visualizzare il messaggio di errore con animazione
    private void showError(String message) {
        errorLabel.setText(message);

        // Animazione di "fade in" per il messaggio di errore
        Timeline fadeInTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> errorLabel.setOpacity(0)),
                new KeyFrame(Duration.seconds(0.2), e -> errorLabel.setOpacity(1))
        );
        fadeInTimeline.setCycleCount(1);
        fadeInTimeline.play();
    }

    @FXML
    private void initialize() {
        // Listener per il campo username
        usernameFieldRegister.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                removeErrorStyles(usernameFieldRegister);  // Rimuove gli stili di errore
                errorLabel.setOpacity(0);  // Nasconde il messaggio di errore
            }
        });

        // Listener per il campo password
        passwordFieldRegister.password.addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                removeErrorStyles(passwordFieldRegister);  // Rimuove gli stili di errore
                errorLabel.setOpacity(0);  // Nasconde il messaggio di errore
            }
        });
    }

    // Metodo per rimuovere gli stili di errore dai campi di input
    private void removeErrorStyles(javafx.scene.control.TextInputControl field) {
        field.setStyle("-fx-background-color: -fu-stroke-rest, -fu-stroke-rest, -fu-foreground-rest");  // Rimuove il bordo rosso
    }


    private void removeErrorStyles(ComboPasswordField field) {

        for (javafx.scene.Node child : field.getChildren()) {
            if (child instanceof TextField) {
                ((TextField) child).setStyle("-fx-background-color: -fu-stroke-rest, -fu-stroke-rest, -fu-foreground-rest");
            } else if (child instanceof PasswordField) {
                ((PasswordField) child).setStyle("-fx-background-color: -fu-stroke-rest, -fu-stroke-rest, -fu-foreground-rest");
            }
        }
    }
}

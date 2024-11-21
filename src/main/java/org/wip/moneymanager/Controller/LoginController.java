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
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.components.ComboPasswordField;
import org.wip.moneymanager.model.DBObjects.dbUser;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.MMDatabase;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginController {

    @FXML
    private Label LabelRegister;

    @FXML
    private Label errorLabel;

    @FXML
    private Label labelPassword;

    @FXML
    private Label labelUsername;

    @FXML
    private Button loginButton;

    @FXML
    private ComboPasswordField passwordField;

    @FXML
    private Button registerButton;

    @FXML
    private TextField usernameField;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    @FXML
    void ChangeToRegister(ActionEvent event) {
        try {
            SceneHandler loginScreen = SceneHandler.getInstance((Stage) registerButton.getScene().getWindow());
            loginScreen.showRegisterScreen(); // Passa alla schermata di registrazione
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void CheckLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.password.get();

        if (username.isEmpty() && (password == null || password.isEmpty())) {
            showError("login.error.missing");
            animateFieldError(usernameField);
            animateFieldError(passwordField);
        } else if (username.isEmpty()) {
            showError("login.error.username");
            animateFieldError(usernameField);
        } else if (password == null || password.isEmpty()) {
            showError("login.error.password");
            animateFieldError(passwordField);
        } else {
            Task<Boolean> loginTask = Data.mmDatabase.checkPassword(username, password);

            // Invio il task al pool
            executorService.submit(loginTask);

            loginTask.setOnSucceeded(e -> {
                boolean isAuthenticated = loginTask.getValue();
                if (isAuthenticated) {
                    Task<dbUser> user = Data.mmDatabase.getUser(username);

                    // Invio il secondo task al pool
                    executorService.submit(user);

                    user.setOnSucceeded(_ -> {
                        Data.dbUser = user.getValue();
                        SceneHandler.getInstance((Stage) registerButton.getScene().getWindow()).startMoneyManager();

                        // Chiudo il pool quando tutti i task sono completati
                        executorService.shutdown();
                    });

                    user.setOnFailed(_ -> {
                        showError("login.error.generic");
                        executorService.shutdown(); // Anche in caso di errore
                    });
                } else {
                    showError("login.error.invalid");
                    animateFieldError(usernameField);
                    animateFieldError(passwordField);
                    executorService.shutdown();
                }
            });

            loginTask.setOnFailed(e -> {
                showError("login.error.generic");
                animateFieldError(usernameField);
                animateFieldError(passwordField);
                executorService.shutdown();
            });
        }
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


    private void showError(String message) {
        errorLabel.textProperty().bind(Data.lsp.lsb(message));
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
        Data.esm.register(executorService);
        loginButton.textProperty().bind(Data.lsp.lsb("login.logintext"));
        labelUsername.textProperty().bind(Data.lsp.lsb("login.username"));
        labelPassword.textProperty().bind(Data.lsp.lsb("login.password"));
        registerButton.textProperty().bind(Data.lsp.lsb("login.register"));
        LabelRegister.textProperty().bind(Data.lsp.lsb("login.registerlabel"));
        errorLabel.textProperty().bind(Data.lsp.lsb("login.error"));

        // Listener per il campo username
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                removeErrorStyles(usernameField);  // Rimuove gli stili di errore
                errorLabel.setOpacity(0);  // Nasconde il messaggio di errore
            }
        });

        // Listener per il campo password
        passwordField.password.addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                removeErrorStyles(passwordField);
                errorLabel.setOpacity(0);  // Nascondi l'alert quando l'utente inizia a scrivere
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

package org.wip.moneymanager.Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.wip.moneymanager.components.ComboPasswordField;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.MMDatabase;
import org.wip.moneymanager.View.SceneHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private Label labelConfirmPassword;

    @FXML
    private ComboPasswordField passwordFieldRegister;

    @FXML
    private ComboPasswordField passwordConfirmPassword;

    @FXML
    private Button registerButton;

    @FXML
    private TextField usernameFieldRegister;

    private final MMDatabase db = MMDatabase.getInstance();

    @FXML
    void ChangeToLogin(ActionEvent event) {
        SceneHandler.getInstance((Stage) loginChangeButton.getScene().getWindow()).showLoginScreen();
    }

    @FXML
    void RegisterUser(ActionEvent event) {
        String username = usernameFieldRegister.getText();
        String password = passwordFieldRegister.password.get();
        String confirmPassword = passwordConfirmPassword.password.get();

        if (username.isEmpty() && (password == null || password.isEmpty()) && (confirmPassword == null || confirmPassword.isEmpty())) {
            showError("register.error.missing");
            animateFieldError(usernameFieldRegister);
            animateFieldError(passwordFieldRegister);
            animateFieldError(passwordConfirmPassword);

        } else if (username.isEmpty() && (password == null || password.isEmpty())) {
            showError("register.error.missing");
            animateFieldError(usernameFieldRegister);
            animateFieldError(passwordFieldRegister);
            return;
        } else if (username.isEmpty() && (confirmPassword == null || confirmPassword.isEmpty())) {
            showError("register.error.missing");
            animateFieldError(usernameFieldRegister);
            animateFieldError(passwordConfirmPassword);
            return;
        } else if (username.isEmpty()) {
            showError("register.error.username");
            animateFieldError(usernameFieldRegister);
            return;
        } else if (password == null || password.isEmpty()) {
            showError("register.error.password");
            animateFieldError(passwordFieldRegister);
            return;
        } else if (confirmPassword == null || confirmPassword.isEmpty()) {
            showError("register.error.confirm");
            animateFieldError(passwordConfirmPassword);
            return;
        } else if (!password.equals(confirmPassword)) {
            showError("register.error.mismatch");
            animateFieldError(passwordFieldRegister);
            animateFieldError(passwordConfirmPassword);
            return;
        } else {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Data.esm.register(executorService);

            Task<Boolean> existTask = Data.mmDatabase.userExists(username);


            existTask.setOnSucceeded(existEvent -> {  // Cambio nome del parametro
                Boolean exist = existTask.getValue();
                if (exist != null && !exist) {
                    // Utente non esiste, procedi con la registrazione
                    Task<Boolean> registrationTask = Data.mmDatabase.createUser(username, password);
                    registrationTask.setOnSucceeded(registrationEvent -> {  // Cambio nome del parametro
                        Boolean isRegistered = registrationTask.getValue();
                        if (isRegistered != null && isRegistered) {
                            errorLabel.setText(Data.lsp.lsb("register.success").get());
                            errorLabel.setTextFill(Color.GREEN);
                            errorLabel.setOpacity(1);
                            SceneHandler.getInstance((Stage) registerButton.getScene().getWindow()).showLoginScreen();
                        } else {
                            showError("register.error.generic");
                        }
                        executorService.shutdown();
                    });

                    registrationTask.setOnFailed(registrationEvent -> {  // Cambio nome del parametro
                        showError("register.error.generic");
                        executorService.shutdown();
                    });

                    executorService.submit(registrationTask);
                } else {
                    // Utente già esistente
                    showError("register.error.exists");
                    executorService.shutdown();
                }
            });

            existTask.setOnFailed(existEvent -> {  // Cambio nome del parametro
                showError("register.error.generic");
                executorService.shutdown();
            });


            executorService.submit(existTask);


        }
    }

    private void animateFieldError(javafx.scene.control.TextInputControl field) {
        field.setStyle("-fx-background-color: transparent;");
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
                ((TextField) child).setStyle("-fx-background-color: red, red, -fu-foreground-rest;");
            } else if (child instanceof PasswordField) {
                ((PasswordField) child).setStyle("-fx-background-color: red, red, -fu-foreground-rest;");
            }
        }

        field.setStyle("-fx-background-color: transparent;");
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
        Timeline fadeInTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> errorLabel.setOpacity(0)),
                new KeyFrame(Duration.seconds(0.2), e -> errorLabel.setOpacity(1))
        );
        fadeInTimeline.setCycleCount(1);
        fadeInTimeline.play();
    }

    @FXML
    private void initialize() {
        labelLogin.textProperty().bind(Data.lsp.lsb("register.loginlabel"));
        labelUsername.textProperty().bind(Data.lsp.lsb("register.username"));
        labelPassword.textProperty().bind(Data.lsp.lsb("register.password"));
        labelConfirmPassword.textProperty().bind(Data.lsp.lsb("register.confirmpassword"));
        registerButton.textProperty().bind(Data.lsp.lsb("register.registertext"));
        loginChangeButton.textProperty().bind(Data.lsp.lsb("register.changetologin"));

        usernameFieldRegister.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                removeErrorStyles(usernameFieldRegister);
                errorLabel.setOpacity(0);
            }
        });

        passwordFieldRegister.password.addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                removeErrorStyles(passwordFieldRegister);
                errorLabel.setOpacity(0);
            }
        });

        passwordConfirmPassword.password.addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                removeErrorStyles(passwordConfirmPassword);
                errorLabel.setOpacity(0);
            }
        });
    }

    private void removeErrorStyles(javafx.scene.control.TextInputControl field) {
        field.setStyle("-fx-background-color: -fu-stroke-rest, -fu-stroke-rest, -fu-foreground-rest");
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

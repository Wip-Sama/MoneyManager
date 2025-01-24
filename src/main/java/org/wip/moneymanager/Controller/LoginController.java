package org.wip.moneymanager.Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.components.ComboPasswordField;
import org.wip.moneymanager.model.DBObjects.dbUser;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.utility.FieldAnimationUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
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

        Map<Node, String> validationErrors = new LinkedHashMap<>();

        if (username.isEmpty() && (password == null || password.isEmpty())) {
            validationErrors.put(usernameField, "login.error.missing");
            validationErrors.put(passwordField, "login.error.missing");
        } else {
            if (username.isEmpty()) {
                validationErrors.put(usernameField, "login.error.username");
            }
            if (password == null || password.isEmpty()) {
                validationErrors.put(passwordField, "login.error.password");
            }
        }

        handleValidationErrors(validationErrors);
        if (!validationErrors.isEmpty()) return;

        Task<Boolean> loginTask = Data.mmDatabase.checkPassword(username, password);
        executorService.submit(loginTask);

        loginTask.setOnSucceeded(e -> {
            boolean isAuthenticated = loginTask.getValue();
            if (isAuthenticated) {
                handleSuccessfulLogin(username);
            } else {
                handleFailedLogin();
            }
        });

        loginTask.setOnFailed(e -> {
            showError("login.error.generic");
            animateErrorFields(usernameField, passwordField);
        });
    }

    private void handleSuccessfulLogin(String username) {
        Task<dbUser> userTask = Data.mmDatabase.getUser(username);
        executorService.submit(userTask);

        userTask.setOnSucceeded(e -> {
            Data.dbUser = userTask.getValue();
            SceneHandler.getInstance((Stage) loginButton.getScene().getWindow()).startMoneyManager();
        });

        userTask.setOnFailed(e -> {
            showError("login.error.generic");
        });
    }

    private void handleFailedLogin() {
        showError("login.error.invalid");
        animateErrorFields(usernameField, passwordField);
    }

    public static void animateErrorFields(Object... fields) {
        for (Object field : fields) {
            if (field instanceof TextField) {
                FieldAnimationUtils.animateFieldError((TextField) field);
            } else if (field instanceof ComboPasswordField) {
                FieldAnimationUtils.animateFieldError((ComboPasswordField) field);
            } else {
                throw new IllegalArgumentException("Unsupported field type: " + field.getClass().getName());
            }
        }
    }

    private void handleValidationErrors(Map<Node, String> validationErrors) {
        if (!validationErrors.isEmpty()) {
            validationErrors.forEach((field, errorKey) -> {
                showError(errorKey);
                if (field instanceof TextField) {
                    FieldAnimationUtils.animateFieldError((TextField) field);
                } else if (field instanceof ComboPasswordField) {
                    FieldAnimationUtils.animateFieldError((ComboPasswordField) field);
                }
            });
        }
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
        Data.esm.register(executorService);

        FieldAnimationUtils.disableContextMenu(
                usernameField
        );

        loginButton.textProperty().bind(Data.lsp.lsb("login.logintext"));
        labelUsername.textProperty().bind(Data.lsp.lsb("login.username"));
        labelPassword.textProperty().bind(Data.lsp.lsb("login.password"));
        registerButton.textProperty().bind(Data.lsp.lsb("login.register"));
        LabelRegister.textProperty().bind(Data.lsp.lsb("login.registerlabel"));
        errorLabel.textProperty().bind(Data.lsp.lsb("login.error"));

        usernameField.setOnKeyPressed(event -> {
            if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                if(!(passwordField.password.get() == null || passwordField.password.get().isEmpty())){
                    loginButton.fire();
                } else {
                passwordField.requestFocusOnPassword();
            }
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                loginButton.fire();
            }
        });

        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                FieldAnimationUtils.removeErrorStyles(usernameField);
                errorLabel.setOpacity(0);
            }
        });

        passwordField.password.addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                FieldAnimationUtils.removeErrorStyles(passwordField);
                errorLabel.setOpacity(0);
            }
        });
    }

}

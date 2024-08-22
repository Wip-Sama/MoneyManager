package org.wip.moneymanager;

import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.MMDatabase;
import org.wip.moneymanager.model.types.Theme;
import org.wip.moneymanager.model.types.User;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MoneyManagerController {
    @FXML
    private ToggleButton accounts;

    @FXML
    private ToggleButton settings;

    @FXML
    private ToggleButton statistics;

    @FXML
    private ToggleButton transactions;

    @FXML
    private BorderPane change_pane;

    @FXML
    private ProgressBar busy_indicator;

    @FXML
    public void initialize() throws ExecutionException, InterruptedException {
        // TODO: da rimuovere dopo che avremo fatto la schermata di login/register
        Task<User> t = MMDatabase.getInstance().getUser(Data.username);
        t.run();
        Data.user = t.get();

        busy_indicator.setVisible(false);
        Data.busyProperty().addListener((_, _, newValue) -> {
            if (newValue.intValue() > 0) {
                show_busy_indicator();
            } else {
                hide_busy_indicator();
            }
        });

        accounts.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                FXMLLoader fxmlLoader = new FXMLLoader(MoneyManager.class.getResource("pages/Accounts.fxml"));

                try {
                    Parent root = fxmlLoader.load();
                    change_pane.setCenter(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        settings.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                FXMLLoader fxmlLoader = new FXMLLoader(MoneyManager.class.getResource("pages/Settings.fxml"));
                try {
                    Parent root = fxmlLoader.load();
                    change_pane.setCenter(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        statistics.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                Data.localizationService.setSelectedLanguage("it");
            } else {
                Data.localizationService.setSelectedLanguage("en");
            }
        });

        Data.user.themeProperty().addListener((_, _, newValue) -> {
            Scene scene = accounts.getScene();
            switch (newValue) {
                case DARK, SYSTEM:
                    scene.getStylesheets().remove("style-light.css");
                    scene.getStylesheets().add("style-dark.css");
                    break;
                case LIGHT:
                    scene.getStylesheets().remove("style-dark.css");
                    scene.getStylesheets().add("style-light.css");
                    break;
            }
        });

        Data.user.accentProperty().addListener((_, _, newValue) -> {
            Scene scene = accounts.getScene();
            scene.getRoot().setStyle("-fu-accent: " + newValue.getHex() + ";");
        });

        // il pulsante non è inizializzato durante questa fase
        // quindi per assicurarci che il tutto parta con il colore giusto dobbiamo fare sta roba
        // e tutte quelle dopo
        accounts.sceneProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                // Alternativa:
                // Non funzioan per System
                // newValue.getStylesheets().add("style-"+Data.user.themeProperty().get().toString().toLowerCase()+".css");
                if (Data.user.themeProperty().get() == Theme.LIGHT) {
                    newValue.getStylesheets().add("style-light.css");
                } else {
                    newValue.getStylesheets().add("style-dark.css");
                }
                newValue.getRoot().setStyle("-fu-accent: " + Data.user.accentProperty().get().getHex() + ";");
                Data.lsp.setSelectedLanguage(Data.user.languageProperty().get());
            }
        });
    }

    public void show_busy_indicator() {
        busy_indicator.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), busy_indicator);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void hide_busy_indicator() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), busy_indicator);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(_ -> busy_indicator.setVisible(false));
        fadeOut.play();
    }

}
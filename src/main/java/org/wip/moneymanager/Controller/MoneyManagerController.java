package org.wip.moneymanager.Controller;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.UserDatabase;
import org.wip.moneymanager.model.types.HomeScreen;
import org.wip.moneymanager.model.types.Theme;
import org.wip.moneymanager.pages.*;

import java.io.File;
import java.util.Objects;
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
    private Label user_username;
    @FXML
    private ImageView user_pic;
    @FXML
    private Button user_profile;
    @FXML
    private Button user_logout;

    private Settings settings_loader;
    private Statistics statistics_loader;
    private Accounts accounts_loader;
    private Transactions transactions_loader;
    private Credits credits_loader;
    private Profile profile_loader;
    private final Image mm_logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/wip/moneymanager/images/Logo_Money_manager_single.svg.png")));

    private void clearLoaders() {
        //transactions_loader = null;
        //accounts_loader = null;
        //statistics_loader = null;
        //settings_loader = null;
        //credits_loader = null;
        //profile_loader = null;
    }

    public void initialize() throws ExecutionException, InterruptedException {
        busy_indicator.setVisible(false);
        Data.busyProperty().addListener((_, _, newValue) -> {
            if (newValue.intValue() > 0) {
                show_busy_indicator();
            } else {
                hide_busy_indicator();
            }
        });

        transactions.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                clearLoaders();
                transactions_loader = new Transactions();
                change_pane.setCenter(transactions_loader);
            }
        });

        accounts.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                clearLoaders();
                accounts_loader = new Accounts();
                change_pane.setCenter(accounts_loader);
            }
        });

        statistics.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                statistics_loader = new Statistics();
                change_pane.setCenter(statistics_loader);
            }
        });

        settings.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                clearLoaders();
                settings_loader = new Settings();
                change_pane.setCenter(settings_loader);
            }
        });

        settings.toggleGroupProperty().get().selectedToggleProperty().addListener((_, _, newValue) -> {
            if (newValue == null) {
                clearLoaders();
                credits_loader = new Credits();
                change_pane.setCenter(credits_loader);
            }
        });

        transactions.textProperty().bind(Data.lsp.lsb("homescreen.transactions"));
        accounts.textProperty().bind(Data.lsp.lsb("homescreen.accounts"));
        statistics.textProperty().bind(Data.lsp.lsb("homescreen.statistics"));
        settings.textProperty().bind(Data.lsp.lsb("homescreen.settings"));
        user_profile.textProperty().bind(Data.lsp.lsb("basemenu.profile"));

        user_profile.setOnAction(_ -> {
            if (settings.toggleGroupProperty().get().getSelectedToggle() != null)
                settings.toggleGroupProperty().get().selectedToggleProperty().get().setSelected(false);
            if (profile_loader == null) {
                profile_loader = new Profile();
            }
            change_pane.setCenter(profile_loader);
        });

        // Listen for theme changes
        Data.dbUser.themeProperty().addListener((_, _, newValue) -> {
            System.out.println("Theme changed to " + newValue);
            Scene scene = accounts.getScene();
            switch (newValue) {
                case DARK, SYSTEM:
                    scene.getStylesheets().remove(getClass().getResource("/org/wip/moneymanager/style-light.css").toExternalForm());
                    scene.getStylesheets().add(getClass().getResource("/org/wip/moneymanager/style-dark.css").toExternalForm());
                    break;
                case LIGHT:
                    scene.getStylesheets().remove(getClass().getResource("/org/wip/moneymanager/style-dark.css").toExternalForm());
                    scene.getStylesheets().add(getClass().getResource("/org/wip/moneymanager/style-light.css").toExternalForm());
                    break;
            }
        });

        Data.dbUser.accentProperty().addListener((_, _, newValue) -> {
            Scene scene = accounts.getScene();
            scene.getRoot().setStyle("-fu-accent: " + newValue.getHex() + ";");
        });

        Data.userUpdated.addListener((_, _, newValue) -> {
            if (newValue) {
                update_user();
                Data.userUpdated.set(false);
            }
        });

        Data.dbUser.username().addListener((_, _, _) -> {
            update_user();
            Data.userUpdated.set(false);
        });

        accounts.sceneProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                if (Data.dbUser.themeProperty().get() == Theme.LIGHT) {
                    newValue.getStylesheets().remove(getClass().getResource("/org/wip/moneymanager/style-dark.css").toExternalForm());
                    newValue.getStylesheets().add(getClass().getResource("/org/wip/moneymanager/style-light.css").toExternalForm());
                } else {
                    newValue.getStylesheets().remove(getClass().getResource("/org/wip/moneymanager/style-light.css").toExternalForm());
                    newValue.getStylesheets().add(getClass().getResource("/org/wip/moneymanager/style-dark.css").toExternalForm());
                }
                newValue.getRoot().setStyle("-fu-accent: " + Data.dbUser.accentProperty().get().getHex() + ";");
                Data.lsp.setSelectedLanguage(Data.dbUser.languageProperty().get());
                change_pane.setCenter(new Credits());
            }
        });

        Circle clip = new Circle(20, 20, 20);
        user_pic.setClip(clip);
        Data.userDatabase = new UserDatabase();
        update_user();
        loadStartPage();
    }

    public void update_user() {
        File directory = new File(Data.users_images_directory);
        String username = String.valueOf(Data.dbUser.id());
        File[] files = directory.listFiles((_, name) -> name.startsWith(username + "."));
        if (files != null && files.length > 0) {
            String fileExtension = files[0].getName().substring(files[0].getName().lastIndexOf('.'));
            File newFile = new File(Data.users_images_directory, username + fileExtension);
            user_pic.setImage(new Image(newFile.toURI().toString()));
        } else {
            user_pic.setImage(mm_logo);
        }
        user_username.setText(Data.dbUser.username().get());
    }

    public void remove_user() {
        user_pic.setImage(mm_logo);
        user_username.setText("Money Manager");
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

    private void loadStartPage() {
        HomeScreen startPage = Data.dbUser.home_screenProperty().get();

        Platform.runLater(() -> {
            if (startPage == HomeScreen.TRANSACTIONS) {
                transactions.setSelected(true);
            } else if (startPage == HomeScreen.ACCOUNTS) {
                accounts.setSelected(true);
            } else if (startPage == HomeScreen.STATISTICS) {
                statistics.setSelected(true);
            }
        });
    }

    @FXML
    public void logout() {
        // Reset dello stato utente
        Data.dbUser = null; // Rimuove l'utente attualmente loggato

        // Reindirizza alla schermata di login
        SceneHandler.getInstance((Stage) user_logout.getScene().getWindow()).showLoginScreen();
    }


}

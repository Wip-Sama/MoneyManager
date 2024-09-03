package org.wip.moneymanager;

import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.wip.moneymanager.model.DBObjects.dbUser;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.MMDatabase;
import org.wip.moneymanager.model.UserDatabase;
import org.wip.moneymanager.model.types.Theme;
import org.wip.moneymanager.pages.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
    private Label user_something;
    @FXML
    private ImageView user_pic;
    @FXML
    private MenuItem user_profile;
    @FXML
    private MenuItem user_logout;

    private Settings settings_loader;
    private Statistics statistics_loader;
    private Accounts accounts_loader;
    private Transactions transactions_loader;
    private Credits credits_loader;
    private Profile profile_loader;
    private final Image mm_logo = new Image(getClass().getResourceAsStream("/org/wip/moneymanager/images/Logo_Money_manager_single.svg.png"));

    private void clearLoaders() {
        settings_loader = null;
        statistics_loader = null;
        accounts_loader = null;
        transactions_loader = null;
        credits_loader = null;
        profile_loader = null;
    }

    @FXML
    public void initialize() throws ExecutionException, InterruptedException {
        // TODO: da rimuovere dopo che avremo fatto la schermata di login/register
        Task<dbUser> t = MMDatabase.getInstance().getUser(Data.username);
        t.run();
        Data.dbUser = t.get();

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
            } else {
                transactions_loader = null;
            }
        });
        accounts.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                clearLoaders();
                accounts_loader = new Accounts();
                change_pane.setCenter(accounts_loader);
            } else {
                accounts_loader = null;
            }
        });
        statistics.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                statistics_loader = new Statistics();
                change_pane.setCenter(statistics_loader);
            } else {
                statistics_loader = null;
            }
        });
        settings.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                clearLoaders();
                settings_loader = new Settings();
                change_pane.setCenter(settings_loader);
            } else {
                if (settings_loader != null) {
                    settings_loader.close();
                    settings_loader = null;
                }
            }
        });

        settings.toggleGroupProperty().get().selectedToggleProperty().addListener((_, _, newValue) -> {
            if (newValue == null) {
                clearLoaders();
                credits_loader = new Credits();
                change_pane.setCenter(credits_loader);
            } else {
                credits_loader = null;
            }
        });

        transactions.textProperty().bind(Data.lsp.lsb("homescreen.transactions"));
        accounts.textProperty().bind(Data.lsp.lsb("homescreen.accounts"));
        statistics.textProperty().bind(Data.lsp.lsb("homescreen.statistics"));
        settings.textProperty().bind(Data.lsp.lsb("homescreen.settings"));
        user_logout.textProperty().bind(Data.lsp.lsb("basemenu.logout"));
        user_profile.textProperty().bind(Data.lsp.lsb("basemenu.profile"));

        user_profile.setOnAction(_ -> {
            if (settings.toggleGroupProperty().get().getSelectedToggle() != null)
                settings.toggleGroupProperty().get().selectedToggleProperty().get().setSelected(false);
            if (profile_loader == null) {
                profile_loader = new Profile();
            }
            change_pane.setCenter(profile_loader);
        });
        user_logout.setOnAction(_ -> {
            Data.dbUser = null;
            remove_user();
        });

        Data.dbUser.themeProperty().addListener((_, _, newValue) -> {
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
                    newValue.getStylesheets().add("style-light.css");
                    newValue.getStylesheets().remove("style-dark.css");
                } else {
                    newValue.getStylesheets().add("style-dark.css");
                    newValue.getStylesheets().remove("style-light.css");
                }
                newValue.getRoot().setStyle("-fu-accent: " + Data.dbUser.accentProperty().get().getHex() + ";");
                Data.lsp.setSelectedLanguage(Data.dbUser.languageProperty().get());
                change_pane.setCenter(new Credits());
            }
        });
        Circle clip = new Circle(25, 25, 25);
        user_pic.setClip(clip);
        Data.userDatabase = new UserDatabase();
        update_user();
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
        user_something.setText("test updated");
    }

    public void remove_user() {
        user_pic.setImage(mm_logo);
        user_username.setText("Money Manager");
        user_something.setText("test removed");
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
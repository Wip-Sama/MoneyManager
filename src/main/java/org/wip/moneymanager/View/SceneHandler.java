package org.wip.moneymanager.View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import org.wip.moneymanager.Controller.MoneyManagerController;
import org.wip.moneymanager.model.DBObjects.dbUser;
import org.wip.moneymanager.model.Data;

import java.io.IOException;
import java.util.Objects;

import static javafx.application.Application.setUserAgentStylesheet;

public class SceneHandler {
    private static SceneHandler instance;
    private static Stage primaryStage;

    private SceneHandler(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public static SceneHandler getInstance(Stage primaryStage) {
        if (instance == null) {
            instance = new SceneHandler(primaryStage);
        }
        return instance;
    }

    // variabili per memorizzare le dimensioni della finestra
    private double lastHeight = 540;
    private double lastWidth = 960;

    public void showLoginScreen() {
        try {
            setUserAgentStylesheet(getClass().getResource("/org/wip/moneymanager/style-dark.css").toExternalForm());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/login.fxml"));
            Scene scene = new Scene(loader.load());

            lastWidth = primaryStage.getWidth();
            lastHeight = primaryStage.getHeight();

            primaryStage.setMinHeight(540);
            primaryStage.setMinWidth(960);

            primaryStage.setWidth(lastWidth);
            primaryStage.setHeight(lastHeight);

            primaryStage.setTitle("Money Manager - Login");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/wip/moneymanager/images/Logo_Money_manager_single.svg.png")));
            primaryStage.getIcons().add(icon);

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showRegisterScreen() {

        try {
            setUserAgentStylesheet(getClass().getResource("/org/wip/moneymanager/style-dark.css").toExternalForm());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/register.fxml"));
            Scene scene = new Scene(loader.load());

            lastWidth = primaryStage.getWidth();
            lastHeight = primaryStage.getHeight();

            primaryStage.setMinHeight(540);
            primaryStage.setMinWidth(960);

            primaryStage.setWidth(lastWidth);
            primaryStage.setHeight(lastHeight);

            primaryStage.setTitle("Money Manager - Register");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/wip/moneymanager/images/Logo_Money_manager_single.svg.png")));
            primaryStage.getIcons().add(icon);

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startMoneyManager() {
        try {
            setUserAgentStylesheet(getClass().getResource("/org/wip/moneymanager/style-dark.css").toExternalForm());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/base_menu.fxml"));
            Scene scene = new Scene(loader.load());

            lastWidth = primaryStage.getWidth();
            lastHeight = primaryStage.getHeight();

            primaryStage.setMinHeight(810);
            primaryStage.setMinWidth(1440);

            primaryStage.setWidth(lastWidth);
            primaryStage.setHeight(lastHeight);

            primaryStage.setTitle("Money Manager");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/wip/moneymanager/images/Logo_Money_manager_single.svg.png")));
            primaryStage.getIcons().add(icon);

            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

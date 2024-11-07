package org.wip.moneymanager;

import javafx.application.Application;
import javafx.stage.Stage;
import org.wip.moneymanager.View.SceneHandler;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Crea un'istanza della classe Login e mostralo
            SceneHandler sceneHandler = SceneHandler.getInstance(primaryStage);
            sceneHandler.showLoginScreen(); // Chiama il metodo per mostrare la schermata di login
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

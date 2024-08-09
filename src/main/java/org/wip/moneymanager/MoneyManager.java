package org.wip.moneymanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;


public class MoneyManager extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        setUserAgentStylesheet("style-dark.css");
        FXMLLoader fxmlLoader = new FXMLLoader(MoneyManager.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setMinHeight(500);
        stage.setMinWidth(640);
        stage.setTitle("Money Manager");

        Image icon = new Image(Objects.requireNonNull(MoneyManager.class.getResourceAsStream("/org/wip/moneymanager/images/Logo_Money_manager_single.svg.png")));
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.show();
        //TODO: Creare un sistema per le impostazioni globali
        /*
        //TODO: Abilitare il database
        String url = "jdbc:sqlite:money_manager.db";
        Connection con = DriverManager.getConnection(url);
        if(con != null && !con.isClosed())
            System.out.println("Connected!");
         */
    }

    public static void main(String[] args) {
        launch();
    }
}
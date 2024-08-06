package org.wip.moneymanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        setUserAgentStylesheet("style-light.css");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        String url = "jdbc:sqlite:money_manager.db";
        Connection con = DriverManager.getConnection(url);
        if(con != null && !con.isClosed())
            System.out.println("Connected!");
    }

    public static void main(String[] args) {
        launch();
    }
}
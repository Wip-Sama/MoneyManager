package org.wip.moneymanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

/*Premessa per il prof:
* In questo codice troverete diversi metodi apparentemente inutilizzati
* Ora probabilmente qualcuno è effettivamente inutilizzato ma la maggior parte sono utilizzati da scenebuilder
* Servono per permetterci di modificare dei valori nel'FXML */

public class MoneyManager extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        setUserAgentStylesheet("style-dark.css");
        FXMLLoader fxmlLoader = new FXMLLoader(MoneyManager.class.getResource("base_menu.fxml"));
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
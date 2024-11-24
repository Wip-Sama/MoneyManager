package org.wip.moneymanager.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;

import java.io.IOException;

public class DateTransactions extends HBox {

    @FXML
    private Button arrowLeftDate;

    @FXML
    private SVGPath arrowRightDate;

    @FXML
    private Label labelDate;

    public DateTransactions() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/dateTransactions.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


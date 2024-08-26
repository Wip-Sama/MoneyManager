// File: src/main/java/org/wip/moneymanager/pages/Credits.java
package org.wip.moneymanager.pages;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Credits extends BorderPane {
    @FXML
    private ScrollPane scroller;
    @FXML
    private VBox credit_container;

    public Credits() {
    }

    private void update_available_with_without_scrollbars() {
        double width = scroller.getWidth();
        credit_container.setPrefWidth(width);
    }

    @FXML
    public void initialize() {
        scroller.widthProperty().addListener((_, _, _) -> update_available_with_without_scrollbars());
    }
}
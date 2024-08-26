package org.wip.moneymanager.pages;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Accounts extends BorderPane {
    @FXML
    private VBox accounts_container;

    @FXML
    private ScrollPane scroll_miservesoloperingrandireilvbox;

    @FXML
    private ToggleButton hide_balance;

    public ReadOnlyBooleanProperty hideBalanceProperty() {
        return hide_balance.selectedProperty();
    }

    @FXML
    private Button new_account;

    public Accounts() {
        /*
        non è un componente
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/pages/Accounts.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        */
    }

    public double getAvailableSpace(ScrollPane scrollPane) {
        Bounds viewportBounds = scrollPane.getViewportBounds();
        Region content = (Region) scrollPane.getContent();
        double paddingLeft = content.getPadding().getLeft();
        double paddingRight = content.getPadding().getRight();
        return viewportBounds.getWidth() - paddingLeft - paddingRight;
    }

    public void initialize() {
        scroll_miservesoloperingrandireilvbox.viewportBoundsProperty().addListener((_, _, _) -> {
            double availableSpace = getAvailableSpace(scroll_miservesoloperingrandireilvbox);
            accounts_container.setPrefWidth(availableSpace);
        });
    }
}

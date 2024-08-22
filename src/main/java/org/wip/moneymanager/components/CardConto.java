package org.wip.moneymanager.components;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.wip.moneymanager.model.Data;

public class CardConto extends AnchorPane {
    @FXML
    private BorderPane edit_pane;
    @FXML
    private Label name_label;
    @FXML
    private TextField name_field;
    @FXML
    private Label balance_label;
    @FXML
    private BalanceEditor balance_field;
    @FXML
    private Label type_label;
    @FXML
    private ChoiceBox<String> type_field;
    @FXML
    private Label creation_date_label;
    @FXML
    private DatePicker creation_date_field;
    @FXML
    private Label include_into_totals_label;
    @FXML
    private Switch include_into_totals_field;
    @FXML
    private Button discard_changes;
    @FXML
    private Button save_changes;
    @FXML
    private BorderPane display_pane;
    @FXML
    private Button delete_button;
    @FXML
    private ToggleButton edit_pane_toggle;
    @FXML
    private Label account_name;
    @FXML
    private Label account_balance;
    @FXML
    private Label account_type;
    @FXML
    private Label account_creation_date;

    private BooleanProperty hide_balance = new SimpleBooleanProperty(false);

    public CardConto() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/cardconto.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateChoiceBoxItems() {
        int selectedIndex = type_field.getSelectionModel().getSelectedIndex();
        type_field.getItems().setAll(FXCollections.observableArrayList(
                Data.localizationService.localizedStringBinding("debit_card").get(),
                Data.localizationService.localizedStringBinding("credit_card").get(),
                Data.localizationService.localizedStringBinding("cash").get()
        ));
        type_field.getSelectionModel().select(selectedIndex);
    }

    public void initialize() {
        /* Graphics part */
        Rectangle clip = new Rectangle();
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        clip.setMouseTransparent(true);
        setClip(clip);
        edit_pane_toggle.selectedProperty().addListener((_, _, newValue) -> {
            // metto 160 e non edit_pane.getHeight() perché altrimenti con i bordi arrotondati non si vede bene
            double endHeight = newValue ? display_pane.getHeight() + 160 : display_pane.getHeight();
            Timeline timeline = new Timeline();
            KeyValue keyValue = new KeyValue(this.prefHeightProperty(), endHeight);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue); // Adjust duration as needed
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
        });

        /* Edit part */
        SimpleStringProperty amount = new SimpleStringProperty("0,00");
        SimpleStringProperty eur = new SimpleStringProperty("eur");
        account_balance.textProperty().bind(
            Data.lsp.lsb("cardconto.balance",
                amount,
                eur
            )
        );

        hide_balance.addListener((_, _, newValue) -> {
            if (newValue) {
                account_balance.setStyle("-fx-text-fill: -fu-text-2");
            } else {
                account_balance.setStyle("-fx-text-fill: -fu-text-1");
            }
        });

        account_creation_date.textProperty().bind(Data.localizationService.localizedStringBinding("cardconto.creation_date"));
        updateChoiceBoxItems();
        type_field.getSelectionModel().select(0);

        /* Update part */
        Data.localizationService.selectedLanguageProperty().addListener((_, _, _) -> {
            updateChoiceBoxItems();
        });
    }
}

package org.wip.moneymanager.components;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.wip.moneymanager.model.DBObjects.dbAccount;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.types.AccountType;
import org.wip.moneymanager.model.types.Theme;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

    private dbAccount account = null;
    public BooleanProperty hide_balance = new SimpleBooleanProperty(false);

    private final static StringProperty hidden_balance = new SimpleStringProperty("00,0");
    private final StringProperty amount = new SimpleStringProperty();
    private final StringProperty currency = new SimpleStringProperty("EUR");
    private final StringProperty creation_date = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> creation_date_time = new SimpleObjectProperty<>();
    private final StringProperty type = new SimpleStringProperty("");
    private final StringProperty name = new SimpleStringProperty("");
    private final BooleanProperty destruct = new SimpleBooleanProperty(false);
    private long lastClickTime = 0;

    public ReadOnlyBooleanProperty destructProperty() {
        return destruct;
    }

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

    public CardConto(dbAccount account) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/cardconto.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.account = account;
    }

    private void update_type_field() {
        int selectedIndex = type_field.getSelectionModel().getSelectedIndex();
        type_field.getItems().setAll(FXCollections.observableArrayList(
                Data.lsp.lsb("accounttype.cash").get(),
                Data.lsp.lsb("accounttype.bank").get(),
                Data.lsp.lsb("accounttype.credit_card").get(),
                Data.lsp.lsb("accounttype.debit_card").get(),
                Data.lsp.lsb("accounttype.savings").get(),
                Data.lsp.lsb("accounttype.investment").get(),
                Data.lsp.lsb("accounttype.loan").get()
        ));
        type_field.getSelectionModel().select(selectedIndex);
    }

    private void initializeChoiceBox() {
        update_type_field();
        type_field.getSelectionModel().select(account.typeProperty().get().ordinal());
        balance_field.setCurrency(account.currencyProperty().get());
    }

    private void discardChanges() {
        name_field.setText(name.get());
        balance_field.setBalance(Double.parseDouble(amount.get()));
        balance_field.setCurrency(currency.get());
        type_field.getSelectionModel().select(account.typeProperty().get().ordinal());
        creation_date_field.setValue(creation_date_time.get().toLocalDate());
        include_into_totals_field.setState(account.includeIntoTotalsProperty().get() == 0);
    }

    private void updateAccount() throws SQLException {
        account.setName(name_field.getText());
        account.setBalance(balance_field.getBalance());
        //(int) (Timestamp.valueOf(creation_date_time.get()).getTime() / 1000)
        account.setCreationDate((int) creation_date_field.getValue().atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
        account.setType(AccountType.fromInt(type_field.getSelectionModel().getSelectedIndex()));
        account.setIncludeIntoTotals(include_into_totals_field.getState() ? 0 : 1);
        account.setCurrency(balance_field.getCurrency());
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
        hide_balance.addListener((_, _, newValue) -> {
            if (newValue) {
                account_balance.setStyle("-fx-text-fill: -fu-text-2");
                account_balance.textProperty().bind(Data.lsp.lsb("cardconto.balance", hidden_balance, currency));
            } else {
                account_balance.setStyle("-fx-text-fill: -fu-text-1");
                account_balance.textProperty().bind(Data.lsp.lsb("cardconto.balance", amount, currency));
            }
        });

        save_changes.onActionProperty().set(event -> {
            try {
                updateAccount();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(new Duration(0));
        tooltip.setHideDelay(new Duration(0));
        tooltip.textProperty().bind(Data.lsp.lsb("cardconto.delete.tooltip"));
        delete_button.setTooltip(tooltip);
        discard_changes.onActionProperty().set(event -> {
            discardChanges();
        });
        delete_button.onMouseClickedProperty().set(event -> {
            handleMouseClick(event.isShiftDown());
        });

        delete_button.setOnMouseEntered(event -> updateTooltipText(tooltip, event.isShiftDown()));

        account_name.textProperty().bind(name);
        account_type.textProperty().bind(type);
        account_balance.textProperty().bind(Data.lsp.lsb("cardconto.balance", amount, currency));
        account_creation_date.textProperty().bind(Data.lsp.lsb("cardconto.creation_date", creation_date));

        /* Update part */
        Data.localizationService.selectedLanguageProperty().addListener((_, _, _) -> {
            // Realisticamente non c'è il pericolo che cambi la lingua mentre ci troviamo qui...
            // ma meglio prevenire che curare
            update_type_field();
        });
        sceneProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                if (account != null) {
                    name.set(account.nameProperty().get());
                    amount.set(String.valueOf(account.balanceProperty().get()));
                    creation_date_time.set(LocalDateTime.ofInstant(Instant.ofEpochSecond(account.creationDateProperty().get()), ZoneId.systemDefault()));
                    creation_date.set(creation_date_time.get().toString());
                    type.bind(Data.lsp.lsb("accounttype." + account.typeProperty().get().toString().toLowerCase()));
                    currency.set(account.currencyProperty().get());

                    creation_date_time.addListener((_, _, newCreationDate) -> {
                        creation_date_field.setValue(newCreationDate.toLocalDate());
                    });

                    name_field.setText(name.get());
                    balance_field.setBalance(Double.parseDouble(amount.get()));
                    type_field.getSelectionModel().select(account.typeProperty().get().ordinal());
                    creation_date_field.setValue(creation_date_time.get().toLocalDate());
                    include_into_totals_field.setState(account.includeIntoTotalsProperty().get() == 0);

                    // Si può fare con un binding, ma è più bello vedere 4 listener di fila
                    account.nameProperty().addListener((_, _, newName) -> {
                        name.set(newName);
                    });
                    account.balanceProperty().addListener((_, _, newBalance) -> {
                        amount.set(String.valueOf(newBalance.doubleValue()));
                    });
                    account.creationDateProperty().addListener((_, _, newCreationDate) -> {
                        Instant instant = Instant.ofEpochSecond(newCreationDate.intValue());
                        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                        creation_date.set(localDateTime.toString());
                        creation_date_time.set(localDateTime);
                    });
                    account.typeProperty().addListener((_, _, newType) -> {
                        switch (newType) {
                            case CASH:
                                type.bind(Data.lsp.lsb("accounttype.cash"));
                                break;
                            case BANK:
                                type.bind(Data.lsp.lsb("accounttype.bank"));
                                break;
                            case CREDIT_CARD:
                                type.bind(Data.lsp.lsb("accounttype.credit_card"));
                                break;
                            case DEBIT_CARD:
                                type.bind(Data.lsp.lsb("accounttype.debit_card"));
                                break;
                            case SAVINGS:
                                type.bind(Data.lsp.lsb("accounttype.savings"));
                                break;
                            case INVESTMENT:
                                type.bind(Data.lsp.lsb("accounttype.investment"));
                                break;
                            case LOAN:
                                type.bind(Data.lsp.lsb("accounttype.loan"));
                                break;
                        }
                    });
                    account.currencyProperty().addListener((_, _, newCurrency) -> {
                        currency.set(newCurrency);
                    });

                    initializeChoiceBox();
                }
            }
        });
    }

    private void handleMouseClick(boolean shiftDown) {
        long currentTime = System.currentTimeMillis();
        if (shiftDown) {
            destruct.set(true);
        } else {
            if (currentTime - lastClickTime <= 200) {
                destruct.set(true);
            }
            lastClickTime = currentTime;
        }
    }

    private void updateTooltipText(Tooltip tooltip, boolean shiftDown) {
        if (shiftDown) {
            tooltip.textProperty().bind(Data.lsp.lsb("cardconto.delete.tooltip_shift"));
        } else {
            tooltip.textProperty().bind(Data.lsp.lsb("cardconto.delete.tooltip"));
        }
    }

    public Property<Boolean> hideBalanceProperty() {
        return hide_balance;
    }
}

package org.wip.moneymanager.pages;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.wip.moneymanager.components.ColorPickerButton;
import org.wip.moneymanager.components.ColorPickerPreset;
import org.wip.moneymanager.model.*;


import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Settings extends BorderPane {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MMDatabase db = MMDatabase.getInstance();

    @FXML
    private ChoiceBox<String> theme;

    @FXML
    private ChoiceBox<String> language;

    @FXML
    private ChoiceBox<String> primary_currency;

    @FXML
    private ChoiceBox<String> first_day_of_week;

    @FXML
    private ChoiceBox<String> start_page;

    @FXML
    private ColorPickerPreset preset_blue;

    @FXML
    private ColorPickerPreset preset_green;

    @FXML
    private ColorPickerPreset preset_yellow;

    @FXML
    private ColorPickerPreset preset_orange;

    @FXML
    private ColorPickerButton custom_color;

    @FXML
    private Button new_category;

    @FXML
    private Button new_sub_category;

    @FXML
    private ScrollPane category_container;

    @FXML
    private ScrollPane sub_category_container;

    @FXML
    private VBox category_list;

    @FXML
    private VBox sub_category_list;

    public Settings() {}

    public double[] getAvailableSpace(ScrollPane scrollPane) {
        Bounds viewportBounds = scrollPane.getViewportBounds();
        Region content = (Region) scrollPane.getContent();
        double paddingLeft = content.getPadding().getLeft();
        double paddingRight = content.getPadding().getRight();
        double paddingTop = content.getPadding().getTop();
        double paddingBottom = content.getPadding().getBottom();
        return new double[]{viewportBounds.getWidth() - paddingLeft - paddingRight, viewportBounds.getHeight() - paddingTop - paddingBottom};
    }

    public double getAvailableSpaceHeight(ScrollPane scrollPane) {
        Bounds viewportBounds = scrollPane.getViewportBounds();
        Region content = (Region) scrollPane.getContent();
        double paddingLeft = content.getPadding().getLeft();
        double paddingRight = content.getPadding().getRight();
        return viewportBounds.getWidth() - paddingLeft - paddingRight;
    }

    private void initialize_choice_box(Task<User> tmp) throws ExecutionException, InterruptedException {
        Data.subscribe_busy();
        tmp.run();
        User user = tmp.get();
        theme.getItems().addAll("Light", "Dark");
        theme.setValue(user.theme().toString());

        language.getItems().addAll("English", "Italian");
        language.setValue(user.language());

        first_day_of_week.getItems().addAll("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica");
        //first_day_of_week.setValue(user.week_start_string());

        start_page.getItems().addAll("Nessuna", "Conti", "Statistiche", "Transazioni");
        //start_page.setValue(user.home_screen());

        Task<List<Currency>> currencies = db.getAllCurrency();
        currencies.run();
        currencies.get().stream().sorted().forEach(currency -> primary_currency.getItems().add(currency.name().toUpperCase()));
        Task<Currency> currency = db.getCurrency(user.main_currency());
        currency.run();
        primary_currency.setValue(currency.get().name().toUpperCase());
        Data.unsubscribe_busy();
    }

    public void initialize() throws ExecutionException, InterruptedException {
        Data.subscribe_busy();
        category_container.viewportBoundsProperty().addListener((_, _, _) -> {
            double[] availableSpace = getAvailableSpace(category_container);
            category_list.setPrefWidth(availableSpace[0]);
            category_list.setMinHeight(availableSpace[1]);
        });
        sub_category_container.viewportBoundsProperty().addListener((_, _, _) -> {
            double[] availableSpace = getAvailableSpace(sub_category_container);
            sub_category_list.setPrefWidth(availableSpace[0]);
            sub_category_list.setMinHeight(availableSpace[1]);
        });

        Task<User> tmp = db.getUser(Data.user);
        tmp.setOnSucceeded(event -> {
            try {
                initialize_choice_box(tmp);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        // Execute the task in a background thread
        //executorService.submit(tmp);

        Thread t = new Thread(tmp);
        t.setDaemon(true) ;
        t.start();

        theme.valueProperty().addListener((_, _, newValue) -> {
            Data.subscribe_busy();
            Task<User> task = db.getUser(Data.user);
            task.run();
            User user = null;
            try {
                user = task.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            try {
                user.setTheme(Theme.fromString(newValue));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Data.theme.set(user.theme());
            Data.unsubscribe_busy();
        });
        Data.unsubscribe_busy();
    }
}

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
import org.wip.moneymanager.model.types.Currency;
import org.wip.moneymanager.model.types.Theme;
import org.wip.moneymanager.model.types.User;


import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private void initialize_choice_box() throws ExecutionException, InterruptedException {
        Data.subscribe_busy();
        theme.getItems().addAll("Light", "Dark");
        theme.setValue(Data.user.themeProperty().get().toString());

        language.getItems().addAll("Eng", "Ita");
        language.setValue(Data.user.languageProperty().get());

        first_day_of_week.getItems().addAll("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica");
        first_day_of_week.setValue(Data.user.week_startProperty().get().toString());

        start_page.getItems().addAll("Nessuna", "Conti", "Statistiche", "Transazioni");
        //start_page.setValue(user.home_screen());

        Task<List<Currency>> currencies = db.getAllCurrency();
        currencies.run();
        currencies.get().stream().sorted().forEach(currency -> primary_currency.getItems().add(currency.name().toUpperCase()));
        primary_currency.setValue(Data.user.main_currencyProperty().get().toUpperCase());
        Data.unsubscribe_busy();
    }

    private void deselect_all_presets() {
        preset_blue.selected.set(false);
        preset_green.selected.set(false);
        preset_yellow.selected.set(false);
        preset_orange.selected.set(false);
        custom_color.selected.set(false);
    }

    private void set_selected() {
        int[] rgb = Data.user.accentProperty().get().getRGB();

        if (Arrays.equals(preset_orange.getRGB(), rgb)) {
            preset_orange.selected.set(true);
        } else if (Arrays.equals(preset_yellow.getRGB(), rgb)) {
            preset_yellow.selected.set(true);
        } else if (Arrays.equals(preset_green.getRGB(), rgb)) {
            preset_green.selected.set(true);
        } else if (Arrays.equals(preset_blue.getRGB(), rgb)) {
            preset_blue.selected.set(true);
        } else {
            custom_color.selected.set(true);
            custom_color.setRGB(rgb);
        }
    }

    private void initialize_accent_selector() {
        set_selected();

        // Set accent color updater
        Data.user.accentProperty().addListener((_, _, newValue) -> {
            if (newValue == null) {
                return;
            }

            deselect_all_presets();
            set_selected();

            try {
                Data.user.setAccent(Data.user.accentProperty().get());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void initialize() throws ExecutionException, InterruptedException {
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
        Data.subscribe_busy();

        Task<User> tmp = db.getUser(Data.username);
        tmp.run();
        User user = tmp.get();

        initialize_choice_box();
        initialize_accent_selector();

        executorService.submit(tmp);

        theme.valueProperty().addListener((_, _, newValue) -> {
            try {
                user.setTheme(Theme.fromString(newValue));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                Data.user.setTheme(user.themeProperty().get());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        Data.unsubscribe_busy();
    }
}

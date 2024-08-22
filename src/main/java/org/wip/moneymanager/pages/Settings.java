package org.wip.moneymanager.pages;

import javafx.collections.FXCollections;
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
import org.wip.moneymanager.model.types.Week;


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
    private Label page_title;
    @FXML
    private Label theme_label;
    @FXML
    private ChoiceBox<String> theme;
    @FXML
    private Label accent_label;
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
    private Label language_label;
    @FXML
    private ChoiceBox<String> language;
    @FXML
    private Label primary_curency_label;
    @FXML
    private ChoiceBox<String> primary_currency;
    @FXML
    private Label first_day_of_week_label;
    @FXML
    private ChoiceBox<String> first_day_of_week;
    @FXML
    private Label start_page_label;
    @FXML
    private ChoiceBox<String> start_page;
    @FXML
    private Label category_label;
    @FXML
    private Button new_category;
    @FXML
    private ScrollPane category_container;
    @FXML
    private VBox category_list;
    @FXML
    private Button new_sub_category;
    @FXML
    private Label sub_category_label;
    @FXML
    private ScrollPane sub_category_container;
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

    private void update_day_of_week() {
        int selectedIndex = first_day_of_week.getSelectionModel().getSelectedIndex();
        first_day_of_week.getItems().setAll(FXCollections.observableArrayList(
                        Data.localizationService.localizedStringBinding("monday").get(),
                        Data.localizationService.localizedStringBinding("tuesday").get(),
                        Data.localizationService.localizedStringBinding("wednesday").get(),
                        Data.localizationService.localizedStringBinding("thursday").get(),
                        Data.localizationService.localizedStringBinding("friday").get(),
                        Data.localizationService.localizedStringBinding("saturday").get(),
                        Data.localizationService.localizedStringBinding("sunday").get()
                )
        );
        first_day_of_week.getSelectionModel().select(selectedIndex);
    }

    private void update_theme() {
        int selectedIndex = theme.getSelectionModel().getSelectedIndex();
        theme.getItems().setAll(FXCollections.observableArrayList(
                        Data.localizationService.localizedStringBinding("theme.light").get(),
                        Data.localizationService.localizedStringBinding("theme.dark").get()
                )
        );
        theme.getSelectionModel().select(selectedIndex);
    }

    private void update_start_page() {
        int selectedIndex = start_page.getSelectionModel().getSelectedIndex();
        start_page.getItems().setAll(FXCollections.observableArrayList(
                        Data.localizationService.localizedStringBinding("monday").get(),
                        Data.localizationService.localizedStringBinding("tuesday").get(),
                        Data.localizationService.localizedStringBinding("wednesday").get(),
                        Data.localizationService.localizedStringBinding("thursday").get(),
                        Data.localizationService.localizedStringBinding("friday").get(),
                        Data.localizationService.localizedStringBinding("saturday").get(),
                        Data.localizationService.localizedStringBinding("sunday").get()
                )
        );
        start_page.getSelectionModel().select(selectedIndex);
    }

    private void initialize_choice_box() throws ExecutionException, InterruptedException {
        Data.subscribe_busy();

        language.getItems().addAll(Data.lsp.getAvailableLocales());
        language.setValue(Data.user.languageProperty().get());
        language.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            try {
                Data.user.setLanguage(newValue);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Data.lsp.setSelectedLanguage(newValue);
        });

        update_theme();
        theme.getSelectionModel().select(Data.user.themeProperty().get().ordinal()-1);
        theme.valueProperty().addListener((_, _, newValue) -> {
            //+1 perché System non esiste ma il conteggio del tema parte da 0 (system)
            try {
                Data.user.setTheme(Theme.fromInt(theme.getSelectionModel().getSelectedIndex()+1));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        update_day_of_week();
        first_day_of_week.getSelectionModel().select(Data.user.week_startProperty().get().ordinal());
        first_day_of_week.valueProperty().addListener((_, _, newValue) -> {
            try {
                Data.user.setWeek_start(Week.fromInt(first_day_of_week.getSelectionModel().getSelectedIndex()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        update_start_page();
        start_page.getSelectionModel().select(Data.user.home_screenProperty().get());


        Task<List<String>> currencies = db.getAllCurrencyName();
        currencies.run();
        currencies.get().stream().sorted().forEach(currency -> primary_currency.getItems().add(currency.toUpperCase()));
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

        initialize_choice_box();
        initialize_accent_selector();

        page_title.textProperty().bind(Data.lsp.lsb("settings"));
        theme_label.textProperty().bind(Data.lsp.lsb("settings.theme"));
        accent_label.textProperty().bind(Data.lsp.lsb("settings.accent"));
        language_label.textProperty().bind(Data.lsp.lsb("settings.language"));
        primary_curency_label.textProperty().bind(Data.lsp.lsb("settings.primary_currency"));
        first_day_of_week_label.textProperty().bind(Data.lsp.lsb("settings.first_day_of_week"));
        start_page_label.textProperty().bind(Data.lsp.lsb("settings.start_page"));
        category_label.textProperty().bind(Data.lsp.lsb("settings.categories"));
        sub_category_label.textProperty().bind(Data.lsp.lsb("settings.subcategories"));
        new_category.textProperty().bind(Data.lsp.lsb("settings.new_category"));
        new_sub_category.textProperty().bind(Data.lsp.lsb("settings.new_subcategory"));

        executorService.shutdown();
        Data.unsubscribe_busy();

        /* Update stage */
        Data.lsp.selectedLanguageProperty().addListener((_, _, _) -> {
            update_day_of_week();
            update_theme();
            update_start_page();
        });
    }
}

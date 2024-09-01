package org.wip.moneymanager.pages;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.wip.moneymanager.MoneyManager;
import org.wip.moneymanager.components.Category;
import org.wip.moneymanager.components.ColorPickerButton;
import org.wip.moneymanager.components.ColorPickerPreset;
import org.wip.moneymanager.model.*;
import org.wip.moneymanager.model.DBObjects.dbCategory;
import org.wip.moneymanager.model.types.HomeScreen;
import org.wip.moneymanager.model.types.Theme;
import org.wip.moneymanager.model.types.Week;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Note per il prof: ho provato a fare il category editor direttamente nella classe setting
// volevo vedere come sarebbe stato se lo avessi fatto così invece che in un suo componente
// questo è il risultato, non mi piace chissà quanto ma ho deciso di lasciarlo
// per lo stesso motivo del color picker come popup

public class Settings extends BorderPane implements AutoCloseable {
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
    private VBox subcategory_list;
    @FXML
    private BorderPane category_bp;
    @FXML
    private BorderPane subcategory_bp;
    @FXML
    private HBox category_editor;
    @FXML
    private ToggleButton income;
    @FXML
    private ToggleButton expense;
    @FXML
    private ToggleGroup category_type;

    //private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MMDatabase db = MMDatabase.getInstance();
    private final ObjectProperty<Category> selectedCategory = new SimpleObjectProperty<>();
    private final ObservableList<Category> categories = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<Category> subcategories = FXCollections.observableList(new ArrayList<>());

    public Settings() {
        try {
            FXMLLoader loader = new FXMLLoader(MoneyManager.class.getResource("pages/settings.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        System.out.println("Closing Settings");
        executorService.shutdown();
    }

    public double[] getAvailableSpace(ScrollPane scrollPane) {
        Bounds viewportBounds = scrollPane.getViewportBounds();
        Region content = (Region) scrollPane.getContent();
        double paddingLeft = content.getPadding().getLeft();
        double paddingRight = content.getPadding().getRight();
        double paddingTop = content.getPadding().getTop();
        double paddingBottom = content.getPadding().getBottom();
        return new double[]{viewportBounds.getWidth() - paddingLeft - paddingRight, viewportBounds.getHeight() - paddingTop - paddingBottom};
    }

    private void localize_day_of_week() {
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

    private void localize_theme() {
        int selectedIndex = theme.getSelectionModel().getSelectedIndex();
        theme.getItems().setAll(FXCollections.observableArrayList(
                        Data.localizationService.localizedStringBinding("theme.light").get(),
                        Data.localizationService.localizedStringBinding("theme.dark").get()
                )
        );
        theme.getSelectionModel().select(selectedIndex);
    }

    private void localize_start_page() {
        int selectedIndex = start_page.getSelectionModel().getSelectedIndex();
        start_page.getItems().setAll(FXCollections.observableArrayList(
                        Data.localizationService.localizedStringBinding("homescreen.none").get(),
                        Data.localizationService.localizedStringBinding("homescreen.transactions").get(),
                        Data.localizationService.localizedStringBinding("homescreen.accounts").get(),
                        Data.localizationService.localizedStringBinding("homescreen.statistics").get()
                )
        );
        start_page.getSelectionModel().select(selectedIndex);
    }

    private void update_category_list() throws ExecutionException, InterruptedException {
        this.categories.clear();
        category_list.getChildren().clear();

        Task<List<dbCategory>> dbCategories = Data.userDatabase.getAllCategories(category_type.selectedToggleProperty().get().equals(income) ? 0 : 1);
        executorService.submit(dbCategories);
        dbCategories.setOnSucceeded(_ -> {
            for (dbCategory dbCategory : dbCategories.getValue()) {
                Category category = new Category(dbCategory);
                this.categories.add(category);
            }
        });
    }

    private void update_subcategory_list() throws ExecutionException, InterruptedException {
        this.subcategories.clear();
        subcategory_list.getChildren().clear();

        Task<List<dbCategory>> dbCategories = Data.userDatabase.getAllSubcategories(selectedCategory.get().getDbcategory().id());
        dbCategories.run();
        for (dbCategory dbCategory : dbCategories.get()) {
            Category category = new Category(dbCategory);
            this.subcategories.add(category);
        }
    }

    private void initialize_choice_box() {
        Data.subscribe_busy();
        language.getItems().addAll(Data.lsp.getAvailableLocales());
        language.setValue(Data.dbUser.languageProperty().get());
        language.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            try {
                Data.dbUser.setLanguage(newValue);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Data.lsp.setSelectedLanguage(newValue);
        });

        localize_theme();
        theme.getSelectionModel().select(Data.dbUser.themeProperty().get().ordinal()-1);
        theme.valueProperty().addListener((_, _, newValue) -> {
            try {
                Data.dbUser.setTheme(Theme.fromInt(theme.getSelectionModel().getSelectedIndex()+1));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        localize_day_of_week();
        first_day_of_week.getSelectionModel().select(Data.dbUser.week_startProperty().get().ordinal());
        first_day_of_week.valueProperty().addListener((_, _, newValue) -> {
            try {
                Data.dbUser.setWeek_start(Week.fromInt(first_day_of_week.getSelectionModel().getSelectedIndex()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        localize_start_page();
        start_page.getSelectionModel().select(Data.dbUser.home_screenProperty().get().ordinal());
        start_page.valueProperty().addListener((_, _, newValue) -> {
            try {
                Data.dbUser.setHome_screen(HomeScreen.fromInt(start_page.getSelectionModel().getSelectedIndex()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        category_editor.widthProperty().addListener((_, _, _) -> {
            category_bp.setMaxWidth(category_editor.getWidth()/2);
            subcategory_bp.setMaxWidth(category_editor.getWidth()/2);
        });

        Task<List<String>> currencies = db.getAllCurrencyName();
        executorService.submit(currencies);
        currencies.setOnSucceeded(_ -> {
            primary_currency.setValue(Data.dbUser.main_currencyProperty().get());
            currencies.getValue().stream().sorted().forEach(currency -> primary_currency.getItems().add(currency.toUpperCase()));
        });

        Data.unsubscribe_busy();
    }

    private void deselect_all_colors() {
        preset_blue.selected.set(false);
        preset_green.selected.set(false);
        preset_yellow.selected.set(false);
        preset_orange.selected.set(false);
        custom_color.selected.set(false);
    }

    private void set_selected_color() {
        int[] rgb = Data.dbUser.accentProperty().get().getRGB();

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
        set_selected_color();
        Data.dbUser.accentProperty().addListener((_, _, newValue) -> {
            if (newValue == null) {
                return;
            }
            deselect_all_colors();
            set_selected_color();
            try {
                Data.dbUser.setAccent(Data.dbUser.accentProperty().get());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void initialize_category_list() throws ExecutionException, InterruptedException {
        this.categories.addListener((ListChangeListener<Category>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Category category : c.getAddedSubList()) {
                        if (category.is_tmp.get()) {
                            category_list.getChildren().addFirst(category);
                        } else {
                            category_list.getChildren().add(category);
                        }
                        category.destruct.addListener((_, _, newValue) -> {
                            if (!newValue) {
                                return;
                            }

                            if (category.getDbcategory() == null) {
                                categories.remove(category);
                                return;
                            }

                            Task<List<dbCategory>> hasSub = Data.userDatabase.getAllSubcategories(category.getDbcategory().id());
                            executorService.submit(hasSub);
                            hasSub.setOnSucceeded(e -> {
                                if (!hasSub.getValue().isEmpty()) {
                                    Alert alert = new Alert(Alert.AlertType.WARNING, Data.lsp.lsb("alert.you_cant_delete_category_with_subcategories").get(), ButtonType.OK);
                                    alert.setTitle("Error");
                                    alert.setHeaderText(null);
                                    alert.showAndWait();
                                    category.destruct.set(false);
                                } else {
                                    Task<Boolean> result = Data.userDatabase.forceRemoveCategory(category.getDbcategory().id());
                                    executorService.submit(result);
                                    result.setOnSucceeded(_ -> categories.remove(category));
                                }
                            });
                        });
                        category.selected.addListener((_, _, newValue) -> {
                            if (newValue) {
                                selectedCategory.set(category);
                            } else if (selectedCategory.get() == category) {
                                selectedCategory.set(null);
                            }
                        });
                    }
                }
                if (c.wasRemoved()) {
                    category_list.getChildren().removeAll(c.getRemoved());
                }
            }
        });
        this.subcategories.addListener((ListChangeListener<Category>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Category subcategory : c.getAddedSubList()) {
                        if (subcategory.is_tmp.get()) {
                            subcategory_list.getChildren().addFirst(subcategory);
                        } else {
                            subcategory_list.getChildren().add(subcategory);
                        }
                        subcategory.selectionable(false);
                        subcategory.destruct.addListener((_, _, newValue) -> {
                            if (!newValue) {
                                return;
                            }

                            if (subcategory.getDbcategory() == null) {
                                subcategories.remove(subcategory);
                                return;
                            }

                            Task<List<dbCategory>> hasSub = Data.userDatabase.getAllSubcategories(subcategory.getDbcategory().id());
                            hasSub.run();
                            try {
                                if (!hasSub.get().isEmpty()) {
                                    Alert alert = new Alert(Alert.AlertType.WARNING, Data.lsp.lsb("alert.you_cant_delete_category_with_subcategories").get(), ButtonType.OK);
                                    alert.setTitle("Error");
                                    alert.setHeaderText(null);
                                    alert.showAndWait();
                                    subcategory.destruct.set(false);
                                } else {
                                    Task<Boolean> result = Data.userDatabase.forceRemoveCategory(subcategory.getDbcategory().id());
                                    result.run();
                                    subcategories.remove(subcategory);
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                }
                if (c.wasRemoved()) {
                    subcategory_list.getChildren().removeAll(c.getRemoved());
                }
            }
        });
        update_category_list();
    }

    private boolean creating_new_category() {
        for (Category category : categories) {
            if (category.is_tmp.get()) {
                return true;
            }
        }
        for (Category subcategory : subcategories) {
            if (subcategory.is_tmp.get()) {
                return true;
            }
        }
        return false;
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
            subcategory_list.setPrefWidth(availableSpace[0]);
            subcategory_list.setMinHeight(availableSpace[1]);
        });

        initialize_choice_box();
        initialize_accent_selector();
        initialize_category_list();

        new_category.onActionProperty().set(_ -> {
            if (creating_new_category()) {
                return;
            }
            this.categories.addFirst(new Category(category_type.selectedToggleProperty().get().equals(income) ? 0 : 1));
        });
        new_sub_category.onActionProperty().set(_ -> {
            if (creating_new_category()) {
                return;
            }
            if (selectedCategory.get() != null) {
                this.subcategories.addFirst(new Category(category_type.selectedToggleProperty().get().equals(income) ? 0 : 1, selectedCategory.get().getDbcategory().id()));
            }
        });

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
        income.textProperty().bind(Data.lsp.lsb("settings.income"));
        expense.textProperty().bind(Data.lsp.lsb("settings.expense"));

        income.selectedProperty().addListener((_, _, newValue) -> {
            if (!newValue && !expense.isSelected()) {
                income.setSelected(true);
            }
        });
        expense.selectedProperty().addListener((_, _, newValue) -> {
            if (!newValue && !income.isSelected()) {
                expense.setSelected(true);
            }
        });

        category_type.selectedToggleProperty().addListener((_, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            if (!newValue.equals(oldValue)) {
                selectedCategory.set(null);
            }
            try {
                update_category_list();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        selectedCategory.addListener((_, oldValue, newValue) -> {
            try {
                if (oldValue != null) {
                    oldValue.selected.set(false);
                }
                if (newValue != null) {
                    update_subcategory_list();
                    new_sub_category.setDisable(false);
                }
                else {
                    subcategories.clear();
                    new_sub_category.setDisable(true);
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Data.unsubscribe_busy();

        sceneProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {

            }
        });

        /* Update stage */
        Data.lsp.selectedLanguageProperty().addListener((_, _, _) -> {
            localize_day_of_week();
            localize_theme();
            localize_start_page();
        });
    }
}
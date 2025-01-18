package org.wip.moneymanager.components;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.utility.FieldAnimationUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategorySelector extends HBox {

    @FXML
    private ComboBox<String> category_box;

    @FXML
    private Label separator;

    @FXML
    private ComboBox<String> sub_category_box;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CategorySelector() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/categorySelector.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        Data.esm.register(executorService);
        if (category_box.getOnAction() == null) { // Evita registrazioni multiple
            category_box.setOnAction(event -> populateSubCategories());
        }

        setComboBoxCellSize(category_box, 50);
        setComboBoxCellSize(sub_category_box, 50);
    }

    public ComboBox<String> getCategoryBox() {
        return category_box;
    }

    public ComboBox<String> getSubCategoryBox() {
        return sub_category_box;
    }

    private void populateMainCategoriesByType(int type) {
        Platform.runLater(() -> {
            clear();
            category_box.setDisable(true);
        });

        Task<List<String>> taskCategory = Data.userDatabase.getMainCategoryNamesByType(type);
        taskCategory.setOnSucceeded(event -> {
            List<String> mainCategories = taskCategory.getValue();
            Platform.runLater(() -> {
                category_box.getItems().addAll(mainCategories);
                category_box.setDisable(false);
            });
        });

        taskCategory.setOnFailed(event -> {
            Throwable ex = taskCategory.getException();
            ex.printStackTrace();
            Platform.runLater(() -> category_box.setDisable(false));
        });

        executorService.submit(taskCategory);
    }

    public void populateMainCategoriesAllType() {
        Platform.runLater(() -> {
            clear();
            category_box.setDisable(true);
        });

        Task<List<String>> taskCategory = Data.userDatabase.getMainCategoryNames();

        taskCategory.setOnSucceeded(event -> {
            List<String> mainCategories = taskCategory.getValue();
            Platform.runLater(() -> {
                category_box.getItems().addAll(mainCategories);
                category_box.setDisable(false);
            });
        });

        taskCategory.setOnFailed(event -> {
            Throwable ex = taskCategory.getException();
            ex.printStackTrace();
            Platform.runLater(() -> category_box.setDisable(false));
        });

        executorService.submit(taskCategory);
    }

    public void populateMainCategoriesType() {
        populateMainCategoriesAllType();
    }

    public void populateMainCategoriesForIncome() {
        populateMainCategoriesByType(0); // Type 0: Entrate
    }

    public void populateMainCategoriesForExpense() {
        populateMainCategoriesByType(1); // Type 1: Spese
    }


    private void populateSubCategories() {
        String selectedMainCategory = category_box.getSelectionModel().getSelectedItem();
        if (selectedMainCategory != null) {
            Platform.runLater(() -> sub_category_box.getItems().clear()); // Svuota prima le sottocategorie

            Task<List<String>> taskSubCategory = Data.userDatabase.getSubCategoriesByMainCategory(selectedMainCategory);

            taskSubCategory.setOnSucceeded(event -> {
                List<String> subCategories = taskSubCategory.getValue();
                Platform.runLater(() -> {
                    sub_category_box.getItems().addAll(subCategories);
                });
            });

            taskSubCategory.setOnFailed(event -> {
                Throwable ex = taskSubCategory.getException();
                ex.printStackTrace();
            });

            executorService.submit(taskSubCategory);
        }
    }

    public String getSelectedCategory() {
        return category_box.getSelectionModel().getSelectedItem();
    }

    public String getSelectedSubCategory() {
        return sub_category_box.getSelectionModel().getSelectedItem();
    }

    public void setCategory_box(int category) throws SQLException {
        String categoryName = Data.userDatabase.getCategoryNameById(category);
        Task<Void> waitForData = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (category_box.getItems().isEmpty()) {
                    Thread.sleep(100); // Aspetta che gli elementi siano caricati
                }
                return null;
            }
        };

        waitForData.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                if (category_box.getItems().contains(categoryName)) {
                    category_box.getSelectionModel().select(categoryName);
                } else {
                    String mainCategoryName = null;
                    try {
                        mainCategoryName = Data.userDatabase.getMainCategoryName(categoryName);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    category_box.getSelectionModel().select(mainCategoryName);
                    populateSubCategories();
                    sub_category_box.getSelectionModel().select(categoryName);
                }
            });
        });

        executorService.submit(waitForData);
    }


    public void shutdownExecutor() {
        executorService.shutdown();
    }

    public void clear() {
        Platform.runLater(() -> {
            category_box.getItems().clear();
            sub_category_box.getItems().clear();
        });
    }

    public void discard() {
        Platform.runLater(() -> {
            category_box.getSelectionModel().clearSelection();
            sub_category_box.getSelectionModel().clearSelection();
        });
    }

    public void animateError() {
        FieldAnimationUtils.animateFieldError(category_box);
    }

    public void removeError() {
        FieldAnimationUtils.removeErrorStyles(category_box);
    }

    private void setComboBoxCellSize(ComboBox<String> comboBox, double maxWidth) {
        comboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    setPrefWidth(maxWidth);
                    setMaxWidth(maxWidth);
                } else {
                    setText(null);
                }
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    setPrefWidth(maxWidth);
                    setMaxWidth(maxWidth);
                } else {
                    setText(null);
                }
            }
        });
    }
}

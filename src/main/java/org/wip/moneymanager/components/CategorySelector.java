package org.wip.moneymanager.components;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.utility.FieldAnimationUtils;

import java.io.IOException;
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
        category_box.setOnAction(event -> populateSubCategories());
    }

    private void populateMainCategoriesByType(int type) {
        Task<List<String>> taskCategory = Data.userDatabase.getMainCategoryNamesByType(type);
        clear();

        taskCategory.setOnSucceeded(event -> {
            List<String> mainCategories = taskCategory.getValue();
            Platform.runLater(() -> {
                category_box.getItems().addAll(mainCategories);
            });
        });

        taskCategory.setOnFailed(event -> {
            Throwable ex = taskCategory.getException();
            System.err.println("Errore durante il caricamento delle categorie principali: " + ex.getMessage());
            ex.printStackTrace();
        });

        executorService.submit(taskCategory);
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
            Task<List<String>> taskSubCategory = Data.userDatabase.getSubCategoriesByMainCategory(selectedMainCategory);
            taskSubCategory.setOnSucceeded(event -> {
                List<String> subCategories = taskSubCategory.getValue();
                Platform.runLater(() -> {
                    sub_category_box.getItems().clear();
                    sub_category_box.getItems().addAll(subCategories);
                });
            });

            taskSubCategory.setOnFailed(event -> {
                Throwable ex = taskSubCategory.getException();
                System.err.println("Errore durante il caricamento delle sottocategorie: " + ex.getMessage());
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

    public void shutdownExecutor() {
        executorService.shutdown();
    }

    public void clear() {
        category_box.getItems().clear();
        sub_category_box.getItems().clear();
    }

    public void animateError() {
        FieldAnimationUtils.animateFieldError(category_box);
    }
    public void removeError() {
        FieldAnimationUtils.removeErrorStyles(category_box);
    }


}

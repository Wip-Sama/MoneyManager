package org.wip.moneymanager.popUp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import org.wip.moneymanager.components.TagFilter;
import org.wip.moneymanager.components.TagSelector;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.pages.Accounts;
import org.wip.moneymanager.utility.FieldAnimationUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javafx.scene.paint.Color.rgb;


public class AddNewTagController extends BorderPane {
    @FXML
    private Label ErrorLabel;

    @FXML
    private Button addButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox<String> colorComboBox;

    @FXML
    private Label labelColor;

    @FXML
    private Label labelName;

    @FXML
    private Label labelPreview;

    @FXML
    private BorderPane popUpAddTags;

    @FXML
    private ToggleButton previewToggleButton;

    @FXML
    private TextField tagNameField;

    private double xOffset = 0;
    private double yOffset = 0;
    private Accounts accountsPage;;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    public AddNewTagController(Window window) throws IOException {
        this.accountsPage = accountsPage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/popUp/addNewTag.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            Parent addTag = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        Data.esm.register(executorService);
        previewToggleButton.setStyle("-fx-background-color: -fu-accent;");
        ErrorLabel.setOpacity(0);
        addButton.setText(Data.lsp.lsb("addNewTag.addButtonLabel").get());
        cancelButton.setText(Data.lsp.lsb("addNewTag.cancelButtonLabel").get());
        labelColor.setText(Data.lsp.lsb("addNewTag.colorLabel").get());
        labelName.setText(Data.lsp.lsb("addNewTag.nameLabel").get());
        labelPreview.setText(Data.lsp.lsb("addNewTag.previewLabel").get());
        ErrorLabel.textProperty().bind(Data.lsp.lsb("addNewTag.error"));
        previewToggleButton.textProperty().bind(Data.lsp.lsb("addNewTag.previewButton"));


        colorComboBox.getItems().addAll(
                Data.lsp.lsb("color.red").get(),
                Data.lsp.lsb("color.blue").get(),
                Data.lsp.lsb("color.green").get(),
                Data.lsp.lsb("color.yellow").get(),
                Data.lsp.lsb("color.orange").get()
        );

        tagNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePreview();
            FieldAnimationUtils.removeErrorStyles(tagNameField);
            ErrorLabel.setOpacity(0);
        });

        colorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updatePreview();
            FieldAnimationUtils.removeErrorStyles(colorComboBox);
            ErrorLabel.setOpacity(0);
        });


        cancelButton.setOnAction(event -> {
            clearFields();
            TagSelector.closeAddNewTag();
        });

        addButton.setOnAction(event -> {
            if (validateFields()) {
                addTag();
            }
        });

        tagNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePreview();
        });
    }

    private void addTag() {
        String tagName = tagNameField.getText().trim();
        String selectedColor = colorComboBox.getValue();
        if (selectedColor != null && !tagName.isEmpty()) {
            String colorHex = getColorHex(selectedColor);
            Task<Boolean> taskAddTag =  Data.userDatabase.createTag(tagName, colorHex);
            executorService.submit(taskAddTag);

            taskAddTag.setOnSucceeded(workerStateEvent -> {
                TagFilter.refreshTags();
            });

            clearFields();
        } else {
            showError("addNewTag.error");
        }
    }


    private String getColorHex(String colorName) {
        return switch (colorName) {
            case "Rosso", "Red" -> "#FF0000";
            case "Blu", "Blue" -> "#0000FF";
            case "Verde", "Green" -> "#008000";
            case "Giallo", "Yellow" -> "#cfb721";
            case "Arancione", "Orange" -> "#FFA500";
            default -> "-fu-accent"; // Default a bianco
        };
    }

    private boolean validateFields() {
        String tagName = tagNameField.getText();
        String selectedColor = colorComboBox.getValue();
        boolean hasError = false;

        if (tagName == null || tagName.trim().isEmpty()) {
            FieldAnimationUtils.animateFieldError(tagNameField);
            hasError = true;
        }

        if (selectedColor == null || selectedColor.isEmpty()) {
            FieldAnimationUtils.animateFieldError(colorComboBox);
            hasError = true;
        }

        if (hasError) {
            showError("addNewTag.error");
        }

        return !hasError;
    }

    private void showError(String message) {
        ErrorLabel.textProperty().bind(Data.lsp.lsb(message));
        ErrorLabel.setTextFill(rgb(255, 0, 0));
        Timeline fadeInTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> ErrorLabel.setOpacity(0)),
                new KeyFrame(Duration.seconds(0.2), e -> ErrorLabel.setOpacity(1))
        );
        fadeInTimeline.setCycleCount(1);
        fadeInTimeline.play();
    }

    private void updatePreview() {
        String tagName = tagNameField.getText();
        String selectedColor = colorComboBox.getValue();
        String colorHex = (selectedColor != null) ? getColorHex(selectedColor) : "-fu-accent";

        previewToggleButton.setText((tagName == null || tagName.isEmpty()) ? "Tag" : tagName);
        previewToggleButton.setStyle("-fx-background-color: " + colorHex + ";");
    }

    private void clearFields() {
        tagNameField.clear();
        colorComboBox.getSelectionModel().selectFirst();
        updatePreview();
    }

}

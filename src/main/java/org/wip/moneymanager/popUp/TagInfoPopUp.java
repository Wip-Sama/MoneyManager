package org.wip.moneymanager.popUp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import javafx.util.Duration;
import org.wip.moneymanager.components.TagHandler;
import org.wip.moneymanager.model.DBObjects.dbTag;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.utility.FieldAnimationUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TagInfoPopUp extends BorderPane {
    @FXML
    private Label ErrorLabel;
    @FXML
    private ComboBox<String> colorComboBox;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;
    @FXML
    private Label labelColor;
    @FXML
    private Label labelName;
    @FXML
    private Label labelPreview;
    @FXML
    private ToggleButton previewToggleButton;
    @FXML
    private TextField tagNameField;

    private dbTag tag;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final TagHandler tagHandler;
    private Mode currentMode;

    public enum Mode {
        ADD,
        EDIT
    }

    public TagInfoPopUp(TagHandler tagHandler, Mode mode) {
        this.tagHandler = tagHandler;
        this.currentMode = mode;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/popUp/infoTag.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        Data.esm.register(executorService);
        initializeUI();
        initializeListeners();
        setMode(currentMode);
    }

    private void initializeUI() {
        ErrorLabel.setOpacity(0);
        colorComboBox.getItems().addAll("Red", "Blue", "Green", "Yellow", "Orange");
        resetFields();
    }

    private void initializeListeners() {
        colorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updatePreview();
            FieldAnimationUtils.removeErrorStyles(colorComboBox);
            ErrorLabel.setOpacity(0);
        });

        tagNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            tagNameField.setText(newValue.toUpperCase());
            tagNameField.positionCaret(newValue.length());
            updatePreview();
            FieldAnimationUtils.removeErrorStyles(tagNameField);
            ErrorLabel.setOpacity(0);
        });
    }

    public void setMode(Mode mode) {
        this.currentMode = mode;
        if (mode == Mode.ADD) {
            setupAddMode();
        } else if (mode == Mode.EDIT && tag != null) {
            setupEditMode();
        }
    }

    public void setTag(dbTag tag) {
        this.tag = tag;
        if (tag != null) {
            setupEditMode();
        } else {
            resetFields();
        }
    }

    private void setupAddMode() {
        resetFields();
        tagNameField.setDisable(false);
        colorComboBox.setDisable(false);

        editButton.setText(Data.lsp.lsb("tagInfo.saveButtonLabel").get());
        editButton.setOnAction(event -> {
            if (validateFields()) {
                addTag();
            }
        });

        deleteButton.setText(Data.lsp.lsb("tagInfo.cancelButtonLabel").get());
        deleteButton.setOnAction(event -> {
            clearFields();
            tagHandler.closeInfoTag();
        });
    }

    private void setupEditMode() {
        tagNameField.setText(tag.name());
        tagNameField.setDisable(true);

        colorComboBox.setValue(getColorName(tag.color()));
        colorComboBox.setDisable(true);

        editButton.setText(Data.lsp.lsb("tagInfo.editButtonLabel").get());
        editButton.setOnAction(event -> enableEditFields());

        deleteButton.setText(Data.lsp.lsb("tagInfo.deleteButtonLabel").get());
        deleteButton.setOnAction(event -> deleteTag());
    }

    private void enableEditFields() {
        tagNameField.setDisable(false);
        colorComboBox.setDisable(false);
        editButton.setText(Data.lsp.lsb("tagInfo.saveButtonLabel").get());
        editButton.setOnAction(event -> {
            if (validateFields()) {
                saveChanges();
            }
        });

        deleteButton.setText(Data.lsp.lsb("tagInfo.cancelButtonLabel").get());
        deleteButton.setOnAction(event -> {
            setupEditMode();
        });
    }

    private void addTag() {
        String tagName = tagNameField.getText().trim();
        String selectedColor = colorComboBox.getValue();

        if (!tagName.isEmpty() && selectedColor != null) {
            String colorHex = getColorHex(selectedColor);
            Task<Boolean> taskAddTag = Data.userDatabase.createTag(tagName, colorHex);

            taskAddTag.setOnSucceeded(workerStateEvent -> {
                if (!taskAddTag.getValue()) {
                    showError("addNewTag.duplicate");
                    FieldAnimationUtils.animateFieldError(tagNameField);
                } else {
                    tagHandler.refreshTags();
                    clearFields();
                    tagHandler.closeInfoTag();
                }
            });

            taskAddTag.setOnFailed(workerStateEvent -> showError("addNewTag.error"));

            executorService.submit(taskAddTag);
        } else {
            showError("addNewTag.error");
        }
    }

    private void clearFields() {
        resetFields();
        tagHandler.closeInfoTag();
    }

    private void resetFields() {
        tagNameField.clear();
        colorComboBox.getSelectionModel().clearSelection();
        resetPreviewButton();
    }

    private void resetPreviewButton() {
        previewToggleButton.setText("Preview");
        previewToggleButton.setStyle("-fx-background-color: -fu-accent;");
    }

    private void saveChanges() {
        String newName = tagNameField.getText().trim();
        String newColor = colorComboBox.getValue();

        if (!newName.isEmpty() && newColor != null) {
            try {
                tag.setName(newName);
                tag.setColor(getColorHex(newColor));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            disableEditMode();
            tagHandler.refreshTags();
        } else {
            showError(Data.lsp.lsb("tagInfo.errorMessage").get());
        }
    }

    private void deleteTag() {
        Task<Boolean> deleteTask = Data.userDatabase.removeTag(tag.name());
        deleteTask.setOnSucceeded(event -> {
            tagHandler.refreshTags();
            hide();
        });
    }

    private void hide() {
        Window window = getScene().getWindow();
        window.hide();
    }

    private void disableEditMode() {
        tagNameField.setDisable(true);
        colorComboBox.setDisable(true);
        editButton.setText(Data.lsp.lsb("tagInfo.editButtonLabel").get());
        deleteButton.setText(Data.lsp.lsb("tagInfo.deleteButtonLabel").get());
    }

    private void updatePreview() {
        String tagName = tagNameField.getText();
        String selectedColor = colorComboBox.getValue();
        String colorHex = (selectedColor != null) ? getColorHex(selectedColor) : "-fu-accent";

        previewToggleButton.textProperty().unbind();
        previewToggleButton.setText((tagName == null || tagName.isEmpty()) ? Data.lsp.lsb("tagInfo.previewDefault").get() : tagName);
        previewToggleButton.setStyle("-fx-background-color: " + colorHex + ";");
    }

    private String getColorHex(String colorName) {
        return switch (colorName) {
            case "Red" -> "#FF0000";
            case "Blue" -> "#0000FF";
            case "Green" -> "#008000";
            case "Yellow" -> "#cfb721";
            case "Orange" -> "#FFA500";
            default -> "-fu-accent";
        };
    }

    private String getColorName(String colorHex) {
        return switch (colorHex) {
            case "#FF0000" -> "Red";
            case "#0000FF" -> "Blue";
            case "#008000" -> "Green";
            case "#cfb721" -> "Yellow";
            case "#FFA500" -> "Orange";
            default -> "-fu-accent";
        };
    }

    private void showError(String message) {
        ErrorLabel.textProperty().bind(Data.lsp.lsb(message));
        Timeline fadeInTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> ErrorLabel.setOpacity(0)),
                new KeyFrame(Duration.seconds(0.2), e -> ErrorLabel.setOpacity(1))
        );
        fadeInTimeline.setCycleCount(1);
        fadeInTimeline.play();
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
            showError(Data.lsp.lsb("tagInfo.errorMessage").get());
        }

        return !hasError;
    }
}

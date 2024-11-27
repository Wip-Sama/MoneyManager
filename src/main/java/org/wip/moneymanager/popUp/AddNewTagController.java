package org.wip.moneymanager.popUp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import org.wip.moneymanager.components.TagFilter;
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
    private Label labelTitleAddNewTag;

    @FXML
    private Button addButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ChoiceBox<String> colorChoiceBox;

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
    private Accounts accountsPage;
    private final Popup popup = new Popup();
    private final Window ownerWindow;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    public AddNewTagController(Window window) throws IOException {
        Data.esm.register(executorService);
        this.accountsPage = accountsPage;
        this.ownerWindow = window;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/popUp/addNewTag.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        Parent loadPopup = fxmlLoader.load();

        Scene popupScene = new Scene(loadPopup );
        popupScene.getRoot().setStyle("-fu-accent: " + Data.dbUser.accentProperty().get().getHex() + ";");

        popup.getContent().add(loadPopup);
        window.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, _ -> hide());
    }

    @FXML
    public void initialize() {
        ErrorLabel.setOpacity(0);
        ErrorLabel.setOpacity(0);
        addButton.setText(Data.lsp.lsb("addNewTag.addButtonLabel").get());
        cancelButton.setText(Data.lsp.lsb("addNewTag.cancelButtonLabel").get());
        labelTitleAddNewTag.setText(Data.lsp.lsb("addNewTag.titleLabel").get());
        labelColor.setText(Data.lsp.lsb("addNewTag.colorLabel").get());
        labelName.setText(Data.lsp.lsb("addNewTag.nameLabel").get());
        labelPreview.setText(Data.lsp.lsb("addNewTag.previewLabel").get());
        ErrorLabel.textProperty().bind(Data.lsp.lsb("addNewTag.error"));


        colorChoiceBox.getItems().addAll("Rosso", "Blu", "Verde", "Giallo", "Arancione");

        tagNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePreview();
            FieldAnimationUtils.removeErrorStyles(tagNameField);
            ErrorLabel.setOpacity(0);
        });

        colorChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updatePreview();
            FieldAnimationUtils.removeErrorStyles(colorChoiceBox);
            ErrorLabel.setOpacity(0);
        });
        popUpAddTags.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        popUpAddTags.setOnMouseDragged(event -> {
            popup.setX(event.getScreenX() - xOffset);
            popup.setY(event.getScreenY() - yOffset);
        });

        cancelButton.setOnAction(event -> {
            clearFields();
            hide();
        });

        addButton.setOnAction(event -> {
            if (validateFields()) {
                addTag();
            }
        });
    }

    private void addTag() {
        String tagName = tagNameField.getText().trim();
        String selectedColor = colorChoiceBox.getValue();
        if (selectedColor != null && !tagName.isEmpty()) {
            String colorHex = getColorHex(selectedColor);
            Task<Boolean> taskAddTag =  Data.userDatabase.createTag(tagName, colorHex);
            executorService.submit(taskAddTag);

            taskAddTag.setOnSucceeded(workerStateEvent -> {
                TagFilter.refreshTags();
            });

            clearFields();
            hide();
        } else {
            showError("addNewTag.error");
        }
    }


    private String getColorHex(String colorName) {
        return switch (colorName) {
            case "Rosso" -> "#FF0000";
            case "Blu" -> "#0000FF";
            case "Verde" -> "#008000";
            case "Giallo" -> "#cfb721";
            case "Arancione" -> "#FFA500";
            default -> "#FFFFFF"; // Default a bianco
        };
    }

    private boolean validateFields() {
        String tagName = tagNameField.getText();
        String selectedColor = colorChoiceBox.getValue();
        boolean hasError = false;

        if (tagName == null || tagName.trim().isEmpty()) {
            FieldAnimationUtils.animateFieldError(tagNameField);
            hasError = true;
        }

        if (selectedColor == null || selectedColor.isEmpty()) {
            FieldAnimationUtils.animateFieldError(colorChoiceBox);
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
        String selectedColor = colorChoiceBox.getValue();

        if (selectedColor != null) {
            String colorHex = getColorHex(selectedColor);
            previewToggleButton.setText(tagName.isEmpty() ? "Anteprima" : tagName);
            previewToggleButton.setStyle("-fx-background-color: " + colorHex + ";");
        }
    }

    private void clearFields() {
        tagNameField.clear();
        colorChoiceBox.getSelectionModel().selectFirst();
        updatePreview();
    }

    public void show() {
        popup.show(ownerWindow);
    }

    public void hide() {
        popup.hide();
    }

}

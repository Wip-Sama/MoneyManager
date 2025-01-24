package org.wip.moneymanager.pages;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.components.ComboPasswordField;
import org.wip.moneymanager.components.Switch;
import org.wip.moneymanager.model.DBObjects.dbUser;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.utility.AlertMessages;
import org.wip.moneymanager.utility.Encrypter;
import org.wip.moneymanager.utility.FieldAnimationUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Profile extends BorderPane implements AutoCloseable {

    @FXML
    private Label EditProfileLabel;

    @FXML
    private Button discardButton;

    @FXML
    private Label errorLabel;

    @FXML
    private ImageView image;

    @FXML
    private ComboPasswordField newPasswordField;

    @FXML
    private Label newPasswordLabel;

    @FXML
    private ComboPasswordField oldPasswordField;

    @FXML
    private Label oldPasswordLabel;

    @FXML
    private Pane profileImage;

    @FXML
    private Button saveButton;

    @FXML
    private TextField usernameField;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label usernamePreview;



    private final static Image mm_logo = new Image(Objects.requireNonNull(SceneHandler.class.getResourceAsStream("/org/wip/moneymanager/images/Logo_Money_manager_single.svg.png")));
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Profile() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneHandler.class.getResource("/org/wip/moneymanager/pages/profile.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Circle clip = new Circle(75, 75, 75);
        image.setClip(clip);
    }

    @FXML
    public void initialize() {
        Data.esm.register(executorService);
        errorLabel.setOpacity(0);
        oldPasswordLabel.textProperty().bind(Data.lsp.lsb("profile.old_password"));
        saveButton.textProperty().bind(Data.lsp.lsb("profile.save"));
        discardButton.textProperty().bind(Data.lsp.lsb("profile.discard"));
        usernameLabel.textProperty().bind(Data.lsp.lsb("profile.username"));
        newPasswordLabel.textProperty().bind(Data.lsp.lsb("profile.new_password"));
        EditProfileLabel.textProperty().bind(Data.lsp.lsb("profile.edit_pic"));

        usernamePreview.textProperty().bind(Data.dbUser.username());
        usernameField.setText(Data.dbUser.username().get());

        File directory = new File(Data.users_images_directory);
        String username = String.valueOf(Data.dbUser.id());
        File[] files = directory.listFiles((_, name) -> name.startsWith(username + "."));
        if (files != null && files.length > 0) {
            System.out.println(files[0].getPath());
            Image newImage = new Image(files[0].toURI().toString());
            image.setImage(newImage);

        } else {
            image.setImage(mm_logo);
        }

        profileImage.onMouseClickedProperty().set(_ -> {
            String user_id = String.valueOf(Data.dbUser.id());

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );

            File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (selectedFile == null) return;

            String fileName = selectedFile.getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf('.'));
            String newFileName = user_id + "_tmp" + fileExtension;

            File newFile = new File(Data.users_images_directory, newFileName);
            try {
                Files.copy(selectedFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Image imageFile = new Image(newFile.toURI().toString());
                image.setImage(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        saveButton.setOnAction(_ -> {
            update_stuff();
            update_pic();

            Data.userUpdated.set(true);


        });

        discardButton.setOnAction(_ -> {
            usernameField.setText(Data.dbUser.username().get());


            File dir = new File(Data.users_images_directory);
            String user_id = String.valueOf(Data.dbUser.id());

            File[] old_files = dir.listFiles((_, name) -> name.startsWith(user_id + "."));
            File[] new_files = dir.listFiles((_, name) -> name.startsWith(user_id + "_tmp."));

            if (new_files != null && new_files.length > 0) {
                new_files[0].delete();
            }

            assert old_files != null;
            String fileExtension = old_files[0].getName().substring(old_files[0].getName().lastIndexOf('.'));
            File oldFile = new File(Data.users_images_directory, user_id + fileExtension);
            image.setImage(new Image(oldFile.toURI().toString()));
        });

        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                FieldAnimationUtils.removeErrorStyles(usernameField);
                errorLabel.setOpacity(0);
            }
        });

        oldPasswordField.password.addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                FieldAnimationUtils.removeErrorStyles(oldPasswordField);
                errorLabel.setOpacity(0);
            }
        });

        newPasswordField.password.addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                FieldAnimationUtils.removeErrorStyles(newPasswordField);
                errorLabel.setOpacity(0);
            }
        });

    }

    private void update_pic() {
        File directory = new File(Data.users_images_directory);
        String user_id = String.valueOf(Data.dbUser.id());

        File[] old_files = directory.listFiles((_, name) -> name.startsWith(user_id + "."));
        File[] new_files = directory.listFiles((_, name) -> name.startsWith(user_id + "_tmp."));

        if (new_files != null && new_files.length > 0) {
            if (old_files != null && old_files.length > 0) {
                if (old_files[0].exists()) {
                    old_files[0].delete();
                }
            }
            String fileExtension = new_files[0].getName().substring(new_files[0].getName().lastIndexOf('.'));
            File newFile = new File(Data.users_images_directory, user_id + fileExtension);
            new_files[0].renameTo(newFile);
            image.setImage(new Image(newFile.toURI().toString()));
        }
    }

    private void update_stuff() {
        errorLabel.setOpacity(0); // Nascondi l'errore all'inizio

        boolean hasError = false;

        if (oldPasswordField.password.get() == null || oldPasswordField.password.get().isEmpty()) {
            FieldAnimationUtils.animateFieldError(oldPasswordField);
            hasError = true;
        }
        if (newPasswordField.password.get() == null || newPasswordField.password.get().isEmpty()) {
            FieldAnimationUtils.animateFieldError(newPasswordField);
            hasError = true;
        }
        if (usernameField.getText().isEmpty()) {
            FieldAnimationUtils.animateFieldError(usernameField);
            hasError = true;
        }

        if (hasError) {
            showError("profile.error.all_fields", "red");
            return;
        }

        Task<Boolean> checkPasswordTask = Data.mmDatabase.checkPassword(Data.dbUser.username().get(), oldPasswordField.password.get());
        checkPasswordTask.setOnSucceeded(event -> {
            boolean isOldPasswordCorrect = checkPasswordTask.getValue();

            if (!isOldPasswordCorrect) {
                showError("profile.error.old_password", "red");
                FieldAnimationUtils.animateFieldError(oldPasswordField);
                return;
            }

            try {
                if (!Objects.equals(usernameField.getText(), Data.dbUser.username().get())) {
                    Data.dbUser.setUsername(usernameField.getText());
                }
                if (!newPasswordField.password.get().isEmpty()) {
                    Data.dbUser.setPassword_hash(Encrypter.encrypt_string_bcrypt(newPasswordField.password.get()));
                }
            } catch (SQLException e) {
                showError("profile.error.all_fields", "red");
                return;
            }

            showError("profile.success", "green");

            usernameField.setText(Data.dbUser.username().get());
            oldPasswordField.clear();
            newPasswordField.clear();
        });

        executorService.submit(checkPasswordTask);
    }

    private void showError(String message, String color) {
        errorLabel.textProperty().unbind();
        errorLabel.textProperty().bind(Data.lsp.lsb(message));
        errorLabel.setStyle("-fx-text-fill: " + color + ";");
        errorLabel.setOpacity(1);

        Timeline fadeInTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> errorLabel.setOpacity(0)),
                new KeyFrame(Duration.seconds(0.2), e -> errorLabel.setOpacity(1))
        );
        fadeInTimeline.setCycleCount(1);
        fadeInTimeline.play();
    }


    @Override
    public void close() {
        executorService.shutdown();
    }

}

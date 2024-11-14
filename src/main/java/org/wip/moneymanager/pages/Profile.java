package org.wip.moneymanager.pages;

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
import org.wip.moneymanager.View.MoneyManager;
import org.wip.moneymanager.components.ComboPasswordField;
import org.wip.moneymanager.components.Switch;
import org.wip.moneymanager.model.DBObjects.dbUser;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.utility.Encrypter;

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
    private ImageView pic;
    @FXML
    private Label username_show;
    @FXML
    private Label username_label;
    @FXML
    private TextField username_field;
    @FXML
    private Label new_password_label;
    @FXML
    private ComboPasswordField new_password_field;
    @FXML
    private Label use_password_label;
    @FXML
    private Switch use_password_field;
    @FXML
    private Button save;
    @FXML
    private Button discard;
    @FXML
    private Label profile_label;
    @FXML
    private Pane profile_pane;

    private final static Image mm_logo = new Image(Objects.requireNonNull(MoneyManager.class.getResourceAsStream("/org/wip/moneymanager/images/Logo_Money_manager_single.svg.png")));
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Profile() {
        Data.esm.register(executorService);
        try {
            FXMLLoader loader = new FXMLLoader(MoneyManager.class.getResource("/org/wip/moneymanager/pages/profile.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Circle clip = new Circle(75, 75, 75);
        pic.setClip(clip);
    }

    @FXML
    public void initialize() {
        save.textProperty().bind(Data.lsp.lsb("profile.save"));
        discard.textProperty().bind(Data.lsp.lsb("profile.discard"));
        username_label.textProperty().bind(Data.lsp.lsb("profile.username"));
        new_password_label.textProperty().bind(Data.lsp.lsb("profile.new_password"));
        use_password_label.textProperty().bind(Data.lsp.lsb("profile.use_password"));
        profile_label.textProperty().bind(Data.lsp.lsb("profile.edit_pic"));

        username_show.textProperty().bind(Data.dbUser.username());
        username_field.setText(Data.dbUser.username().get());
        use_password_field.setState(Data.dbUser.safe_login().get());

        File directory = new File(Data.users_images_directory);
        String username = String.valueOf(Data.dbUser.id());
        File[] files = directory.listFiles((_, name) -> name.startsWith(username + "."));
        if (files != null && files.length > 0) {
            System.out.println(files[0].getPath());
            Image newImage = new Image(files[0].toURI().toString());
            pic.setImage(newImage);
        } else {
            pic.setImage(mm_logo);
        }

        profile_pane.onMouseClickedProperty().set(_ -> {
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
                Image image = new Image(newFile.toURI().toString());
                pic.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        save.setOnAction(_ -> {
            update_stuff();
            update_pic();
            username_field.setText(Data.dbUser.username().get());
            use_password_field.updateState(Data.dbUser.safe_login().get());
            Data.userUpdated.set(true);
        });
        discard.setOnAction(_ -> {
            username_field.setText(Data.dbUser.username().get());
            use_password_field.updateState(Data.dbUser.safe_login().get());

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
            pic.setImage(new Image(oldFile.toURI().toString()));
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
            pic.setImage(new Image(newFile.toURI().toString()));
        }
    }

    private void update_stuff() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Task<dbUser> dbUser = Data.mmDatabase.getUser(username_field.getText());
        executorService.submit(dbUser);
        try {
            if (dbUser.getValue() != null && !dbUser.getValue().username().get().equals(Data.dbUser.username().get())) {
                alert.setContentText("Username already exists");
                alert.showAndWait();
                return;
            }
            if (use_password_field.getState() && (new_password_field.password.get() == null || new_password_field.password.get().isEmpty())) {
                alert.setContentText("Password cannot be empty if you want to use it");
                alert.showAndWait();
                return;
            }
            if (new_password_field.password.get() != null && !new_password_field.password.get().isEmpty() && new_password_field.password.get().matches("[a-zA-Z0-9_]")) {
                alert.setContentText("Password must contain at least one special character");
                alert.showAndWait();
                return;
            }

            Data.dbUser.setUsername(username_field.getText());
            Data.dbUser.setSafe_login(use_password_field.getState());
            if (new_password_field.password.get() != null && !new_password_field.password.get().isEmpty())
                Data.dbUser.setPassword_hash(Encrypter.encrypt_string_bcrypt(new_password_field.password.get()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}

package org.wip.moneymanager.components;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import org.wip.moneymanager.model.DBObjects.dbTag;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.ExecutorsServiceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TagFilter extends BorderPane {
    @FXML
    public TextField search_bar;

    @FXML
    public FlowPane tag_pane;

    @FXML
    public ScrollPane scroll_pane;

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final ArrayList<Tag> tagsList = new ArrayList<>();
    private static final ObservableList<Tag> observableTagList = FXCollections.observableArrayList(tagsList);
    private static final ListProperty<Tag> tags = new SimpleListProperty<>(observableTagList);

    public static void refreshTags() {
        tags.clear();  // Azzera i tag attuali
        initializeTags();  // Ricarica i tag
    }

    public ReadOnlyListProperty<Tag> tagsProperty() {
        return tags;
    }

    public TagFilter() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/tagfilter.fxml"));
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
        tags.addListener((_, _, newValue) -> {
            tag_pane.getChildren().clear();
            tag_pane.getChildren().addAll(newValue);
            for (Tag tag : newValue) {
                search_bar.textProperty().addListener((_, _, newValue1) -> {
                    if (newValue1.isEmpty()) {
                        tag.setVisible(true);
                    } else {
                        tag.setVisible(tag.tagProperty().get().contains(newValue1));
                    }
                });
                tag.managedProperty().bindBidirectional(tag.visibleProperty());
            }
        });
        initializeTags();
    }

    private static void initializeTags() {
        Task<List<dbTag>> loadTagsTask = Data.userDatabase.getAllTag();
        loadTagsTask.setOnSucceeded(event -> {
            List<dbTag> dbTags = loadTagsTask.getValue();
            if (dbTags != null) {
                // Aggiungi solo tag non duplicati
                for (dbTag dbTagItem : dbTags) {
                    boolean exists = tags.stream()
                            .anyMatch(tag -> tag.getTag().equals(dbTagItem.name()));
                    if (!exists) {
                        Tag tag = new Tag(dbTagItem.name(), 0, 1, dbTagItem.color());
                        tags.add(tag);
                    }
                }
            }
        });
        loadTagsTask.setOnFailed(event -> {
            Throwable exception = loadTagsTask.getException();
            exception.printStackTrace();
        });

        executorService.submit(loadTagsTask);
    }


}


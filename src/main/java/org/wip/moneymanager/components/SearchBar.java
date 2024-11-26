package org.wip.moneymanager.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.UserDatabase;
import org.wip.moneymanager.model.DBObjects.dbTag;
import org.wip.moneymanager.popUp.AddNewAccountController;
import org.wip.moneymanager.popUp.AddNewTagController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchBar extends VBox {

    @FXML
    private Button addTagButton;

    @FXML
    private TextField searchBar;

    @FXML
    private HBox searchBaraAll;

    @FXML
    private Button searchButton;

    @FXML
    private ListView<HBox> tagsListView;



    protected Parent loaded;
    private AddNewTagController AddNewTag;
    private UserDatabase userDatabase = UserDatabase.getInstance();
    private static List<dbTag> selectedTags = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    public SearchBar() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/wip/moneymanager/components/searchbar.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            loaded = fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        Data.esm.register(executorService);

        searchButton.setOnAction(actionEvent -> {

            searchForTags();
        });

        addTagButton.setOnAction(actionEvent -> {
            try {
                if (AddNewTag == null) {
                    AddNewTag = new AddNewTagController(loaded.getScene().getWindow());
                }
                AddNewTag.show(); // Mostra il popup
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


    public static void addToFilter(dbTag tag) {
        selectedTags.add(tag);
    }

    public static void removeToFilter(dbTag tag) {
        selectedTags.remove(tag);
    }


    private void searchForTags() {
        String searchText = searchBar.getText().toLowerCase();

        userDatabase.getAllTag().setOnSucceeded(e -> {
            List<dbTag> allTags = userDatabase.getAllTag().getValue();
            tagsListView.getItems().clear();

            for (dbTag tag : allTags) {
                if (tag.name().toLowerCase().contains(searchText)) {
                    Tag tagNode = new Tag();
                    tagNode.initialize(tag);
                    tagsListView.getItems().add(tagNode);
                }
            }
        });

        userDatabase.getAllTag().setOnFailed(e -> {
            System.err.println("Errore nel recupero dei tag dal database");
        });

        // Esegui il task asincrono per ottenere i tag
        new Thread(userDatabase.getAllTag()).start();
    }
}


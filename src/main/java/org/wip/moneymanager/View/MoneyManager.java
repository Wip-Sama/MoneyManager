package org.wip.moneymanager.View;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.json.JSONException;
import org.json.JSONObject;
import org.wip.moneymanager.model.DBObjects.dbCurrency;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.MMDatabase;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
/*Premessa per il prof:
 * In questo codice troverete diversi metodi apparentemente inutilizzati
 * Ora probabilmente qualcuno è effettivamente inutilizzato ma la maggior parte sono utilizzati da scenebuilder
 * Servono per permetterci di modificare dei valori nel'FXML
 * Altri semplicemente sono buone pratiche, come avere tutti i getter/setter impostati anche se non utilizzati*/

public class MoneyManager extends Application {
    private Set<String> initialThreads;
    @Override
    public void start(Stage stage) throws IOException {
        initialThreads = Thread.getAllStackTraces().keySet().stream()
                .map(Thread::getName)
                .collect(Collectors.toSet());

        setUserAgentStylesheet(getClass().getResource("/org/wip/moneymanager/style-dark.css").toExternalForm());
        FXMLLoader fxmlLoader = new FXMLLoader(MoneyManager.class.getResource("/org/wip/moneymanager/base_menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setMinHeight(540); //MAX 540 (1080/2)
        stage.setMinWidth(960); //MAX 960 (1920/2)
        stage.setTitle("Money Manager");
        Image icon = new Image(Objects.requireNonNull(MoneyManager.class.getResourceAsStream("/org/wip/moneymanager/images/Logo_Money_manager_single.svg.png")));
        stage.getIcons().add(icon);

//        stage.setOnCloseRequest(e -> {
//            System.exit(0);
//        });

        stage.setScene(scene);
        stage.show();

        Task<Void> update_currency = new Task<>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Checking for currency updates...");
                MMDatabase db = MMDatabase.getInstance();
                Task<Boolean> checked = db.checkUpdate_date();
                checked.run();
                if (checked.get() == null || !checked.get()) {
                    System.out.println("Updating currency...");
                    Task<List<dbCurrency>> currencies = db.getAllCurrency();
                    currencies.run();

                    String url = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json";
                    try (HttpClient client = HttpClient.newBuilder()
                            .connectTimeout(Duration.ofSeconds(10))
                            .build()) {
                        HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create(url))
                                .build();
                        HttpResponse<String> response;

                        try {
                            response = client.send(request, HttpResponse.BodyHandlers.ofString());

                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject tmp = jsonObject.getJSONObject("eur");

                            List<dbCurrency> currenciesList = currencies.get();
                            System.out.println(currenciesList.size());


                            for (dbCurrency currency : currenciesList) {
                                if (tmp.has(currency.name())) {
                                    currency.setUpdate_date();
                                    currency.setValue(tmp.getDouble(currency.name()));
                                    tmp.remove(currency.name());
                                }
                            }

                            if (tmp.keySet() != null) {
                                for (String key : tmp.keySet()) {
                                    Task<Boolean> createCurrencyTask = db.createCurrency(key, tmp.getDouble(key));
                                    createCurrencyTask.run();
                                    createCurrencyTask.get();
                                }
                            }
                        } catch (HttpTimeoutException e) {
                            System.err.println("Request timed out: " + e.getMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println("Currency update complete!");
                return null;
            }
        };

        try (ExecutorService executorService = Executors.newCachedThreadPool()) {
            executorService.submit(update_currency);
            executorService.shutdown();
        }

        // Add a ShutdownHook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown Hook is running!");
            Set<String> currentThreads = Thread.getAllStackTraces().keySet().stream()
                    .map(Thread::getName)
                    .collect(Collectors.toSet());
            currentThreads.removeAll(initialThreads);
            for (String threadName : currentThreads) {
                System.out.println("Thread still running: " + threadName);
            }
        }));
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        Data.esm.shutdown();
    }
}
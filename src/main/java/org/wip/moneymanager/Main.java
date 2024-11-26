package org.wip.moneymanager;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;
import org.wip.moneymanager.View.SceneHandler;
import org.wip.moneymanager.model.DBObjects.dbCurrency;
import org.wip.moneymanager.model.DBObjects.dbUser;
import org.wip.moneymanager.model.Data;
import org.wip.moneymanager.model.MMDatabase;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main extends Application {
    private Set<String> initialThreads;
    @Override
    public void start(Stage primaryStage) {
        initialThreads = Thread.getAllStackTraces().keySet().stream()
                .map(Thread::getName)
                .collect(Collectors.toSet());

        try {
            // Crea un'istanza della classe Login e mostralo
            SceneHandler sceneHandler = SceneHandler.getInstance(primaryStage);
            sceneHandler.showLoginScreen(); // Chiama il metodo per mostrare la schermata di login


            /*per accendere direttamente con utente1
            Task<dbUser> userTask = Data.mmDatabase.getUser("utente1");
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(userTask);

            userTask.setOnSucceeded(e -> {
                Data.dbUser = userTask.getValue();
                sceneHandler.startMoneyManager();
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        //per accendere direttamente con utente1

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
        launch(args);
    }

    @Override
    public void stop() {
        Data.esm.shutdown();
    }
}

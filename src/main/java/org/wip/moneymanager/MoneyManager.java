package org.wip.moneymanager;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.json.JSONException;
import org.json.JSONObject;
import org.wip.moneymanager.model.Currency;
import org.wip.moneymanager.model.MMDatabase;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.sql.*;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/*Premessa per il prof:
 * In questo codice troverete diversi metodi apparentemente inutilizzati
 * Ora probabilmente qualcuno è effettivamente inutilizzato ma la maggior parte sono utilizzati da scenebuilder
 * Servono per permetterci di modificare dei valori nel'FXML
 * Altri semplicemente sono buone pratiche, come avere tutti i getter/setter impostati anche se non utilizzati*/

public class MoneyManager extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        setUserAgentStylesheet("style-dark.css");
        FXMLLoader fxmlLoader = new FXMLLoader(MoneyManager.class.getResource("base_menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setMinHeight(500);
        stage.setMinWidth(640);
        stage.setTitle("Money Manager");

        Image icon = new Image(Objects.requireNonNull(MoneyManager.class.getResourceAsStream("/org/wip/moneymanager/images/Logo_Money_manager_single.svg.png")));
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.show();

        Task<Void> update_currency = new Task<>() {
            @Override
            protected Void call() throws Exception {
                MMDatabase db = MMDatabase.getInstance();
                Task<Boolean> checked = db.checkUpdate_date();
                // Non so perché ma quando l'ho provato se faccio run qui si impalla tutto
                // Ma se non metto run a quelli avanti non va avanti
                if (checked.get() == null || checked.get()) {
                    Task<List<Currency>> currencies = db.getAllCurrency();
                    currencies.run();

                    // Mamma che bellezza sta API
                    // https://www.jsdelivr.com/
                    // https://github.com/fawazahmed0/exchange-api
                    String url = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json";
                    HttpClient client = HttpClient.newBuilder()
                            .connectTimeout(Duration.ofSeconds(10))
                            .build();
                    HttpRequest request = HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create(url))
                            .build();
                    HttpResponse<String> response;

                    try {
                        response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONObject tmp = jsonObject.getJSONObject("eur");

                        // Aggiorniamo le valute che esistono
                        if (currencies.get() != null) {
                            for (Currency currency : currencies.get()) {
                                if (tmp.has(currency.name())) {
                                    currency.setUpdate_date();
                                    currency.setValue(tmp.getDouble(currency.name()));
                                    tmp.remove(currency.name());
                                }
                            }
                        }
                        // Creiamo le valute che non esistono
                        if (tmp.keySet() != null) {
                            for (String key : tmp.keySet()) {
                                Task<Boolean> createCurrencyTask = db.createCurrency(key, tmp.getDouble(key));
                                createCurrencyTask.run();
                            }
                        }
                    } catch (HttpTimeoutException e) {
                        System.err.println("Request timed out: " + e.getMessage());
                        // TODO: Aggiungere un alert o comunque un qualcosa che avvisi l'utente
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        Thread t = new Thread(update_currency);
        t.setDaemon(true) ;
        t.start();
    }

    public static void main(String[] args) {
        launch();
    }
}
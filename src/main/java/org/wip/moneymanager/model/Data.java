package org.wip.moneymanager.model;

import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class Data {
    // Non serve ma volevo metteralo lo stesso
    public static final MMDatabase mmDatabase = MMDatabase.getInstance();

    //Questo potrebbe esere utile... ma non so bene perché finché l'utente non fa il login getIstance non funziona
    //public static final UserDatabase = UserDatabase.getInstance();
    //Potrebbe anche esserer comodo salvare User qui, dato che è un oggetto comune, averlo a portata di mano potrebbe convenire

    //public static String user = null;
    // Solo per i test
    public static String user = "user";

    public static UserDatabase userDatabase;

    public static final ObjectProperty<Theme> theme = new SimpleObjectProperty<>(Theme.DARK);

    private static final IntegerProperty busy = new SimpleIntegerProperty(0);

    public static ReadOnlyIntegerProperty busyProperty() {
        return busy;
    }

    public static void subscribe_busy() {
        busy.set(busy.get() + 1);
    }

    public static void unsubscribe_busy() {
        busy.set(busy.get() - 1);
        if (busy.get() < 0) {
            busy.set(0);
        }
    }
}

package org.wip.moneymanager.model;

import javafx.beans.property.*;
import org.wip.moneymanager.model.types.User;

public class Data {
    // Non serve particolarmente ma volevo metteralo lo stesso
    public static final MMDatabase mmDatabase = MMDatabase.getInstance();

    public static UserDatabase userDatabase;

    // public static String user = null;
    // Solo per i test
    public static String username = "user";

    public static User user;

    // BUSY status
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
            throw new IllegalStateException("Busy counter is negative");
            //busy.set(0);
        }
    }
}

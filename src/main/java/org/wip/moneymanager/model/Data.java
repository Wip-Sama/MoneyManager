package org.wip.moneymanager.model;

import javafx.beans.property.*;
import org.wip.moneymanager.model.DBObjects.dbUser;

public class Data {
    // Non serve particolarmente ma volevo metteralo lo stesso
    public static final MMDatabase mmDatabase = MMDatabase.getInstance();

    public static UserDatabase userDatabase;

    public static final LocalizationService localizationService = new LocalizationService("en");
    public static final LocalizationService lsp = localizationService;

    // Solo per i test "user"
    public static String username = "user";

    public static dbUser dbUser;

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

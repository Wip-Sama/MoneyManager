package org.wip.moneymanager.model;

import javafx.beans.property.*;
import org.wip.moneymanager.model.DBObjects.dbUser;

public class Data {
    // Non serve particolarmente ma volevo metteralo lo stesso
    public static final MMDatabase mmDatabase = MMDatabase.getInstance();

    public static UserDatabase userDatabase;
    public static final BooleanProperty userUpdated = new SimpleBooleanProperty(false);

    public static final LocalizationService localizationService = new LocalizationService("en");
    public static final LocalizationService lsp = localizationService;

    public static dbUser dbUser;

    public static final ExecutorsServiceManager esm = new ExecutorsServiceManager();

    // BUSY counter
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

    public static final String users_images_directory = "Data/user_imgs";
}

package org.wip.moneymanager.model.types;

import org.wip.moneymanager.model.Data;

public enum HomeScreen {
    NONE,
    TRANSACTIONS,
    ACCOUNTS,
    STATISTICS;

    public static HomeScreen fromString(String home_screen) {
        home_screen = home_screen.toLowerCase();
        if (Data.lsp.lsb("none").get().toLowerCase().equals(home_screen)) {
            return HomeScreen.NONE;
        } else if (Data.lsp.lsb("transactions").get().toLowerCase().equals(home_screen)) {
            return HomeScreen.TRANSACTIONS;
        } else if (Data.lsp.lsb("accounts").get().toLowerCase().equals(home_screen)) {
            return HomeScreen.ACCOUNTS;
        } else if (Data.lsp.lsb("statistics").get().toLowerCase().equals(home_screen)) {
            return HomeScreen.STATISTICS;
        }
        return null;
    }

    public static String toString(HomeScreen home_screen) {
        return Data.lsp.lsb(home_screen.toString().toLowerCase()).get();
    }

    public static HomeScreen fromInt(int type) {
        if (type < 0 || type >= values().length) {
            return null;
        }
        return values()[type];
    }
}

package org.wip.moneymanager.model.types;

public enum HomeScreen {
    NONE,
    TRANSACTIONS,
    ACCOUNTS,
    STATISTICS;

    public static HomeScreen fromString(String home_screen) {
        home_screen = home_screen.toLowerCase();
        return switch (home_screen) {
            case "none" -> NONE;
            case "transactions" -> TRANSACTIONS;
            case "accounts" -> ACCOUNTS;
            case "statistics" -> STATISTICS;
            default -> null;
        };
    }

    public static String toString(HomeScreen home_screen) {
        return switch (home_screen) {
            case NONE -> "None";
            case TRANSACTIONS -> "Transactions";
            case ACCOUNTS -> "Accounts";
            case STATISTICS -> "Statistics";
        };
    }
}

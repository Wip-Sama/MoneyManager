package org.wip.moneymanager.model;

public enum Theme {
    SYSTEM,
    LIGHT,
    DARK;

    public static Theme fromString(String theme) {
        theme = theme.toLowerCase();
        return switch (theme) {
            case "system" -> SYSTEM;
            case "light" -> LIGHT;
            case "dark" -> DARK;
            default -> null;
        };
    }

    public static String toString(Theme theme) {
        return switch (theme) {
            case SYSTEM -> "System";
            case LIGHT -> "Light";
            case DARK -> "Dark";
        };
    }

    public static Theme fromInt(int theme) {
        return switch (theme) {
            case 0 -> SYSTEM;
            case 1 -> LIGHT;
            case 2 -> DARK;
            default -> null;
        };
    }
}

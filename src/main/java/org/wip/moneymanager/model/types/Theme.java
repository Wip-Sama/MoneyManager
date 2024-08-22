package org.wip.moneymanager.model.types;

import org.wip.moneymanager.model.Data;

public enum Theme {
    SYSTEM,
    LIGHT,
    DARK;

    // Andrebbe sistemato, il problema è che se abbiamo più linge dovremmo
    // Convertire la localizzazione a inglese o una lingua comune per poterla usare
    @Deprecated
    public static Theme fromString(String theme) {
        theme = theme.toLowerCase();
        return switch (theme) {
            case "system" -> SYSTEM;
            // System non funziona per il semplice fatto che prenderlo probabilmente cambia tra mac/window/linux ecc
            // quindi non ha senso implementarlo, anche perché non possiamo tertsalo su mac
            case "light" -> LIGHT;
            case "dark" -> DARK;
            default -> null;
        };
    }

    public static String toString(Theme theme) {
        return switch (theme) {
            case SYSTEM -> Data.lsp.lsb("system").get();
            case LIGHT -> Data.lsp.lsb("light").get();
            case DARK -> Data.lsp.lsb("dark").get();
        };
    }

    @Override
    public String toString() {
        return Theme.toString(this);
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

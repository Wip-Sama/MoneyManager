package org.wip.moneymanager.model.types;

import org.wip.moneymanager.model.Data;

public enum Theme {
    SYSTEM,
    LIGHT,
    DARK;

    // Andrebbe sistemato, il problema è che se abbiamo più linge dovremmo
    // Convertire la localizzazione a inglese o una lingua comune per poterla usare
    // Edit: ho cambiato da switch a if, ora teoricamente dovrebbe avere meno problemi
    // c'è sempre però ilc aso in cui theme sia un dato più vecchio della lingua attuale
    // non so come si potrebbe verificare ma è una possibilità
    @Deprecated
    public static Theme fromString(String theme) {
        theme = theme.toLowerCase();
        if (Data.lsp.lsb("system").get().toLowerCase().equals(theme)) {
            return Theme.SYSTEM;
        } else if (Data.lsp.lsb("light").get().toLowerCase().equals(theme)) {
            return Theme.LIGHT;
        } else if (Data.lsp.lsb("dark").get().toLowerCase().equals(theme)) {
            return Theme.DARK;
        }
        return null;
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

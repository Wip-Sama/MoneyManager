package org.wip.moneymanager.model.types;

import org.wip.moneymanager.MoneyManager;
import org.wip.moneymanager.model.Data;

public enum Week {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    @Deprecated
    public static Week fromString(String week) {
        week = week.toLowerCase();
        if (Data.lsp.lsb("monday").get().toLowerCase().equals(week)) {
            return Week.MONDAY;
        } else if (Data.lsp.lsb("tuesday").get().toLowerCase().equals(week)) {
            return Week.TUESDAY;
        } else if (Data.lsp.lsb("wednesday").get().toLowerCase().equals(week)) {
            return Week.WEDNESDAY;
        } else if (Data.lsp.lsb("thursday").get().toLowerCase().equals(week)) {
            return Week.THURSDAY;
        } else if (Data.lsp.lsb("friday").get().toLowerCase().equals(week)) {
            return Week.FRIDAY;
        } else if (Data.lsp.lsb("saturday").get().toLowerCase().equals(week)) {
            return Week.SATURDAY;
        } else if (Data.lsp.lsb("sunday").get().toLowerCase().equals(week)) {
            return Week.SUNDAY;
        }
        return null;
    }

    public static String toString(Week week) {
        return switch (week) {
            case MONDAY -> Data.lsp.lsb("Monday").get();
            case TUESDAY -> Data.lsp.lsb("Monday").get();
            case WEDNESDAY -> "Wednesday";
            case THURSDAY -> "Thursday";
            case FRIDAY -> "Friday";
            case SATURDAY -> "Saturday";
            case SUNDAY -> "Sunday";
        };
    }

    @Override
    public String toString() {
        return Week.toString(this);
    }

    public static Week fromInt(int week) {
        return switch (week) {
            case 0 -> MONDAY;
            case 1 -> TUESDAY;
            case 2 -> WEDNESDAY;
            case 3 -> THURSDAY;
            case 4 -> FRIDAY;
            case 5 -> SATURDAY;
            case 6 -> SUNDAY;
            default -> null;
        };
    }
}

package org.wip.moneymanager.model.types;

public enum Week {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static Week fromString(String week) {
        week = week.toLowerCase();
        return switch (week) {
            case "monday" -> MONDAY;
            case "tuesday" -> TUESDAY;
            case "wednesday" -> WEDNESDAY;
            case "thursday" -> THURSDAY;
            case "friday" -> FRIDAY;
            case "saturday" -> SATURDAY;
            case "sunday" -> SUNDAY;
            default -> null;
        };
    }

    public static String toString(Week week) {
        return switch (week) {
            case MONDAY -> "Monday";
            case TUESDAY -> "Tuesday";
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

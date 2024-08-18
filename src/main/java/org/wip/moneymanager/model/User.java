package org.wip.moneymanager.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class User {
    private static final MMDatabase db = MMDatabase.getInstance();
    private final int id;
    private String username;
    private String password_hash;
    private String safe_login;
    private Theme theme;
    private String accent;
    private int home_screen;
    private int week_start;
    private String main_currency;
    private String language;
    private int last_login;

    public User(int id,
                String username,
                String password_hash,
                String safe_login,
                int theme,
                String accent,
                int home_screen,
                int week_start,
                String main_currency,
                String language,
                int last_login) {
        this.id = id;
        this.username = username;
        this.password_hash = password_hash;
        this.safe_login = safe_login;
        this.theme = Theme.fromInt(theme);
        this.accent = accent;
        this.home_screen = home_screen;
        this.week_start = week_start;
        this.main_currency = main_currency;
        this.language = language;
        this.last_login = last_login;
    }

    public User(ResultSet rs) throws SQLException {
        this(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("safe_login"),
                rs.getInt("theme"),
                rs.getString("accent"),
                rs.getInt("home_screen"),
                rs.getInt("week_start"),
                rs.getString("main_currency"),
                rs.getString("language"),
                rs.getInt("last_login")
        );
    }

    public int id() {
        return id;
    }

    public String username() {
        return username;
    }

    public void setUsername(String username) throws SQLException {
        this.username = username;
        updateField("username", username);
    }

    public String password_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) throws SQLException {
        this.password_hash = password_hash;
        updateField("password_hash", password_hash);
    }

    public String safe_login() {
        return safe_login;
    }

    public void setSafe_login(String safe_login) throws SQLException {
        this.safe_login = safe_login;
        updateField("safe_login", safe_login);
    }

    public Theme theme() {
        return theme;
    }

    public void setTheme(Theme theme) throws SQLException {
        this.theme = theme;
        updateField("theme", theme);
    }

    public String accent() {
        return accent;
    }

    public void setAccent(String accent) throws SQLException {
        this.accent = accent;
        updateField("accent", accent);
    }

    public int home_screen() {
        return home_screen;
    }

    public void setHome_screen(int home_screen) throws SQLException {
        this.home_screen = home_screen;
        updateField("home_screen", home_screen);
    }

    public int week_start() {
        return week_start;
    }

    public void setWeek_start(int week_start) throws SQLException {
        this.week_start = week_start;
        updateField("week_start", week_start);
    }

    public String main_currency() {
        return main_currency;
    }

    public void setMain_currency(String main_currency) throws SQLException {
        this.main_currency = main_currency;
        updateField("main_currency", main_currency);
    }

    public String language() {
        return language;
    }

    public void setLanguage(String language) throws SQLException {
        this.language = language;
        updateField("language", language);
    }

    public int last_login() {
        return last_login;
    }

    public void setLast_login(int last_login) throws SQLException {
        this.last_login = last_login;
        updateField("last_login", last_login);
    }

    private void updateField(String fieldName, Object value) throws SQLException {
        String query = "UPDATE Users SET " + fieldName + " = ? WHERE username = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setObject(1, value);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (User) obj;
        return Objects.equals(this.username, that.username) &&
                Objects.equals(this.password_hash, that.password_hash) &&
                Objects.equals(this.safe_login, that.safe_login) &&
                this.theme == that.theme &&
                Objects.equals(this.accent, that.accent) &&
                this.home_screen == that.home_screen &&
                this.week_start == that.week_start &&
                Objects.equals(this.main_currency, that.main_currency) &&
                Objects.equals(this.language, that.language) &&
                this.last_login == that.last_login;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password_hash, safe_login, theme, accent, home_screen, week_start, main_currency, language, last_login);
    }

    @Override
    public String toString() {
        return "User[" +
                "username=" + username + ", " +
                "password_hash=" + password_hash + ", " +
                "safe_login=" + safe_login + ", " +
                "theme=" + theme + ", " +
                "accent=" + accent + ", " +
                "home_screen=" + home_screen + ", " +
                "week_start=" + week_start + ", " +
                "main_currency=" + main_currency + ", " +
                "language=" + language + ", " +
                "last_login=" + last_login + ']';
    }

}
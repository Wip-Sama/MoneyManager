package org.wip.moneymanager.model.DBObjects;

import javafx.beans.property.*;
import org.wip.moneymanager.model.MMDatabase;
import org.wip.moneymanager.model.types.Color;
import org.wip.moneymanager.model.types.HomeScreen;
import org.wip.moneymanager.model.types.Theme;
import org.wip.moneymanager.model.types.Week;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class dbUser {
    private static final MMDatabase db = MMDatabase.getInstance();
    private final int id;
    private final StringProperty username;
    private final StringProperty password_hash;
    private final BooleanProperty safe_login;
    private final ObjectProperty<Theme> theme;
    private final ObjectProperty<Color> accent;
    private final ObjectProperty<HomeScreen> home_screen;
    private final ObjectProperty<Week> week_start;
    private final StringProperty main_currency;
    private final StringProperty language;
    private final IntegerProperty last_login;

    public dbUser(int id,
                  String username,
                  String password_hash,
                  int safe_login,
                  int theme,
                  String accent,
                  int home_screen,
                  int week_start,
                  String main_currency,
                  String language,
                  int last_login) {
        this.id = id;
        this.username = new SimpleStringProperty(username);
        this.password_hash = new SimpleStringProperty(password_hash);
        this.safe_login = new SimpleBooleanProperty(safe_login == 1);
        this.theme = new SimpleObjectProperty<>(Theme.fromInt(theme));
        this.accent = new SimpleObjectProperty<>(new Color(accent));
        this.home_screen = new SimpleObjectProperty<>(HomeScreen.fromInt(home_screen)); // -> string -> Object
        this.week_start = new SimpleObjectProperty<>(Week.fromInt(week_start)); // -> string -> Object
        this.main_currency = new SimpleStringProperty(main_currency); // -> Object
        this.language = new SimpleStringProperty(language); // -> Object // Forse no
        this.last_login = new SimpleIntegerProperty(last_login); // Qualcosa che indichi la data???
        // Salvare le cose come intero lo rende molto difficile da portare se non mettiamo un versioning al db
    }

    public dbUser(ResultSet rs) throws SQLException {
        this(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getInt("safe_login"),
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

    public ReadOnlyStringProperty username() {
        return username;
    }

    public ReadOnlyStringProperty password_hash() {
        return password_hash;
    }

    public ReadOnlyBooleanProperty safe_login() {
        return safe_login;
    }

    public ReadOnlyObjectProperty<Theme> themeProperty() {
        return theme;
    }

    public ReadOnlyObjectProperty<Color> accentProperty() {
        return accent;
    }

    public ReadOnlyObjectProperty<HomeScreen> home_screenProperty() {
        return home_screen;
    }

    public ReadOnlyObjectProperty<Week> week_startProperty() {
        return week_start;
    }

    public ReadOnlyStringProperty main_currencyProperty() {
        return main_currency;
    }

    public ReadOnlyStringProperty languageProperty() {
        return language;
    }

    public ReadOnlyIntegerProperty last_loginProperty() {
        return last_login;
    }

    public void setUsername(String username) throws SQLException {
        updateField("username", username);
        this.username.set(username);
    }

    public void setPassword_hash(String password_hash) throws SQLException {
        this.password_hash.set(password_hash);
        updateField("password_hash", password_hash);
    }

    public void setSafe_login(boolean safe_login) throws SQLException {
        this.safe_login.set(safe_login);
        updateField("safe_login", safe_login ? 1 : 0);
    }

    public void setTheme(Theme theme) throws SQLException {
        this.theme.set(theme);
        updateField("theme", theme.ordinal());
    }

    public void setAccent(String accent) throws SQLException {
        setAccent(new Color(accent));
    }

    public void setAccent(int[] rgb) throws SQLException {
        setAccent(new Color(rgb));
    }

    public void setAccent(Color accent) throws SQLException {
        this.accent.set(accent);
        updateField("accent", accent.getHex());
    }

    public void setHome_screen(HomeScreen home_screen) throws SQLException {
        this.home_screen.set(home_screen);
        updateField("home_screen", home_screen.ordinal());
    }

    public void setWeek_start(Week week_start) throws SQLException {
        this.week_start.set(week_start);
        updateField("week_start", week_start.ordinal());
    }

    public void setMain_currency(String main_currency) throws SQLException {
        this.main_currency.set(main_currency);
        updateField("main_currency", main_currency);
    }

    public void setLanguage(String language) throws SQLException {
        this.language.set(language);
        updateField("language", language);
    }

    public void setLast_login(int last_login) throws SQLException {
        this.last_login.set(last_login);
        updateField("last_login", last_login);
    }

    private void updateField(String fieldName, Object value) throws SQLException {
        String query = "UPDATE Users SET " + fieldName + " = ? WHERE username = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setObject(1, value);
            stmt.setString(2, username.get());
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (dbUser) obj;
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
        return "dbUser[" +
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
package org.wip.moneymanager.model.DBObjects;

import javafx.beans.property.*;
import org.wip.moneymanager.model.UserDatabase;
import org.wip.moneymanager.model.types.AccountType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class dbAccount {
    private final UserDatabase db;
    private final int id;
    private final StringProperty name;
    private final ObjectProperty<AccountType> type;
    private final DoubleProperty balance;
    private final IntegerProperty creation_date;
    private final IntegerProperty include_into_totals;
    private final StringProperty currency;

    public dbAccount(int id, String name, int type, double balance, int creation_date, int include_into_totals, String currency, UserDatabase db) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleObjectProperty<>(AccountType.fromInt(type));
        this.balance = new SimpleDoubleProperty(balance);
        this.creation_date = new SimpleIntegerProperty(creation_date);
        this.include_into_totals = new SimpleIntegerProperty(include_into_totals);
        this.currency = new SimpleStringProperty(currency);
        this.db = db;
    }

    public dbAccount(ResultSet rs, UserDatabase db) throws Exception {
        this(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("type"),
                rs.getDouble("balance"),
                rs.getInt("creation_date"),
                rs.getInt("include_into_totals"),
                rs.getString("currency"),
                db
        );
    }

    public int id() {
        return id;
    }

    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    public ReadOnlyObjectProperty<AccountType> typeProperty() {
        return type;
    }

    public ReadOnlyDoubleProperty balanceProperty() {
        return balance;
    }

    public ReadOnlyIntegerProperty creationDateProperty() {
        return creation_date;
    }

    public ReadOnlyIntegerProperty includeIntoTotalsProperty() {
        return include_into_totals;
    }

    public ReadOnlyStringProperty currencyProperty() {
        return currency;
    }

    public void setName(String name) throws SQLException {
        this.name.set(name);
        updateField("name", name);
    }

    public void setType(AccountType type) throws SQLException {
        this.type.set(type);
        updateField("type", type.ordinal());
    }

    public void setBalance(double balance) throws SQLException {
        this.balance.set(balance);
        updateField("balance", balance);
    }

    public void setCreationDate(int creation_date) throws SQLException {
        this.creation_date.set(creation_date);
        updateField("creation_date", creation_date);
    }

    public void setIncludeIntoTotals(int include_into_totals) throws SQLException {
        this.include_into_totals.set(include_into_totals);
        updateField("include_into_totals", include_into_totals);
    }

    public void setCurrency(String currency) throws SQLException {
        this.currency.set(currency);
        updateField("currency", currency);
    }

    private void updateField(String fieldName, Object value) throws SQLException {
        String query = "UPDATE Accounts SET " + fieldName + " = ? WHERE id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setObject(1, value);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (dbAccount) obj;
        return this.id == that.id &&
                Objects.equals(this.name.get(), that.name.get()) &&
                this.type.get() == that.type.get() &&
                Double.doubleToLongBits(this.balance.get()) == Double.doubleToLongBits(that.balance.get()) &&
                this.creation_date.get() == that.creation_date.get() &&
                this.include_into_totals.get() == that.include_into_totals.get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name.get(), type.get(), balance.get(), creation_date.get(), include_into_totals.get());
    }

    @Override
    public String toString() {
        return "dbAccount[" +
                "id=" + id + ", " +
                "name=" + name.get() + ", " +
                "type=" + type.get() + ", " +
                "balance=" + balance.get() + ", " +
                "creation_date=" + creation_date.get() + ", " +
                "include_into_totals=" + include_into_totals.get() + ']';
    }
}
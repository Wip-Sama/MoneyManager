package org.wip.moneymanager.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class Account {
    private final UserDatabase db;
    private final int id;
    private String name;
    private int type;
    private double balance;
    private int creation_date;
    private int include_into_totals;

    public Account(int id, String name, int type, double balance, int creation_date, int include_into_totals, UserDatabase db) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.creation_date = creation_date;
        this.include_into_totals = include_into_totals;
        this.db = db;
    }

    public Account(ResultSet rs, UserDatabase db) throws Exception {
        this(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("type"),
            rs.getDouble("balance"),
            rs.getInt("creation_date"),
            rs.getInt("include_into_totals"),
            db
        );
        /*
        Si potrebbe fare anche così ma mi piace l'idea di tenere il minor numero di costruttori primitivi
        Non mi piace la ripetizione del codice se è così facile da evitare
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.type = rs.getInt("type");
        this.balance = rs.getDouble("balance");
        this.creation_date = rs.getInt("creation_date");
        this.include_into_totals = rs.getInt("include_into_totals");
        this.db = db;
        */
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public int type() {
        return type;
    }

    public double balance() {
        return balance;
    }

    public int creation_date() {
        return creation_date;
    }

    public int include_into_totals() {
        return include_into_totals;
    }

    public void setName(String name) throws SQLException {
        this.name = name;
        updateField("name", name);
    }

    public void setType(int type) throws SQLException {
        this.type = type;
        updateField("type", type);
    }

    public void setBalance(double balance) throws SQLException {
        this.balance = balance;
        updateField("balance", balance);
    }

    public void setCreationDate(int creation_date) throws SQLException {
        this.creation_date = creation_date;
        updateField("creation_date", creation_date);
    }

    public void setIncludeIntoTotals(int include_into_totals) throws SQLException {
        this.include_into_totals = include_into_totals;
        updateField("include_into_totals", include_into_totals);
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
        var that = (Account) obj;
        return this.id == that.id &&
                Objects.equals(this.name, that.name) &&
                this.type == that.type &&
                Double.doubleToLongBits(this.balance) == Double.doubleToLongBits(that.balance) &&
                this.creation_date == that.creation_date &&
                this.include_into_totals == that.include_into_totals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, balance, creation_date, include_into_totals);
    }

    @Override
    public String toString() {
        return "Account[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "type=" + type + ", " +
                "balance=" + balance + ", " +
                "creation_date=" + creation_date + ", " +
                "include_into_totals=" + include_into_totals + ']';
    }

    public enum type {
        CASH,
        BANK,
        CREDIT_CARD,
        DEBIT_CARD,
        PREPAID_CARD,
        OTHER
    }
}

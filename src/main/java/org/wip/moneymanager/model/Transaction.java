package org.wip.moneymanager.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class Transaction {
    private final UserDatabase db;
    private final int id;
    private int date;
    private int type;
    private double amount;
    private int account;
    private int second_account;
    private String note;
    private int fauvorite;

    public Transaction(int id, int date, int type, double amount, int account, int second_account, String note, int fauvorite, UserDatabase db) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.account = account;
        this.second_account = second_account;
        this.note = note;
        this.fauvorite = fauvorite;
        this.db = db;
    }

    public Transaction(ResultSet rs, UserDatabase db) throws SQLException {
        this(
                rs.getInt("id"),
                rs.getInt("date"),
                rs.getInt("type"),
                rs.getDouble("amount"),
                rs.getInt("account"),
                rs.getInt("second_account"),
                rs.getString("note"),
                rs.getInt("fauvorite"),
                db
        );
    }

    public int id() {
        return id;
    }

    public int date() {
        return date;
    }

    public int type() {
        return type;
    }

    public double amount() {
        return amount;
    }

    public int account() {
        return account;
    }

    public int second_account() {
        return second_account;
    }

    public String note() {
        return note;
    }

    public int fauvorite() {
        return fauvorite;
    }

    public void setDate(int date) throws SQLException {
        this.date = date;
        updateField("date", date);
    }

    public void setType(int type) throws SQLException {
        this.type = type;
        updateField("type", type);
    }

    public void setAmount(double amount) throws SQLException {
        this.amount = amount;
        updateField("amount", amount);
    }

    public void setAccount(int account) throws SQLException {
        this.account = account;
        updateField("account", account);
    }

    public void setSecondAccount(int second_account) throws SQLException {
        this.second_account = second_account;
        updateField("second_account", second_account);
    }

    public void setNote(String note) throws SQLException {
        this.note = note;
        updateField("note", note);
    }

    public void setFauvorite(int fauvorite) throws SQLException {
        this.fauvorite = fauvorite;
        updateField("fauvorite", fauvorite);
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
        var that = (Transaction) obj;
        return this.id == that.id &&
                this.date == that.date &&
                this.type == that.type &&
                Double.doubleToLongBits(this.amount) == Double.doubleToLongBits(that.amount) &&
                this.account == that.account &&
                this.second_account == that.second_account &&
                Objects.equals(this.note, that.note) &&
                this.fauvorite == that.fauvorite;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, type, amount, account, second_account, note, fauvorite);
    }

    @Override
    public String toString() {
        return "Transaction[" +
                "id=" + id + ", " +
                "date=" + date + ", " +
                "type=" + type + ", " +
                "amount=" + amount + ", " +
                "account=" + account + ", " +
                "second_account=" + second_account + ", " +
                "note=" + note + ", " +
                "fauvorite=" + fauvorite + ']';
    }

}

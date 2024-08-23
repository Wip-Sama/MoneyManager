package org.wip.moneymanager.model.DBObjects;

import org.wip.moneymanager.model.MMDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class Currency implements Comparable<Currency>{
    private static final MMDatabase db = MMDatabase.getInstance();
    private final String name;
    private double value;
    private int update_date;

    public Currency(String name, double value, int update_date) {
        this.name = name;
        this.value = value;
        this.update_date = update_date;
    }

    public Currency(ResultSet rs) throws SQLException {
        this(
                rs.getString("name"),
                rs.getDouble("value"),
                rs.getInt("update_date")
        );
    }

    public String name() {
        return name;
    }

    public double value() {
        return value;
    }

    public void setValue(double value) throws SQLException {
        this.value = value;
        updateField("value", value);
    }

    public int update_date() {
        return update_date;
    }

    public void setUpdate_date() throws SQLException {
        this.update_date = (int) (System.currentTimeMillis() / 1000);
        updateField("update_date", update_date);
    }
    public void setUpdate_date(int update_date) throws SQLException {
        this.update_date = update_date;
        updateField("update_date", update_date);
    }

    private void updateField(String fieldName, Object value) throws SQLException {
        if (fieldName.equals("name")) {
            throw new IllegalArgumentException("Cannot update primary key");
        }

        String query = "UPDATE Currency SET " + fieldName + " = ? WHERE name = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setObject(1, value);
            stmt.setString(2, name);
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Currency) obj;
        return Objects.equals(this.name, that.name) &&
                Double.doubleToLongBits(this.value) == Double.doubleToLongBits(that.value) &&
                Objects.equals(this.update_date, that.update_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, update_date);
    }

    @Override
    public String toString() {
        return "Currency[" +
                "name=" + name + ", " +
                "value=" + value + ", " +
                "update_date=" + update_date + ']';
    }

    @Override
    public int compareTo(Currency other) {
        return this.name.compareTo(other.name);
    }
}
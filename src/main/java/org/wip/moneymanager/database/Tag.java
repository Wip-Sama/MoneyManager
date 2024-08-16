package org.wip.moneymanager.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class Tag {
    private final UserDatabase db;
    private final int id;
    private String name;
    private String color;

    public Tag(int id, String name, String color, UserDatabase db) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.db = db;
    }

    public Tag(ResultSet rs, UserDatabase db) throws SQLException {
        this(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("color"),
                db
        );
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public void setName(String name) throws SQLException {
        this.name = name;
        updateField("name", color);
    }

    public String color() {
        return color;
    }

    public void setColor(String color) throws SQLException {
        this.color = color;
        updateField("color", color);
    }

    private void updateField(String fieldName, Object value) throws SQLException {
        String query = "UPDATE Tag SET " + fieldName + " = ? WHERE id = ?";
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
        var that = (Tag) obj;
        return this.id == that.id &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Tag[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "color=" + color + ']';
    }

}

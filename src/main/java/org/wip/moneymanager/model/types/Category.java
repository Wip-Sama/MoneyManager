package org.wip.moneymanager.model.types;

import org.wip.moneymanager.model.UserDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class Category {
    private final UserDatabase db;
    private final int id;
    private int type;
    private String name;
    private int parent_category;

    public Category(int id, int type, String name, int parent_category, UserDatabase db) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.parent_category = parent_category;
        this.db = db;
    }

    public Category(ResultSet rs, UserDatabase db) throws Exception {
        this(
            rs.getInt("id"),
            rs.getInt("type"),
            rs.getString("name"),
            rs.getInt("parent_category"),
            db
        );
    }

    public int id() {
        return id;
    }

    public int type() {
        return type;
    }

    public String name() {
        return name;
    }

    public int parent_category() {
        return parent_category;
    }

    public void setType(int type) throws SQLException {
        updateField("type", type);
        this.type = type;
    }

    public void setName(String name) throws SQLException {
        updateField("name", name);
        this.name = name;
    }

    public void setParentCategory(int parent_category) throws SQLException {
        updateField("parent_category", parent_category);
        this.parent_category = parent_category;
    }

    private void updateField(String fieldName, Object value) throws SQLException {
        String query = "UPDATE Categories SET " + fieldName + " = ? WHERE id = ?";
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
        var that = (Category) obj;
        return this.id == that.id &&
                this.type == that.type &&
                Objects.equals(this.name, that.name) &&
                this.parent_category == that.parent_category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, name, parent_category);
    }

    @Override
    public String toString() {
        return "Category[" +
                "id=" + id + ", " +
                "type=" + type + ", " +
                "name=" + name + ", " +
                "parent_category=" + parent_category + ']';
    }

}

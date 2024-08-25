package org.wip.moneymanager.model.DBObjects;

import javafx.beans.property.*;
import org.wip.moneymanager.model.UserDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class dbCategory {
    private final UserDatabase db;
    private final int id;
    private IntegerProperty type;
    private StringProperty name;
    private IntegerProperty parent_category;

    public dbCategory(int id, int type, String name, int parent_category, UserDatabase db) {
        this.id = id;
        this.type = new SimpleIntegerProperty(type);
        this.name = new SimpleStringProperty(name);
        this.parent_category = new SimpleIntegerProperty(parent_category);
        this.db = db;
    }

    public dbCategory(ResultSet rs, UserDatabase db) throws Exception {
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

    public ReadOnlyIntegerProperty type() {
        return type;
    }

    public ReadOnlyStringProperty name() {
        return name;
    }

    public ReadOnlyIntegerProperty parent_category() {
        return parent_category;
    }

    public void setType(int type) throws SQLException {
        updateField("type", type);
        this.type.set(type);
    }

    public void setName(String name) throws SQLException {
        updateField("name", name);
        this.name.set(name);
    }

    public void setParentCategory(int parent_category) throws SQLException {
        updateField("parent_category", parent_category);
        this.parent_category.set(parent_category);
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
        var that = (dbCategory) obj;
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
        return "dbCategory[" +
                "id=" + id + ", " +
                "type=" + type + ", " +
                "name=" + name + ", " +
                "parent_category=" + parent_category + ']';
    }
}

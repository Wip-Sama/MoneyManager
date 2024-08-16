package org.wip.moneymanager.database;

import javafx.concurrent.Task;
import org.wip.moneymanager.utility.Encrypter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDatabase extends Database {
    private UserDatabase instance = null;
    private String username;

    public UserDatabase getInstance() {
        if (instance == null) {
            instance = new UserDatabase(username);
        }
        return instance;
    }

    public UserDatabase(String username) {
        super(username+".db");
        this.username = username;
    }

    public Task<Boolean> createTag(String name, String color) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "INSERT INTO Tag (name, color) VALUES (?, ?);";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, color);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> removeTag(String name) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Tag WHERE name=?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> removeAllTag() {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Tag;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Tag> getTag(String name) {
        return asyncCall(() -> {
            Tag tag = null;
            if (isConnected()) {
                String query = "SELECT * FROM Tag WHERE name = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    tag= new Tag(rs, this);
                };
                stmt.close();
            }
            return tag;
        });
    }

    public Task<List<Tag>> getAllTag() {
        return asyncCall(() -> {
            List<Tag> tags = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Tag;";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tags.add(new Tag(rs, this));
                };
                stmt.close();
            }
            return tags;
        });
    }

    public Task<Boolean> createTransaction_tags(int transaction, int tag) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "INSERT INTO Transaction_tags (transaction, tag) VALUES (?, ?);";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, transaction);
                stmt.setInt(2, tag);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> removeTransaction_tags(int transaction, int tag) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Transaction_tags WHERE 'transaction' = ? AND tag = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, transaction);
                stmt.setInt(1, tag);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> removeAllTransaction_tags() {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Transaction_tags;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<List<Transaction_tags>> getAllTransaction_tags_per_transaction(int transaction) {
        return asyncCall(() -> {
            List<Transaction_tags> tt = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Transaction_tags WHERE 'transaction' = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, transaction);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    tt.add(new Transaction_tags(rs, this));
                };
                stmt.close();
            }
            return tt;
        });
    }

    public Task<List<Transaction_tags>> getAllTransaction_tags() {
        return asyncCall(() -> {
            List<Transaction_tags> tt = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Transaction_tags;";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tt.add(new Transaction_tags(rs, this));
                };
                stmt.close();
            }
            return tt;
        });
    }
}

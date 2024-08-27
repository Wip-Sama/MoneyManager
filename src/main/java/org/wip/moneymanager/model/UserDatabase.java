package org.wip.moneymanager.model;

import javafx.concurrent.Task;
import org.wip.moneymanager.model.DBObjects.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDatabase extends Database {
    private static UserDatabase instance = null;

    public static UserDatabase getInstance() {
        if (instance == null) {
            instance = new UserDatabase();
        }
        return instance;
    }

    public UserDatabase() {
        super("data/user_dbs/"+Data.username+".db");
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
                // WHERE id != -1 non serve ma altrimenti intellij eprnede che la query pulisce il db come un errore
                String query = "DELETE FROM Tag WHERE id != -1;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<dbTag> getTag(String name) {
        return asyncCall(() -> {
            dbTag dbTag = null;
            if (isConnected()) {
                String query = "SELECT * FROM Tag WHERE name = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dbTag = new dbTag(rs, this);
                };
                stmt.close();
            }
            return dbTag;
        });
    }

    public Task<List<dbTag>> getAllTag() {
        return asyncCall(() -> {
            List<dbTag> dbTags = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Tag;";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    dbTags.add(new dbTag(rs, this));
                };
                stmt.close();
            }
            return dbTags;
        });
    }

    public Task<List<dbTransaction>> getAlltransactionBetween(int start, int end) {
        return asyncCall(() -> {
            List<dbTransaction> dbTransactions = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Transactions WHERE date BETWEEN ? AND ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, start);
                stmt.setInt(2, end);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    dbTransactions.add(new dbTransaction(rs, this));
                };
                stmt.close();
            }
            return dbTransactions;
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

    public Task<List<dbTransaction_tags>> getAllTransaction_tags_per_transaction(int transaction) {
        return asyncCall(() -> {
            List<dbTransaction_tags> tt = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Transaction_tags WHERE 'transaction' = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, transaction);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    tt.add(new dbTransaction_tags(rs, this));
                };
                stmt.close();
            }
            return tt;
        });
    }

    public Task<List<dbTransaction_tags>> getAllTransaction_tags() {
        return asyncCall(() -> {
            List<dbTransaction_tags> tt = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Transaction_tags;";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tt.add(new dbTransaction_tags(rs, this));
                };
                stmt.close();
            }
            return tt;
        });
    }

    public Task<List<dbCategory>> getAllCategories(int type) {
        return asyncCall(() -> {
            List<dbCategory> categories = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Categories WHERE type == ? AND parent_category is null;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, type);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    categories.add(new dbCategory(rs, this));
                };
                stmt.close();
            }
            return categories;
        });
    }

    public Task<dbCategory> getCategory(int id) {
        return asyncCall(() -> {
            dbCategory dbCategory = null;
            if (isConnected()) {
                String query = "SELECT * FROM Categories WHERE id = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dbCategory = new dbCategory(rs, this);
                };
                stmt.close();
            }
            return dbCategory;
        });
    }

    public Task<dbCategory> getCategory(String name, int type) {
        return asyncCall(() -> {
            dbCategory dbCategory = null;
            if (isConnected()) {
                String query = "SELECT * FROM Categories WHERE name = ? AND type = ? AND parent_category is null;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setInt(2, type);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dbCategory = new dbCategory(rs, this);
                };
                stmt.close();
            }
            return dbCategory;
        });
    }

    public Task<dbCategory> getSubcategory(String name, int type, int parent_category) {
        return asyncCall(() -> {
            dbCategory dbCategory = null;
            if (isConnected()) {
                String query = "SELECT * FROM Categories WHERE name = ? AND type = ? AND parent_category = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setInt(2, type);
                stmt.setInt(3, parent_category);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dbCategory = new dbCategory(rs, this);
                };
                stmt.close();
            }
            return dbCategory;
        });
    }

    public Task<Boolean> createCategory(String name, int type) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "INSERT INTO Categories (name, type) VALUES (?, ?);";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setInt(2, type);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> createSubcategory(String name, int type, int parent_category) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "INSERT INTO Categories (name, type, parent_category) VALUES (?, ?, ?);";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setInt(2, type);
                stmt.setInt(3, parent_category);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> forceRemoveCategory(int id) {
        return asyncCall(() -> {
            if (isConnected()) {
                String remove_from_transactions = "UPDATE Transactions SET category = null WHERE category = ?;";
                PreparedStatement stmt1 = con.prepareStatement(remove_from_transactions);
                stmt1.setInt(1, id);
                stmt1.executeUpdate();
                stmt1.close();

                Task<List<dbCategory>> subc = getAllSubcategories(id);
                subc.run();
                subc.get().forEach(sub -> forceRemoveCategory(sub.id()));

                String remove_subcategory = "DELETE FROM Categories WHERE parent_category = ?;";
                PreparedStatement stmt2 = con.prepareStatement(remove_subcategory);
                stmt2.setInt(1, id);
                stmt2.executeUpdate();
                stmt2.close();

                String remove_category = "DELETE FROM Categories WHERE id = ?;";
                PreparedStatement stmt3 = con.prepareStatement(remove_category);
                stmt3.setInt(1, id);
                stmt3.executeUpdate();
                stmt3.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> removeCategory(int id) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Categories WHERE id = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, id);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> removeAllSubcategories(int parent_category) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Categories WHERE parent_category = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, parent_category);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<List<dbCategory>> getAllSubcategories(int parent_category) {
        return asyncCall(() -> {
            List<dbCategory> categories = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Categories WHERE parent_category = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, parent_category);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    categories.add(new dbCategory(rs, this));
                };
                stmt.close();
            }
            return categories;
        });
    }

    public Task<List<dbAccount>> getAllAccounts() {
        return asyncCall(() -> {
            List<dbAccount> accounts = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Accounts;";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    accounts.add(new dbAccount(rs, this));
                };
                stmt.close();
            }
            return accounts;
        });
    }

    public Task<dbAccount> getAccount(int id) {
        return asyncCall(() -> {
            dbAccount dbAccount = null;
            if (isConnected()) {
                String query = "SELECT * FROM Accounts WHERE id = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dbAccount = new dbAccount(rs, this);
                };
                stmt.close();
            }
            return dbAccount;
        });
    }

    public Task<Boolean> removeAccount(int id) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Accounts WHERE id = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, id);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> forceRemoveAccount(int id) {
        return asyncCall(() -> {
            if (isConnected()) {
                String remove_from_transactions = "DELETE FROM Transactions WHERE account = ? OR second_account = ?;";
                PreparedStatement stmt1 = con.prepareStatement(remove_from_transactions);
                stmt1.setInt(1, id);
                stmt1.setInt(2, id);
                stmt1.executeUpdate();
                stmt1.close();

                String remove_account = "DELETE FROM Accounts WHERE id = ?;";
                PreparedStatement stmt2 = con.prepareStatement(remove_account);
                stmt2.setInt(1, id);
                stmt2.executeUpdate();
                stmt2.close();
                return true;
            }
            return false;
        });
    }
}
